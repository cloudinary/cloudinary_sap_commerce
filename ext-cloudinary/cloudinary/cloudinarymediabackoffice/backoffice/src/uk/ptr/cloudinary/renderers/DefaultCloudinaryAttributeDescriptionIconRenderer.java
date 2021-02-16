package uk.ptr.cloudinary.renderers;

import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.renderers.attributedescription.AttributeDescriptionIconRenderer;
import com.hybris.cockpitng.renderers.attributedescription.DefaultAttributeDescriptionIconRenderer;
import org.zkoss.zul.Div;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;

import com.hybris.cockpitng.core.util.CockpitProperties;
import com.hybris.cockpitng.util.UITools;

import javax.annotation.Resource;

public class DefaultCloudinaryAttributeDescriptionIconRenderer extends DefaultAttributeDescriptionIconRenderer implements AttributeDescriptionIconRenderer {

    public static final String YW_DESC_CLOUDINARY_TOOLTIP = "yw-desc-cloudinary-tooltip";
    protected static final String COCKPIT_PROPERTY_DISPLAY_ATTRIBUTE_DESCRIPTION = "cockpitng.displayAttributeDescriptions";
    private static final String SCLASS_CELL_DESCRIPTION = "attribute-label-description";

    @Resource
    private CockpitProperties cockpitProperties;


    @Override
    public void renderDescriptionIcon(final String desc, final Div
            labelContainer)
    {
        if (BooleanUtils.isTrue(isDisplayingAttributeDescriptionEnabled()) && StringUtils.isNotBlank(desc))
        {
            final Div description = new Div();

            final Popup popup = new Popup();
            popup.appendChild(new Label(desc));

            UITools.modifySClass(popup, YW_DESC_CLOUDINARY_TOOLTIP, true);

            description.setTooltiptext(StringUtils.abbreviate(desc, 20));
            description.appendChild(popup);
            description.addEventListener(Events.ON_CLICK, event -> popup.open(description));
            labelContainer.appendChild(description);
            UITools.modifySClass(description, SCLASS_CELL_DESCRIPTION, true);
        }
    }

    protected boolean isDisplayingAttributeDescriptionEnabled()
    {
        final String displayAttributeDescriptionPropertyValue = cockpitProperties.getProperty
                (COCKPIT_PROPERTY_DISPLAY_ATTRIBUTE_DESCRIPTION);
        return BooleanUtils.toBoolean(displayAttributeDescriptionPropertyValue);
    }


}
