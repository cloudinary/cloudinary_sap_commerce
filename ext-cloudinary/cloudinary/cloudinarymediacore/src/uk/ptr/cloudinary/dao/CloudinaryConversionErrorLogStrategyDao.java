package uk.ptr.cloudinary.dao;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.mediaconversion.conversion.ConversionErrorLogStrategyDao;
import de.hybris.platform.mediaconversion.model.ConversionErrorLogModel;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;

import java.util.Collection;


public interface CloudinaryConversionErrorLogStrategyDao extends ConversionErrorLogStrategyDao
{
    Collection<ConversionErrorLogModel> findAllErrorLogs(MediaContainerModel mediaContainer, MediaFormatModel mediaFormat);
}
