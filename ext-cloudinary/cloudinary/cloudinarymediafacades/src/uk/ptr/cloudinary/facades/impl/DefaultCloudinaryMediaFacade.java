package uk.ptr.cloudinary.facades.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.cmsfacades.media.impl.DefaultMediaFacade;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.Validator;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.facades.CloudinaryMediaFacade;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.response.UploadApiResponseData;
import uk.ptr.cloudinary.service.UploadApiService;
import uk.ptr.cloudinary.util.CloudinaryConfigUtils;

import jakarta.annotation.Resource;
import java.util.Map;

public class DefaultCloudinaryMediaFacade implements CloudinaryMediaFacade {

    private final Logger LOG = Logger.getLogger(DefaultCloudinaryMediaFacade.class);

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private ModelService modelService;

    @Resource
    private FacadeValidationService facadeValidationService;

    @Resource
    private Converter<MediaModel, MediaData> mediaModelConverter;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private UploadApiService uploadApiService;

    @Resource
    private Validator createMediaValidator;


    public MediaData addCloudinaryMedia(final MediaData media) {

        Preconditions.checkArgument(media != null);

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();


            UploadApiResponseData responseData = new UploadApiResponseData();
            try {
               responseData = getUploadApiResponseData(media.getCloudinaryMediaJson());
            } catch (JsonProcessingException e) {
                LOG.error("Json parsing error save media", e);
            }
            MediaModel mediaModel = savecloudinaryMedia(responseData, cloudinaryConfigModel.getCloudinaryCname(), media);

           return mediaModelConverter.convert(mediaModel);

    }

    private MediaModel savecloudinaryMedia(UploadApiResponseData responseData, String cloudinaryCname, MediaData media) {

        final MediaModel mediaModel = modelService.create(MediaModel.class);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(cloudinaryCname)) {
            String updatedUrl = CloudinaryConfigUtils.updateMediaCloudinaryUrl(uploadApiService.getSecureUrl(responseData), cloudinaryCname);
            mediaModel.setURL(updatedUrl);
        } else {
            mediaModel.setURL(uploadApiService.getSecureUrl(responseData));
        }
        mediaModel.setCode(media.getCode());
        setCatalogForMedia(mediaModel,media);
        mediaModel.setCloudinaryPublicId(responseData.getPublic_id());
        mediaModel.setCloudinaryResourceType(responseData.getResource_type());
        mediaModel.setCloudinaryType(responseData.getType());
        mediaModel.setCloudinaryVersion(responseData.getVersion());
        mediaModel.setCloudinaryMediaFormat(responseData.getFormat());
        mediaModel.setAltText(media.getAltText());
        mediaModel.setDescription(media.getDescription());
        mediaModel.setCloudinaryTransformation(uploadApiService.getTransformationForDerivedMedia(responseData));
        modelService.save(mediaModel);
        return mediaModel;
    }

    private void setCatalogForMedia(MediaModel mediaModel, MediaData media) {

        try
        {
            final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(media.getCatalogId(),
                    media.getCatalogVersion());
            mediaModel.setCatalogVersion(catalogVersion);
        }
        catch (UnknownIdentifierException | AmbiguousIdentifierException | IllegalArgumentException e)
        {
            throw new ConversionException("Unable to find a catalogVersion for catalogId [" + media.getCatalogId()
                    + "] and versionId [" + media.getCatalogVersion() + "]", e);
        }
    }

    private UploadApiResponseData getUploadApiResponseData(String jsonData) throws JsonProcessingException {
        try {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(jsonData)) {
                final ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> response = mapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {
                });
                return mapper.convertValue(response, UploadApiResponseData.class);
            }
        } catch (JsonProcessingException e) {
            LOG.error("Json parsing error save media", e);
        }
        return null;
    }

    @Override
    public MediaData addMedia(MediaData media) {
        LOG.info("Override DefaultMediaFacade");
        getFacadeValidationService().validate(getCreateMediaValidator(), media);
        MediaData mediaData = null;
        if (media.getCloudinaryMediaJson()!= null) {

            mediaData = addCloudinaryMedia(media);
        }
        return mediaData;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    protected FacadeValidationService getFacadeValidationService() {
        return facadeValidationService;
    }

    public Validator getCreateMediaValidator() {
        return createMediaValidator;
    }
}
