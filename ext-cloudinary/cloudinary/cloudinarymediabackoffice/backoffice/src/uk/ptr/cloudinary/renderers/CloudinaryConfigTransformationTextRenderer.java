package uk.ptr.cloudinary.renderers;

import com.cloudinary.api.ApiResponse;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractSection;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.MessageboxUtils;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaComponentRenderer;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.AdminApiService;
import uk.ptr.cloudinary.service.AnalyticsApiService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfigTransformationTextRenderer extends AbstractEditorAreaComponentRenderer<AbstractSection, CloudinaryConfigModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryConfigTransformationTextRenderer.class);

    @Resource
    private ConfigurationService configurationService;



    @Override
    public void render(Component component, AbstractSection abstractSectionConfiguration, CloudinaryConfigModel cloudinaryConfigModel, DataType dataType, WidgetInstanceManager widgetInstanceManager) {

        Div usageResponseDiv = new Div();
        final Html html = new Html();
        html.setSclass("yw-editorarea-z-html");
        html.setContent("Cloudinary allows you to transform your images and videos on-the-fly to any required format, style, dimensions and quality, simply by adding transformation parameters to your delivery URLs. <a href='https://cloudinary.com/documentation/transformation_reference' target=‘_blank’>See examples.</a>");

        usageResponseDiv.appendChild(html);
        usageResponseDiv.setParent(component);

    }

    private String getLabel(String key) {
        return Labels.getLabel(key);
    }

}

