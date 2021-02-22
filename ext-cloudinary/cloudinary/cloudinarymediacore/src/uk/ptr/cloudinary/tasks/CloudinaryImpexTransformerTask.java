package uk.ptr.cloudinary.tasks;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.ImpexTransformerTask;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import uk.ptr.cloudinary.cronjob.CloudinaryMediaTagUpdateJob;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.UpdateTagApiService;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudinaryImpexTransformerTask extends ImpexTransformerTask {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryImpexTransformerTask.class);

    private String encoding = CSVConstants.HYBRIS_ENCODING;

    @Resource
    CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    UpdateTagApiService updateTagApiService;

    @Override
    public BatchHeader execute(final BatchHeader header) throws UnsupportedEncodingException, FileNotFoundException
    {
        Assert.notNull(header);
        Assert.notNull(header.getFile());
        final File file = header.getFile();
        header.setEncoding(encoding);
        final List<ImpexConverter> converters = getConverters(file);
        int position = 1;
        for (final ImpexConverter converter : converters)
        {
            final File impexFile = getImpexFile(file, position++);
            if (convertFile(header, file, impexFile, converter))
            {
                header.addTransformedFile(impexFile);
            }
            else
            {
                super.getCleanupHelper().cleanupFile(impexFile);
            }
        }
        return header;
    }

    protected boolean convertFile(final BatchHeader header, final File file, final File impexFile, final ImpexConverter converter)
            throws UnsupportedEncodingException, FileNotFoundException
    {
        boolean result = false;
        CSVReader csvReader = null;
        PrintWriter writer = null;
        PrintWriter errorWriter = null;
        OutputStream impexOutputStream = null;
        Map<String, Map<String, String>> publicIdMap = new HashMap<>();

        try		// NOSONAR
        {
            csvReader = createCsvReader(file);
            impexOutputStream = new FileOutputStream(impexFile);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(impexOutputStream, encoding)));
            writer.println(getReplacedHeader(header, converter));
            while (csvReader.readNextLine())
            {
                final Map<Integer, String> row = csvReader.getLine();

                Map<String,String> productCodeMap = new HashMap<String,String>();
                //Key - product code, Value - Resource type
                productCodeMap.put(row.get(6),row.get(3));
                //Key - public id, Value - map of product code,resource type
                publicIdMap.put(row.get(2), productCodeMap);
                if (converter.filter(row))
                {
                    try
                    {
                        writer.println(converter.convert(row, header.getSequenceId()));
                        result = true;
                    }
                    catch (final IllegalArgumentException exc)
                    {
                        errorWriter = writeErrorLine(file, csvReader, errorWriter, exc);
                    }
                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(writer);
            IOUtils.closeQuietly(errorWriter);
            IOUtils.closeQuietly(impexOutputStream);
            closeQuietly(csvReader);
            updateTagOnMedia(publicIdMap);
        }
        return result;
    }

    private void updateTagOnMedia(Map<String, Map<String, String>> publicIdMap) {
        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();
        try {
            for (Map.Entry<String, Map<String, String>> entry : publicIdMap.entrySet()) {
                String productCode = entry.getValue().entrySet().iterator().next().getKey();
                String resourceType = entry.getValue().entrySet().iterator().next().getValue();
                updateTagApiService.updateTagOnAsests(entry.getKey(), productCode, cloudinaryConfigModel.getCloudinaryURL(),resourceType);
            }
        } catch (IOException e) {
            LOG.error("Error occurred while updating tag for Media ", e);
        }
    }
}
