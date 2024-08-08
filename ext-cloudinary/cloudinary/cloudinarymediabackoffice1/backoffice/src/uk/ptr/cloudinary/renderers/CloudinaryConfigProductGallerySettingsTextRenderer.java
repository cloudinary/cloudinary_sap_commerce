package uk.ptr.cloudinary.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractPanel;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractSection;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaComponentRenderer;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

import javax.annotation.Resource;

public class CloudinaryConfigProductGallerySettingsTextRenderer extends AbstractEditorAreaComponentRenderer<AbstractPanel, CloudinaryConfigModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryConfigProductGallerySettingsTextRenderer.class);

    @Resource
    private ConfigurationService configurationService;


    private String getLabel(String key) {
        return Labels.getLabel(key);
    }

    @Override
    public void render(Component component, AbstractPanel abstractPanel, CloudinaryConfigModel cloudinaryConfigModel, DataType dataType, WidgetInstanceManager widgetInstanceManager) {

        Div usageResponseDiv = new Div();
        final Html html = new Html();
        html.setSclass("yw-editorarea-z-html");
        html.setContent("Enter the configuration JSON to be used by the gallery.<br/> For a quick and easy start, use the <a href='https://demo.cloudinary.com/product-gallery/editor?code=configjson' target=‘_blank’>Product Gallery Editor</a>. Configure the widget as needed and copy the generated JSON to this field.<br/>E.g. {\"aspectRatio\":\"3:4\",\"transformation\":{\"crop\":\"fill\"}}<br/>Note that the \"cloudName\", \"container\", and \"mediaAssets\" parameters set in the editor are not required and will be added/overwritten by the integration.<br/>Additional parameters are available and not offered by the editor. See <a href='https://cloudinary.com/documentation/product_gallery_reference' target=‘_blank’>Product Gallery API reference</a> for the full list of parameters.");

        usageResponseDiv.appendChild(html);
        usageResponseDiv.setParent(component);
    }
}

