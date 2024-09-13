
package uk.ptr.cloudinary.interceptors.beforeview;

import com.cloudinary.Cloudinary;
import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.facades.CloudinaryConfigFacade;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Interceptor to set cloudinary related details for responsive behaviour
 */
public class CloudinaryBeforeViewHandler implements BeforeViewHandler
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CloudinaryBeforeViewHandler.class);

	@Resource
	private CloudinaryConfigFacade cloudinaryConfigFacade;

	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
	{
		CloudinaryConfigModel cloudinaryConfig = cloudinaryConfigFacade.getCloudinaryConfig();


		if(BooleanUtils.isTrue(cloudinaryConfig.getEnableCloudinary()) && BooleanUtils.isTrue(cloudinaryConfig.getCloudinaryResponsive()) && cloudinaryConfig.getCloudinaryURL()!= null) {

			Cloudinary cloudinary = new Cloudinary(cloudinaryConfig.getCloudinaryURL());
			cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");

			modelAndView.addObject("cloudName", cloudinary.config.cloudName);
			modelAndView.addObject("max", cloudinaryConfig.getCloudinaryImageWidthLimitMax());
			modelAndView.addObject("min", cloudinaryConfig.getCloudinaryImageWidthLimitMin());
			modelAndView.addObject("byteStep", cloudinaryConfig.getCloudinaryByteStep());
		}

		modelAndView.addObject("isResponsiveEnabled", BooleanUtils.isTrue(cloudinaryConfig.getEnableCloudinary()) && BooleanUtils.isTrue(cloudinaryConfig.getCloudinaryResponsive()));

	}
}
