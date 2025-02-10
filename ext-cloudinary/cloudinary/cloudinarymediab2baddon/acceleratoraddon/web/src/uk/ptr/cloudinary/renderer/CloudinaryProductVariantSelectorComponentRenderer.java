
package uk.ptr.cloudinary.renderer;

import de.hybris.platform.acceleratorcms.model.components.ProductVariantSelectorComponentModel;
import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import uk.ptr.cloudinary.constants.Cloudinarymediab2baddonConstants;

import javax.servlet.jsp.PageContext;
import java.util.Map;


/**
 * Cloudinarymediaaddon renderer for SimpleResponsiveBanner
 */
public class CloudinaryProductVariantSelectorComponentRenderer<C extends ProductVariantSelectorComponentModel> extends
		DefaultAddOnCMSComponentRenderer<C>
{
	private static final String COMPONENT = "component";

	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final C component)
	{
		final Map<String, Object> model = super.getVariablesToExpose(pageContext, component);
		model.put(COMPONENT, component);
		return model;
	}

	@Override
	protected String getAddonUiExtensionName(final C component)
	{
		return Cloudinarymediab2baddonConstants.EXTENSIONNAME;
	}
}
