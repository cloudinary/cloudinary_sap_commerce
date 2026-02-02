
package uk.ptr.cloudinary.renderer;

import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.platform.acceleratorfacades.device.ResponsiveMediaFacade;
import de.hybris.platform.acceleratorfacades.productcarousel.ProductCarouselFacade;
import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.media.MediaContainerModel;
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
public class CloudinaryProductCarouselComponentRenderer<C extends ProductCarouselComponentModel> extends
		DefaultAddOnCMSComponentRenderer<C>
{
	private static final String COMPONENT = "component";

	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;

	@Resource(name = "productCarouselFacade")
	private ProductCarouselFacade productCarouselFacade;

	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final C component)
	{
		final Map<String, Object> model = super.getVariablesToExpose(pageContext, component);
		model.put(COMPONENT, component);
		final List<ProductData> products = new ArrayList<>();

		products.addAll(collectLinkedProducts(component));
		products.addAll(collectSearchProducts(component));

		model.put("title", component.getTitle());
		model.put("productData", products);
		return model;
	}

	protected List<ProductData> collectLinkedProducts(final ProductCarouselComponentModel component)
	{
		return productCarouselFacade.collectProducts(component);
	}

	protected List<ProductData> collectSearchProducts(final ProductCarouselComponentModel component)
	{
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(component.getSearchQuery());
		final String categoryCode = component.getCategoryCode();

		if (searchQueryData.getValue() != null && categoryCode != null)
		{
			final SearchStateData searchState = new SearchStateData();
			searchState.setQuery(searchQueryData);

			final PageableData pageableData = new PageableData();
			pageableData.setPageSize(100); // Limit to 100 matching results

			return productSearchFacade.categorySearch(categoryCode, searchState, pageableData).getResults();
		}

		return Collections.emptyList();
	}

	@Override
	protected String getAddonUiExtensionName(final C component)
	{
		return CloudinarymediaaddonConstants.EXTENSIONNAME;
	}
}
