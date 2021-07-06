package uk.ptr.cloudinary.renderers;

import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractPanel;
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

public class CloudinaryConfigMediaPresetTextRenderer extends AbstractEditorAreaComponentRenderer<AbstractPanel, CloudinaryConfigModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryConfigMediaPresetTextRenderer.class);
    public static final String PRESET_TEXT = "See the <a href='https://cloudinary.com/documentation/admin_api#upload_presets' target=‘_blank’>upload presets</a> documentation";

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
        html.setContent(PRESET_TEXT);

        usageResponseDiv.appendChild(html);
        usageResponseDiv.setParent(component);
    }
}

