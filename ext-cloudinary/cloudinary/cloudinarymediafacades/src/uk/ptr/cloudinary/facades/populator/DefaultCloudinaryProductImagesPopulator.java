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

import de.hybris.platform.variants.model.VariantProductModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.CloudinaryConfigService;
import uk.ptr.cloudinary.service.TransformationApiService;


/**
 * Populate the product data with the product's gallery images
 */
public class DefaultCloudinaryProductImagesPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductImagePopulator<SOURCE, TARGET>
{
	@Resource
	private TransformationApiService transformationApiService;

	@Override
	public void populate(final SOURCE productModel, final TARGET productData) throws ConversionException
	{

		productData.setImages(transformationApiService.createTransformation(productModel,productData.getImages()));

		StringBuilder sapCCProductCode = new StringBuilder();
		sapCCProductCode.append(CloudinarymediacoreConstants.SAP_SKU);
		if (CollectionUtils.isEmpty(productModel.getGalleryImages()) && productModel instanceof VariantProductModel) {
			ProductModel variantProductModel = ((VariantProductModel) productModel).getBaseProduct();

			if(CollectionUtils.isEmpty(variantProductModel.getGalleryImages()))
			{
				ProductModel currentProduct = ((VariantProductModel) variantProductModel).getBaseProduct();
				sapCCProductCode.append(currentProduct.getCode());
			}
			else {
				sapCCProductCode.append(variantProductModel.getCode());
			}
		} else {
			sapCCProductCode.append(productModel.getCode());
		}

		productData.setSapCCProductCode(sapCCProductCode.toString());
		productData.setSpinSetCode(productModel.getCloudinaryImageSpinTag());
	}
}
