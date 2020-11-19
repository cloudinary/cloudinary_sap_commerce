package uk.ptr.cloudinary.servlet;

import de.hybris.platform.core.PK;
import de.hybris.platform.mediaconversion.web.facades.OnDemandConversionFacade;
import de.hybris.platform.mediaconversion.web.servlet.ConversionServlet;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import uk.ptr.cloudinary.facades.CloudinaryOnDemandConversionFacade;


public class CloudinaryConversionServlet extends ConversionServlet
{
    private static final Logger LOG = Logger.getLogger(CloudinaryConversionServlet.class);
    private static final String SLASH = "/";
    private static final Pattern PATH_INFO_SPLIT = Pattern.compile("/");
    private static final Pattern PATH_INFO_CLEANUP = Pattern.compile("/\\.?/");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !"/".equals(pathInfo) && pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
            pathInfo = PATH_INFO_CLEANUP.matcher(pathInfo).replaceAll("/");
            String[] pathSplit = PATH_INFO_SPLIT.split(pathInfo, 2);
            if (pathSplit.length != 2) {
                LOG.debug("No format specified.");
                resp.sendError(404, "No format specified.");
            }

            String cleanedPathSplit1 = this.cleanFromFormat(pathSplit[1]);
            this.processRequest(resp, pathSplit[0], cleanedPathSplit1);
        } else {
            resp.sendError(404, "Nothing's here...");
        }
    }

    private void processRequest(HttpServletResponse resp, String containerPK, String format) throws IOException {
        LOG.debug("Retrieving Media for container '" + containerPK + "' " + "and format '" + format + "'.");

        try {
            String url = this.retrieveImageFacade().convert(PK.parse(containerPK), format);
            LOG.debug("Redirecting to '" + url + "'.");
            resp.sendRedirect(url);
        } catch (PK.PKException var5) {
            LOG.debug("Invalid PK '" + containerPK + "' specified. Not a number.", var5);
            resp.sendError(404, "Invalid PK.");
        } catch (IllegalArgumentException var6) {
            LOG.debug("Invalid PK '" + containerPK + "' specified. Unknown type.", var6);
            resp.sendError(404, "Invalid PK.");
        } catch (ModelLoadingException var7) {
            LOG.debug("MediaContainer for '" + containerPK + "' not found.", var7);
            resp.sendError(404, "Not found.");
        } catch (UnknownIdentifierException var8) {
            LOG.debug("Invalid format qualifier '" + format + "' specified. " + "Not found.", var8);
            resp.sendError(404, "Invalid format qualifier.");
        } catch (Exception var9) {
            LOG.error("An error occurred while serving the request.", var9);
            resp.sendError(500);
        }

    }

    CloudinaryOnDemandConversionFacade retrieveImageFacade() {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        return (CloudinaryOnDemandConversionFacade)ctx.getBean("cloudinaryOnDemandConversionFacade", CloudinaryOnDemandConversionFacade.class);
    }
}
