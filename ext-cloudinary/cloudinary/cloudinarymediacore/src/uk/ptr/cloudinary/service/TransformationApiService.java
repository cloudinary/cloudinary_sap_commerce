package uk.ptr.cloudinary.service;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collection;


public interface TransformationApiService
{
    String createTransformation(MediaModel masterMedia,MediaFormatModel format);

    Collection<ImageData> createTransformation(ProductModel product, Collection<ImageData> imageDatas);
}
