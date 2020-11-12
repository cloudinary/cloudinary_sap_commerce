package uk.ptr.cloudinary.dao.impl;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.mediaconversion.conversion.DefaultConversionErrorLogStrategyDao;
import de.hybris.platform.mediaconversion.model.ConversionErrorLogModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import uk.ptr.cloudinary.dao.CloudinaryConversionErrorLogStrategyDao;


public class DefaultCloudinaryConversionErrorLogStrategyDao extends DefaultConversionErrorLogStrategyDao implements CloudinaryConversionErrorLogStrategyDao
{
    @Override
    public Collection<ConversionErrorLogModel> findAllErrorLogs(final MediaContainerModel mediaContainer, final MediaFormatModel mediaFormat)
    {
        Map<String, Object> params = new TreeMap();
        params.put("container", mediaContainer);
        params.put("format", mediaFormat);
        FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {pk} FROM {ConversionErrorLog} WHERE {container} = ?container AND {targetMediaFormat} = ?format", params);
        SearchResult<ConversionErrorLogModel> result = getFlexibleSearchService().search(query);
        return result.getResult();
    }
}
