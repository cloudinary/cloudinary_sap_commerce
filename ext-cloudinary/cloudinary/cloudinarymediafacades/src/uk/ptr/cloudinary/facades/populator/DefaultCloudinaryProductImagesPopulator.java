/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.facades.populator;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Collection;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.CloudinaryConfigService;


/**
 * Populate the product data with the product's gallery images
 */
public class DefaultCloudinaryProductImagesPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductImagePopulator<SOURCE, TARGET>
{
	@Resource
	private CloudinaryConfigService cloudinaryConfigService;

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{

		CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigService.getCloudinaryConfigModel();

		if(cloudinaryConfigModel!=null && BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary()))
		{
			StringBuilder categoryImageTransformation = new StringBuilder();
			StringBuilder categoryVideoTransformation = new StringBuilder();

			String globalImageTransformation = cloudinaryConfigModel.getCloudinaryGlobalImageTransformation();
			String globalVideoTransformation = cloudinaryConfigModel.getCloudinaryGlobalVideoTransformation();

			Collection<CategoryModel> categories = productModel.getSupercategories();
			boolean isCategoryOverride = false;

			if (CollectionUtils.isNotEmpty(categories))
			{

				isCategoryOverride = categories.stream().anyMatch(category -> BooleanUtils.isTrue(category.getIsCloudinaryOverride()));
				for (final CategoryModel category : categories)
				{
					if (!(category instanceof ClassificationClassModel))
					{
						if (StringUtils.isNotBlank(category.getCloudinaryImageTransformation()))
						{
							categoryImageTransformation.append(CloudinarymediacoreConstants.SLASH).append(category.getCloudinaryImageTransformation());
						}
						if (StringUtils.isNotBlank(category.getCloudinaryVideoTransformation()))
						{
							categoryVideoTransformation.append(CloudinarymediacoreConstants.SLASH).append(category.getCloudinaryVideoTransformation());
						}
					}
				}

			}

			for (ImageData imageData : productData.getImages())
			{
				if(imageData.getCloudinaryURL()!=null)
				{

					StringBuilder transformationURL = new StringBuilder();

					if (cloudinaryConfigModel != null)
					{
						if (com.cloudinary.utils.StringUtils.isNotBlank(cloudinaryConfigModel.getCloudinaryCname()))
						{
							transformationURL.append(cloudinaryConfigModel.getCloudinaryCname());
						}
						else
						{
							transformationURL.append(CloudinarymediacoreConstants.CLOUDINARY_DOMAIN_URL);
						}
						transformationURL.append(CloudinarymediacoreConstants.SLASH);

						String cloudinaryConnectionURL = cloudinaryConfigModel.getCloudinaryURL();
						int cloudNameIndex = cloudinaryConnectionURL.indexOf(CloudinarymediacoreConstants.AT);

						//Extract and set cloudname
						transformationURL.append(cloudinaryConnectionURL.substring(cloudNameIndex + 1, cloudinaryConnectionURL.length()));
						transformationURL.append(CloudinarymediacoreConstants.SLASH);
					}

					transformationURL.append(imageData.getCloudinaryResourceType());
					transformationURL.append(CloudinarymediacoreConstants.SLASH);
					transformationURL.append(imageData.getCloudinaryType());
					transformationURL.append(CloudinarymediacoreConstants.SLASH);

					if (StringUtils.isNotBlank(imageData.getCloudinaryTransformation()))
					{
						transformationURL.append(imageData.getCloudinaryTransformation());
					}

					if (StringUtils.isNotBlank(cloudinaryConfigModel.getCloudinaryQuality()))
					{
						if (StringUtils.isNotBlank(imageData.getCloudinaryTransformation()))
						{
							transformationURL.append(",");
						}
						transformationURL.append(cloudinaryConfigModel.getCloudinaryQuality());
					}


					if(!imageData.isCloudinaryOverride())
					{
						boolean isProductOverride = BooleanUtils.isTrue(productModel.getIsCloudinaryOverride());

						if (isProductOverride)
						{
							if (StringUtils.isNotBlank(productModel.getCloudinaryImageTransformation()) || StringUtils.isNotBlank(productModel.getCloudinaryVideoTransformation()))
							{
								transformationURL.append(CloudinarymediacoreConstants.SLASH);
								if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
								{
									transformationURL.append(productModel.getCloudinaryImageTransformation());
								}
								else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
								{
									transformationURL.append(productModel.getCloudinaryVideoTransformation());
								}
							}

						}
						else if (!isProductOverride)
						{
							if (isCategoryOverride)
							{
								if (StringUtils.isNotBlank(categoryImageTransformation.toString()) || StringUtils.isNotBlank(categoryVideoTransformation.toString()))
								{
									if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
									{
										transformationURL.append(categoryImageTransformation);
									}
									else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
									{
										transformationURL.append(categoryVideoTransformation);
									}
								}
								if (StringUtils.isNotBlank(productModel.getCloudinaryImageTransformation()) || StringUtils.isNotBlank(productModel.getCloudinaryVideoTransformation()))
								{
									transformationURL.append(CloudinarymediacoreConstants.SLASH);
									if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
									{
										transformationURL.append(productModel.getCloudinaryImageTransformation());
									}
									else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
									{
										transformationURL.append(productModel.getCloudinaryVideoTransformation());
									}
								}

							}
							else if (!isCategoryOverride)
							{
								if (StringUtils.isNotBlank(globalImageTransformation) || StringUtils.isNotBlank(globalVideoTransformation))
								{
									transformationURL.append(CloudinarymediacoreConstants.SLASH);
									if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
									{
										transformationURL.append(globalImageTransformation);
									}
									else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
									{
										transformationURL.append(globalVideoTransformation);
									}
								}

								if (StringUtils.isNotBlank(categoryImageTransformation.toString()) || StringUtils.isNotBlank(categoryVideoTransformation.toString()))
								{
									if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
									{
										transformationURL.append(categoryImageTransformation);
									}
									else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
									{
										transformationURL.append(categoryVideoTransformation);
									}
								}
								if (StringUtils.isNotBlank(productModel.getCloudinaryImageTransformation()) || StringUtils.isNotBlank(productModel.getCloudinaryVideoTransformation()))
								{
									transformationURL.append(CloudinarymediacoreConstants.SLASH);
									if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
									{
										transformationURL.append(productModel.getCloudinaryImageTransformation());
									}
									else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
									{
										transformationURL.append(productModel.getCloudinaryVideoTransformation());
									}
								}
							}

						}

					}
					transformationURL.append(CloudinarymediacoreConstants.SLASH);
					transformationURL.append(imageData.getCloudinaryVersion());
					transformationURL.append(CloudinarymediacoreConstants.SLASH);
					transformationURL.append(imageData.getCloudinaryPublicId());
					transformationURL.append(CloudinarymediacoreConstants.DOT);
					transformationURL.append(imageData.getCloudinaryMediaFormat());

					imageData.setUrl(transformationURL.toString());
				}
			}
		}
	}
}
