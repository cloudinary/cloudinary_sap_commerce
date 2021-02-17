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
		MediaModel media;
		try {
			media = this.getMediaService().getMediaByFormat(parent.getMediaContainer(), format);
			LOG.debug("Updating existing media '" + media + "'.");
		} catch (ModelNotFoundException var5) {
			media = this.createModel();
			media.setCode(this.createCode(parent, format));
			media.setFolder(parent.getFolder());
			media.setMediaContainer(parent.getMediaContainer());
			media.setMediaFormat(format);
			media.setAltText(parent.getAltText());
			media.setCatalogVersion(parent.getCatalogVersion());
			media.setDescription(parent.getDescription());
		}


		String transformationUrl = getTransformationApiService().createTransformation(parent,format);
		media.setURL(transformationUrl);
		//media.setCloudinaryURL(transformationUrl);
		media.setOriginal(parent);
		media.setOriginalDataPK(parent.getDataPK());
		media.setCloudinaryTransformation(format.getTransformation());
		media.setCloudinaryPublicId(parent.getCloudinaryPublicId());
		media.setCloudinaryResourceType(parent.getCloudinaryResourceType());
		media.setCloudinaryType(parent.getCloudinaryType());
		media.setCloudinaryVersion(parent.getCloudinaryVersion());
		media.setCloudinaryMediaFormat(parent.getCloudinaryMediaFormat());
		this.getModelService().save(media);
		//this.loadContents(dmm, parent, format, content);
		this.getModelService().refresh(media);
		return media;
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
