/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.facades.populator;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductImagePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPrimaryImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Populate the product data with the product's primary image
 */
public class CloudinaryProductPrimaryImagePopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductImagePopulator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{

		final MediaContainerModel primaryImageMediaContainer = getPrimaryImageMediaContainer(productModel);
		if (primaryImageMediaContainer != null)
		{
			final List<ImageData> imageList = new ArrayList<ImageData>();

			// Use the first container as the primary image
			addImagesInFormats(primaryImageMediaContainer, ImageDataType.PRIMARY, 0, imageList);

			for (final ImageData imageData : imageList)
			{
				if (imageData.getAltText() == null)
				{
					imageData.setAltText(productModel.getName());
				}
			}
			productData.setImages(imageList);
		}


		if (primaryImageMediaContainer != null)
		{
			 Collection<ImageData> imageList = productData.getImages();
			List<MediaModel> masterMediaList =  primaryImageMediaContainer.getMedias().stream().filter(media -> media.getCloudinaryURL()!=null && media.getMediaFormat()==null).collect(Collectors.toList());
			if(CollectionUtils.isNotEmpty(masterMediaList)){
				MediaModel masterMedia = masterMediaList.get(0);
				final ImageData imageData = getImageConverter().convert(masterMedia);
				imageData.setImageType(ImageDataType.PRIMARY);
				if (imageData.getAltText() == null)
				{
					imageData.setAltText(productModel.getName());
				}
				imageList.add(imageData);
				productData.setImages(imageList);
			}
		}
	}

	protected MediaContainerModel getPrimaryImageMediaContainer(final SOURCE productModel)
	{
		final MediaModel picture = (MediaModel) getProductAttribute(productModel, ProductModel.PICTURE);
		if (picture != null)
		{
			return picture.getMediaContainer();
		}
		return null;
	}


}
