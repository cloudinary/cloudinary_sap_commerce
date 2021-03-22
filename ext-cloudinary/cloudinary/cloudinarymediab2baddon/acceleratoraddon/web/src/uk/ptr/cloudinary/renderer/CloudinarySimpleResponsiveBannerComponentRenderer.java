
package uk.ptr.cloudinary.renderer;

import de.hybris.platform.acceleratorcms.model.components.SimpleResponsiveBannerComponentModel;
import de.hybris.platform.acceleratorfacades.device.ResponsiveMediaFacade;
import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import uk.ptr.cloudinary.constants.Cloudinarymediab2baddonConstants;

import javax.annotation.Resource;
import javax.servlet.jsp.PageContext;
import java.util.List;
import java.util.Map;


/**
 * Cloudinarymediaaddon renderer for SimpleResponsiveBanner
 */
public class CloudinarySimpleResponsiveBannerComponentRenderer<C extends SimpleResponsiveBannerComponentModel> extends
		DefaultAddOnCMSComponentRenderer<C>
{
	private static final String COMPONENT = "component";

	@Resource(name = "responsiveMediaFacade")
	private ResponsiveMediaFacade responsiveMediaFacade;

	@Resource(name = "commerceCommonI18NService")
	private CommerceCommonI18NService commerceCommonI18NService;

	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext, final C component)
	{
		final Map<String, Object> model = super.getVariablesToExpose(pageContext, component);
		model.put(COMPONENT, component);
		MediaContainerModel mediaContainer = component
				.getMedia(commerceCommonI18NService.getCurrentLocale());
		final List<ImageData> mediaDataList = responsiveMediaFacade.getImagesFromMediaContainer(mediaContainer);
		model.put("medias", mediaDataList);
		model.put("urlLink", component.getUrlLink());

		if(mediaContainer!=null && "testtaglibcomp".equalsIgnoreCase(component.getUid())) {
			model.put("mediaContainerPk", component
					.getMedia(commerceCommonI18NService.getCurrentLocale()).getPk());
		}
		return model;
	}

	@Override
	protected String getAddonUiExtensionName(final C component)
	{
		return Cloudinarymediab2baddonConstants.EXTENSIONNAME;
	}
}
