package uk.ptr.cloudinary.search.providers;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.provider.impl.ImageValueProvider;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Resource;

import org.apache.log4j.Logger;

import uk.ptr.cloudinary.service.TransformationApiService;


public class CloudinaryImageValueProvider extends ImageValueProvider
{

    private static final Logger LOG = Logger.getLogger(CloudinaryImageValueProvider.class);

    @Resource
    private TransformationApiService transformationApiService;

    @Resource
    private Converter<MediaModel, ImageData> imageConverter;


    @Override
    public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
                                                 final Object model) throws FieldValueProviderException
    {
        if (model instanceof ProductModel)
        {
            final MediaFormatModel mediaFormatModel = getMediaService().getFormat(getMediaFormat());
            if (mediaFormatModel != null)
            {
                final MediaModel media = findMedia((ProductModel) model, mediaFormatModel);

                if (media != null)
                {
                    Collection<ImageData> imageData = transformationApiService.createTransformation((ProductModel) model,Collections.singletonList(imageConverter.convert(media)));
                    return createFieldValues(indexedProperty, imageData.stream().findFirst().get().getUrl());
                }
                if (LOG.isDebugEnabled())
                {
                    LOG.debug("No [" + mediaFormatModel.getQualifier() + "] image found for product ["
                            + ((ProductModel) model).getCode() + "]");
                }
            }
        }
        return Collections.emptyList();
    }
}
