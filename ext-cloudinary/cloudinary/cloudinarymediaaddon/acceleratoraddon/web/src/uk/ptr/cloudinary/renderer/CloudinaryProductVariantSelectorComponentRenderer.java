
package uk.ptr.cloudinary.renderer;

import de.hybris.platform.acceleratorcms.model.components.ProductVariantSelectorComponentModel;
import de.hybris.platform.acceleratorfacades.productcarousel.ProductCarouselFacade;
import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import uk.ptr.cloudinary.constants.CloudinarymediaaddonConstants;

import javax.annotation.Resource;
import javax.servlet.jsp.PageContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
		return CloudinarymediaaddonConstants.EXTENSIONNAME;
	}
}
