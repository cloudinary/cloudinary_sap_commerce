package uk.ptr.cloudinary.strategies.impl;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import uk.ptr.cloudinary.service.TransformationApiService;
import uk.ptr.cloudinary.strategies.CloudinaryConvertedMediaCreationStrategy;


public class DefaultCloudinaryConvertedMediaCreationStrategy implements CloudinaryConvertedMediaCreationStrategy
{

	private static final Logger LOG = Logger.getLogger(DefaultCloudinaryConvertedMediaCreationStrategy.class);

	@Resource
	private ModelService modelService;

	@Resource
	private MediaService mediaService;

	@Resource
	private TransformationApiService transformationApiService;

	@Override
	public MediaModel createOrUpdate(MediaModel parent, MediaFormatModel format, InputStream content) throws
			MediaIOException
	{
		MediaModel dmm;
		try {
			dmm = this.getMediaService().getMediaByFormat(parent.getMediaContainer(), format);
			LOG.debug("Updating existing media '" + dmm + "'.");
		} catch (ModelNotFoundException var5) {
			dmm = this.createModel();
			dmm.setCode(this.createCode(parent, format));
			dmm.setFolder(parent.getFolder());
			dmm.setMediaContainer(parent.getMediaContainer());
			dmm.setMediaFormat(format);
			dmm.setAltText(parent.getAltText());
			dmm.setCatalogVersion(parent.getCatalogVersion());
			dmm.setDescription(parent.getDescription());
		}


//		System.out.println("originalUrl-->>"+originalUrl);
//
//		int domainUrlIndex = originalUrl.indexOf("/upload/")+8;
//
//		System.out.println("domainUrlIndex-->>"+domainUrlIndex);
//
//		String domainUrl = originalUrl.substring(0,domainUrlIndex);
//
//		System.out.println("domainUrl-->>"+domainUrl);
//
//		String resourceUrl = originalUrl.substring(domainUrlIndex-1,originalUrl.length());
//
//		System.out.println("resourceUrl-->>"+resourceUrl);
//
//		String conversionFormat = format.getConversion();
//		System.out.println("conversionFormat-->>"+conversionFormat);

		String transformationUrl = getTransformationApiService().createTransformation(parent,format);
		dmm.setURL(transformationUrl);
		dmm.setCloudinaryURL(transformationUrl);

		System.out.println("dmm.getURL()-->>"+dmm.getURL());

		dmm.setOriginal(parent);
		dmm.setOriginalDataPK(parent.getDataPK());
		//dmm.setSaveToCloudinary(Boolean.TRUE);
		this.getModelService().save(dmm);
		//this.loadContents(dmm, parent, format, content);
		this.getModelService().refresh(dmm);
		return dmm;
	}

	protected MediaModel createModel() {
		return (MediaModel)this.getModelService().create(MediaModel.class);
	}

	protected void loadContents(MediaModel dmm, MediaModel parent, ConversionMediaFormatModel format, InputStream inputStream) {
		BufferedInputStream stream = null;

		try {
			stream = new BufferedInputStream(inputStream);
			this.getMediaService().setStreamForMedia(dmm, stream, this.createFileName(parent, format), this.createMime(parent, format));
		} finally {
			IOUtils.closeQuietly(stream);
		}

	}

	protected String createMime(MediaModel parent, ConversionMediaFormatModel format) {
		return format.getMimeType() != null ? format.getMimeType() : parent.getMime();
	}

	protected String createFileName(MediaModel parent, ConversionMediaFormatModel format) {
		return this.createCode(parent, format);
	}

	protected String createCode(MediaModel parent, MediaFormatModel format) {
		return parent.getCode() + "_" + format.getQualifier();
	}

	public ModelService getModelService() {
		return this.modelService;
	}


	public MediaService getMediaService() {
		return this.mediaService;
	}



	public TransformationApiService getTransformationApiService()
	{
		return transformationApiService;
	}
}
