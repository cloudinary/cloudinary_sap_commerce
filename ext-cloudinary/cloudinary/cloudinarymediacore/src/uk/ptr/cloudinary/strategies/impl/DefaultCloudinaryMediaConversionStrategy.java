package uk.ptr.cloudinary.strategies.impl;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.conversion.ConvertedMediaCreationStrategy;
import de.hybris.platform.mediaconversion.conversion.MediaConversionException;
import de.hybris.platform.mediaconversion.conversion.MediaConversionStrategy;
import de.hybris.platform.mediaconversion.imagemagick.ImageMagickMediaConversionStrategy;
import de.hybris.platform.mediaconversion.imagemagick.ImageMagickService;
import de.hybris.platform.mediaconversion.imagemagick.MimeMappingStrategy;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.media.NoDataAvailableException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import uk.ptr.cloudinary.strategies.CloudinaryConvertedMediaCreationStrategy;
import uk.ptr.cloudinary.strategies.CloudinaryMediaConversionStrategy;


public class DefaultCloudinaryMediaConversionStrategy implements CloudinaryMediaConversionStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultCloudinaryMediaConversionStrategy.class);

	private static final Pattern FILEEXTENSION_PATTERN = Pattern.compile("\\.([a-z]+)(?:[?;].*)?$", 2);
	private File tmpDir;

	@Resource
	private MimeMappingStrategy mimeMappingStrategy;

	@Resource
	private CloudinaryConvertedMediaCreationStrategy cloudinaryConvertedMediaCreationStrategy;

	@Resource
	private MediaService mediaService;


	@Override
	public MediaModel convert(MediaConversionService mediaConversionService, MediaModel input, MediaFormatModel format) throws MediaConversionException {
		ServicesUtil.validateParameterNotNull(mediaConversionService, "MediaConversionService must not be null.");
		ServicesUtil.validateParameterNotNull(input, "Input media model must not be null.");
		ServicesUtil.validateParameterNotNull(format, "Conversion media format model must not be null.");

		try {
			String fileExt = this.targetFileExtension(format, input);
			File tmpFile = File.createTempFile("tmp_", "." + fileExt, getTmpDir());

			MediaModel var7;
			try {
				//convert(mediaConversionService, input, tmpFile, format);
				var7 = create(input, format, tmpFile);
			} finally {
				if (!tmpFile.delete()) {
					LOG.warn("Failed to delete temporary file '" + tmpFile + "'.");
				}

			}

			return var7;
		} catch (IOException var11) {
			throw new MediaConversionException("Failed to run cloudinary.", var11);
		}
	}

	public File getTmpDir() {
		if (this.tmpDir == null) {
			this.tmpDir = new File(System.getProperty("HYBRIS_TEMP_DIR", System.getProperty("java.io.tmpdir")), "convert");
			LOG.info("Using tmp dir '" + this.tmpDir.getAbsolutePath() + "'.");
		}

		if (!this.tmpDir.isDirectory() && !this.tmpDir.mkdirs()) {
			LOG.warn("Failed to create tmp directory '" + this.tmpDir.getAbsolutePath() + "'.");
		}

		return this.tmpDir;
	}

	private MediaModel create(MediaModel input, MediaFormatModel format, File tmpFile) {
		try {
			FileInputStream inputStream = new FileInputStream(tmpFile);

			MediaModel var6;
			try {
				var6 = this.getCloudinaryConvertedMediaCreationStrategy().createOrUpdate(input, format, inputStream);
			} finally {
				try {
					inputStream.close();
				} catch (IOException var12) {
					LOG.warn("Failed to close input stream on '" + tmpFile.getAbsolutePath() + "'.", var12);
				}

			}

			return var6;
		} catch (FileNotFoundException var14) {
			throw new MediaIOException("The specified file '" + tmpFile + "' could not be found.", var14);
		}
	}

	protected void convert(MediaConversionService mediaConversionService, MediaModel input, File target, ConversionMediaFormatModel format) throws IOException {
		//Logic to convert formats


	}

	protected String targetFileExtension(MediaFormatModel format, MediaModel input) throws IOException, MediaConversionException {
		String ret = this.targetFileExtension(format);
		if (ret == null && input.getMime() != null) {
			ret = this.getMimeMappingStrategy().fileExtensionForMimeType(input.getMime());
		}

		if (ret == null) {
			ret = extractFileExtension(input.getURL());
		}

		if (ret == null) {
			throw new MediaConversionException("Target file extension could neither be computed from input's mime '" + input.getMime() + "' nor url '" + input.getURL() + "'.");
		} else {
			return ret;
		}
	}

	static String extractFileExtension(String url) {
		if (url == null) {
			return null;
		} else {
			Matcher matcher = FILEEXTENSION_PATTERN.matcher(url);
			return matcher.find() ? matcher.group(1) : null;
		}
	}

	private String targetFileExtension(MediaFormatModel format) throws IOException, MediaConversionException {
		String mime = format.getMediaMimeType();
		if (mime == null) {
			return null;
		} else {
			String ret = this.getMimeMappingStrategy().fileExtensionForMimeType(mime);
			if (ret == null) {
				throw new MediaConversionException("No file extension could be found for mime type '" + mime + "'.");
			} else {
				return ret;
			}
		}
	}


	public File retrieveFile(MediaModel media) throws IOException
	{
		NoDataAvailableException cause = null;

		try {
			Collection<File> files = this.getMediaService().getFiles(media);
			Iterator var5 = files.iterator();

			while(var5.hasNext()) {
				File f = (File)var5.next();
				if (f.isFile() && f.canRead()) {
					return f;
				}
			}
		} catch (NoDataAvailableException var6) {
			cause = var6;
		}

		throw new IOException("Cannot access media '" + media + "'. Data is not locally available.", cause);
	}

	public MimeMappingStrategy getMimeMappingStrategy()
	{
		return mimeMappingStrategy;
	}

	public CloudinaryConvertedMediaCreationStrategy getCloudinaryConvertedMediaCreationStrategy()
	{
		return cloudinaryConvertedMediaCreationStrategy;
	}

	public MediaService getMediaService()
	{
		return mediaService;
	}
}
