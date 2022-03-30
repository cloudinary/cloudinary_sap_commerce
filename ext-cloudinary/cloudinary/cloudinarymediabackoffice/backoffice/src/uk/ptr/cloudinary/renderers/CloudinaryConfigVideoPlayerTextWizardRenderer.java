package uk.ptr.cloudinary.renderers;

import com.hybris.cockpitng.config.jaxb.wizard.ViewType;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.configurableflow.renderer.DefaultCustomViewRenderer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;

import java.util.Map;

public class CloudinaryConfigVideoPlayerTextWizardRenderer extends DefaultCustomViewRenderer {

    @Override
    public void render(Component component, ViewType viewType, Map<String, String> map, DataType dataType, WidgetInstanceManager widgetInstanceManager) {

        Div usageResponseDiv = new Div();
        final Html html = new Html();
        html.setContent("Enter the configuration JSON to be used by the player.<br/> For a quick and easy start, use the <a href='https://studio.cloudinary.com/?code=configjson' target=‘_blank’>Video Player Studio</a>. Configure the player as needed and copy the generated JSON to this field.<br/>E.g.{\"player\":{\"fluid\":\"true\",\"controls\":\"true\"} <br/>");
        usageResponseDiv.appendChild(html);
        usageResponseDiv.setParent(component);
    }

}
