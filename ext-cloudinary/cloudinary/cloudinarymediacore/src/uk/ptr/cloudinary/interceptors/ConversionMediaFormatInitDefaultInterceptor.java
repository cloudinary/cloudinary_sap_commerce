package uk.ptr.cloudinary.interceptors;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.interceptor.InitDefaultsInterceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;


public class ConversionMediaFormatInitDefaultInterceptor implements InitDefaultsInterceptor
{
	@Override
	public void onInitDefaults(final Object model, final InterceptorContext interceptorContext) throws InterceptorException
	{
		if (model instanceof MediaFormatModel) {
			((MediaFormatModel)model).setTransformationStrategy("cloudinaryMediaConversionStrategy");
		}
	}
}
