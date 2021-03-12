package uk.ptr.cloudinary.renderers;

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

public class CloudinaryConfigProductGalleryTextRenderer extends AbstractEditorAreaComponentRenderer<AbstractSection, CloudinaryConfigModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryConfigProductGalleryTextRenderer.class);

    @Resource
    private ConfigurationService configurationService;



    @Override
    public void render(Component component, AbstractSection abstractSectionConfiguration, CloudinaryConfigModel cloudinaryConfigModel, DataType dataType, WidgetInstanceManager widgetInstanceManager) {

        Div usageResponseDiv = new Div();
        final Html html = new Html();
        html.setSclass("yw-editorarea-z-html");
        html.setContent("Integrate a modern, responsive, flexible product gallery into your product detail pages on your e-commerce site.<br/> Cloudinary's Product Gallery has saved hundreds of hours of development time for iconic e-commerce brands.<br/><a href='https://cloudinary.com/documentation/product_gallery' target=‘_blank’>Go to documentation</a>, <a href='https://demo.cloudinary.com/product-gallery/editor?code=configjson' target=‘_blank’>View Active demo</a>");

        usageResponseDiv.appendChild(html);
        usageResponseDiv.setParent(component);

    }

    private String getLabel(String key) {
        return Labels.getLabel(key);
    }

}

