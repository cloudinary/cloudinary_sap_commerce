package uk.ptr.cloudinary.facades;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.mediaconversion.web.facades.OnDemandConversionFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;


public interface CloudinaryOnDemandConversionFacade extends OnDemandConversionFacade
{
    String retrieveURL(String mediaQualifier, String format) throws UnknownIdentifierException;

    String retrieveURL(MediaContainerModel mediaQualifier, MediaFormatModel format);

    String convert(MediaContainerModel mediaQualifier, MediaFormatModel format);

    String convert(MediaContainerModel mediaQualifier, String format);
}
