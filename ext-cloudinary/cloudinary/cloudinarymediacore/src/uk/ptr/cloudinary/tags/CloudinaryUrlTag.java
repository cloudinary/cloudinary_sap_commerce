package uk.ptr.cloudinary.tags;

import de.hybris.platform.mediaconversion.web.tag.Scope;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;


public class CloudinaryUrlTag extends AbstractCloudinaryUrlTag
{
    private static final Logger LOG = Logger.getLogger(CloudinaryUrlTag.class);
    private String var;
    private CloudinaryScope scope;

    public CloudinaryUrlTag() {
        this.scope = CloudinaryScope.page;
    }

    public void doTag() throws JspException, IOException
    {
        String url = this.retrieveURL();
        if (this.getVar() == null) {
            this.getJspContext().getOut().print(url);
        } else {
            if (!(this.getJspContext() instanceof PageContext)) {
                throw new IllegalStateException("PageContext cannot be accessed. (JspContext is not a PageContext.)");
            }

            this.getScope().set((PageContext)this.getJspContext(), this.getVar(), url);
        }

    }

    public String getVar() {
        return this.var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public CloudinaryScope getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope == null ? CloudinaryScope.page : CloudinaryScope.valueOf(scope);
    }

}
