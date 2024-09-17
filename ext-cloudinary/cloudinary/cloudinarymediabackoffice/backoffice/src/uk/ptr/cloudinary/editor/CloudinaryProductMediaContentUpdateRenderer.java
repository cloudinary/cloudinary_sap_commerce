package uk.ptr.cloudinary.editor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractSection;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaComponentRenderer;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
//import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import uk.ptr.cloudinary.CloudinaryMasterMediaUtil;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.response.UploadApiResponseData;
import uk.ptr.cloudinary.service.RemoveTagApiService;
import uk.ptr.cloudinary.service.UpdateTagApiService;
import uk.ptr.cloudinary.util.CloudinaryConfigUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

public class CloudinaryProductMediaContentUpdateRenderer extends AbstractEditorAreaComponentRenderer<AbstractSection, ProductModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryProductMediaContentUpdateRenderer.class);

    public static final String VERSION = "v";

    //private AnnotateDataBinder binder;

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private ModelService modelService;

    @Resource
    private UpdateTagApiService updateTagApiService;

    @Resource
    private RemoveTagApiService removeTagApiService;

    @Override
    public void render(Component component, AbstractSection abstractSection, ProductModel productModel, DataType dataType, WidgetInstanceManager widgetInstanceManager) {

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        if (cloudinaryConfigModel != null && BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary())) {
            Textbox textbox = new Textbox();
            textbox.setVisible(false);
            Div uploadButtonDiv = new Div();
            final Button button = new Button("Select Asset");

            uploadButtonDiv.appendChild(button);

            button.addEventListener(Events.ON_CLICK, (event) -> {
                onClickbutton(component, textbox, productModel, cloudinaryConfigModel);
            });
            component.appendChild(uploadButtonDiv);
            component.appendChild(textbox);
        }

    }

    public void onClickbutton(Component parent, Textbox textbox, ProductModel productModel, CloudinaryConfigModel cloudinaryConfigModel) {
        Window dialogWin = (Window) Executions.createComponents("widgets/cloudinaryuploadedit.zul", parent, null);
        //binder = new AnnotateDataBinder(dialogWin);
        //binder.loadAll();
        dialogWin.doModal();

        Button done = (Button) dialogWin.getFellow("done");
        done.addEventListener(Events.ON_CLICK, event -> {
            Textbox tx = (Textbox) dialogWin.getFellow(CloudinarymediacoreConstants.TEXT_FIELD);
            textbox.setValue(tx != null ? tx.getValue() : "");
            populateProductMediaValues(textbox, productModel, cloudinaryConfigModel);
        });

        dialogWin.addEventListener(Events.ON_CLOSE, event -> {
            Textbox tx = (Textbox) dialogWin.getFellow(CloudinarymediacoreConstants.TEXT_FIELD);
            textbox.setValue(tx != null ? tx.getValue() : "");
            populateProductMediaValues(textbox, productModel, cloudinaryConfigModel);
        });
    }

    private void populateProductMediaValues(final Textbox textbox, final ProductModel productModel, final CloudinaryConfigModel cloudinaryConfigModel) {
        String updatedUrl = "";
        try {
            UploadApiResponseData responseData = getUploadApiResponseData(textbox);
            if (responseData != null) {
                updatedUrl = CloudinaryConfigUtils.updateMediaCloudinaryUrl(responseData.getSecure_url(), cloudinaryConfigModel.getCloudinaryCname());
                if (CollectionUtils.isEmpty(productModel.getGalleryImages())) {

                    MediaContainerModel mediaContainerModel = createMasterMedia(productModel, updatedUrl, responseData, cloudinaryConfigModel.getCloudinaryURL());
                    productModel.setGalleryImages(Collections.singletonList(mediaContainerModel));
                    modelService.save(productModel);
                } else {
                    updateCloudinaryMediaOnProduct(productModel, cloudinaryConfigModel, updatedUrl, responseData);
                }
            }

        } catch (JsonProcessingException e) {
            LOG.error("Json parsing error save media", e);
        } catch (RuntimeException runtimeException) {
            LOG.error("Cannot save cloudinary media on product", runtimeException);
        }
    }

    private void updateCloudinaryMediaOnProduct(ProductModel productModel, CloudinaryConfigModel cloudinaryConfigModel, String updatedUrl, UploadApiResponseData responseData) {

        List<MediaContainerModel> mediaContainerModelList = productModel.getGalleryImages();
        mediaContainerModelList.stream().forEach(mc -> {
            MediaModel masterImage = CloudinaryMasterMediaUtil.getMasterImage(mc);
            try {
                if (masterImage != null) {
                    removeTagApiService.removeTagFromAsset(masterImage.getCloudinaryPublicId(), productModel.getCode(), cloudinaryConfigModel.getCloudinaryURL());
                }
            } catch (IllegalArgumentException illegalException) {
                LOG.error("Illegal Argument " + illegalException.getMessage(), illegalException);
            } catch (Exception e) {
                LOG.error("Exception occurred calling Upload  API " + e.getMessage(), e);
            }
        });
        MediaContainerModel mediaContainer = createMasterMedia(productModel, updatedUrl, responseData, cloudinaryConfigModel.getCloudinaryURL());
        productModel.setGalleryImages(Collections.singletonList(mediaContainer));
        modelService.save(productModel);
    }

    private MediaContainerModel createMasterMedia(ProductModel productModel, String updatedUrl, UploadApiResponseData responseData, String cloudinaryUrl) {
        MediaModel mediaModel = this.modelService.create(MediaModel.class);
        //mediaModel.setCloudinaryURL(updatedUrl != null ? updatedUrl : responseData.getSecure_url());
        mediaModel.setURL(updatedUrl != null ? updatedUrl : responseData.getSecure_url());
        mediaModel.setCode(UUID.randomUUID().toString());
        mediaModel.setCatalogVersion(productModel.getCatalogVersion());
        mediaModel.setCloudinaryPublicId(responseData.getPublic_id());
        mediaModel.setCloudinaryResourceType(responseData.getResource_type());
        mediaModel.setCloudinaryType(responseData.getType());
        StringBuilder version = new StringBuilder();
        version.append(VERSION).append(responseData.getVersion());
        mediaModel.setCloudinaryVersion(version.toString());
        mediaModel.setCloudinaryMediaFormat(responseData.getFormat());

        modelService.save(mediaModel);

        MediaContainerModel mediaContainerModel = this.modelService.create(MediaContainerModel.class);
        mediaContainerModel.setQualifier(UUID.randomUUID().toString());
        mediaContainerModel.setCatalogVersion(productModel.getCatalogVersion());
        mediaContainerModel.setMedias(Collections.singletonList(mediaModel));

        modelService.save(mediaContainerModel);

        updateTagOnProduct(cloudinaryUrl, productModel.getCode(), mediaModel);
        return mediaContainerModel;
    }

    private UploadApiResponseData getUploadApiResponseData(Textbox textField) throws JsonProcessingException {
        try {
            if (StringUtils.isNotEmpty(textField.getValue())) {
                final ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> response = mapper.readValue(textField.getValue(), new TypeReference<Map<String, Object>>() {
                });
                return mapper.convertValue(response, UploadApiResponseData.class);
            }
        } catch (JsonProcessingException e) {
            LOG.error("Json parsing error save media", e);
        }
        return null;
    }


    private void updateTagOnProduct(String cloudinaryUrl, String productCode, MediaModel mediaModel) {
        try {
            updateTagApiService.updateTagOnAsests(mediaModel.getCloudinaryPublicId(), productCode, cloudinaryUrl,mediaModel.getCloudinaryResourceType());
        } catch (IOException e) {
            LOG.error("Error occured while updating tag for Media code  : " + mediaModel.getCode()  + "Asset public id" + mediaModel.getCloudinaryPublicId() + "productCode : " + productCode , e);
        }
    }
}
