package uk.ptr.cloudinary.tags;

import de.hybris.platform.mediaconversion.web.tag.AbstractUrlTag;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import uk.ptr.cloudinary.facades.CloudinaryOnDemandConversionFacade;


public abstract class AbstractCloudinaryUrlTag extends AbstractUrlTag
{
    private static final Logger LOG = Logger.getLogger(AbstractCloudinaryUrlTag.class);

    private String mediaQualifier;

    public String getMediaQualifier()
    {
        return mediaQualifier;
    }

    public void setMediaQualifier(final String mediaQualifier)
    {
        this.mediaQualifier = mediaQualifier;
    }

    @Override
    protected String retrieveURL() {
        String ret = this.retrieveImageFacade().retrieveURL(this.getMediaQualifier(), this.getFormat());
        LOG.debug("Image url is '" + ret + "'.");
        return ret;
    }


    protected CloudinaryOnDemandConversionFacade retrieveImageFacade() {
        if (this.getJspContext() instanceof PageContext) {
            return this.retrieveImageFacade(((PageContext)this.getJspContext()).getServletContext());
        } else {
            throw new IllegalStateException("Failed to access servlet context.");
        }
    }

    protected CloudinaryOnDemandConversionFacade retrieveImageFacade(ServletContext servletCtx) {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletCtx);
        return (CloudinaryOnDemandConversionFacade)ctx.getBean("cloudinaryOnDemandConversionFacade", CloudinaryOnDemandConversionFacade.class);
    }
}
