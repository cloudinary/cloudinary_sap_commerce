package uk.ptr.cloudinary.dao.impl;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.conversion.DefaultMediaConversionServiceDao;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import uk.ptr.cloudinary.dao.CloudinaryMediaConversionServiceDao;


public class DefaultCloudinaryMediaConversionServiceDao implements CloudinaryMediaConversionServiceDao
{
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Override
	public Collection<MediaModel> getConvertedMedias(MediaContainerModel container) {
		Map<String, Object> params = new TreeMap();
		params.put("container", container);
		FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {pk} FROM {Media} WHERE {mediaContainer} = ?container AND {original} IS NOT NULL", params);
		SearchResult<MediaModel> result = this.getFlexibleSearchService().search(query);
		return result.getResult();
	}


	@Override
	public Collection<MediaFormatModel> getAllMediaFormats()
	{
		SearchResult<MediaFormatModel> ret = this.getFlexibleSearchService().search("SELECT {pk} FROM {MediaFormat}");
		return ret.getResult();
	}

	@Override
	public MediaModel retrieveMaster(MediaContainerModel model) {
		Map<String, Object> params = new TreeMap();
		params.put("container", model);
		FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {m.pk} FROM {Media as m} WHERE {m.mediaContainer} = ?container AND {m.original} IS NULL AND {m.originalDataPK} IS NULL AND {m.cloudinaryPublicId} IS NOT NULL", params);
		return (MediaModel)this.getFlexibleSearchService().searchUnique(query);
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}
}
