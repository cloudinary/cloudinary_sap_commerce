/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.channel;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import uk.ptr.cloudinary.queues.data.ProductExpressUpdateElementData;
import uk.ptr.cloudinary.queues.impl.ProductExpressUpdateQueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductExpressUpdateChannelListenerTest
{
	private static final String PRODUCT_CODE = "productCode";
	private static final String ANOTHER_PRODUCT_CODE = "anotherProductCode";
	private static final String CATALOG_VERSION = "Online";
	private static final String CATALOG_ID = "productCatalog";
	@Mock
	private ProductModel product;
	@Mock
	private ProductModel anotherProduct;
	@Mock
	private ProductModel duplicateProduct;
	private ProductExpressUpdateElementData productElementData;
	private ProductExpressUpdateElementData anotherProductElementData;
	private ProductExpressUpdateChannelListener listener;
	private ProductExpressUpdateQueue productExpressUpdateQueue;
	@Mock
	private Converter<ProductModel, ProductExpressUpdateElementData> productExpressUpdateElementConverter;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);


		productExpressUpdateQueue = new ProductExpressUpdateQueue();
		listener = new ProductExpressUpdateChannelListener();
		listener.setProductExpressUpdateElementConverter(productExpressUpdateElementConverter);
		listener.setProductExpressUpdateQueue(productExpressUpdateQueue);

		productElementData = new ProductExpressUpdateElementData();
		productElementData.setCode(PRODUCT_CODE);
		productElementData.setCatalogId(CATALOG_ID);
		productElementData.setCatalogVersion(CATALOG_VERSION);

		given(product.getCode()).willReturn(PRODUCT_CODE);
		given(productExpressUpdateElementConverter.convert(product)).willReturn(productElementData);

		anotherProductElementData = new ProductExpressUpdateElementData();
		anotherProductElementData.setCode(ANOTHER_PRODUCT_CODE);
		anotherProductElementData.setCatalogId(CATALOG_ID);
		anotherProductElementData.setCatalogVersion(CATALOG_VERSION);

		given(anotherProduct.getCode()).willReturn(ANOTHER_PRODUCT_CODE);
		given(productExpressUpdateElementConverter.convert(anotherProduct)).willReturn(anotherProductElementData);

		final ProductExpressUpdateElementData duplicateProductElementData = new ProductExpressUpdateElementData();
		duplicateProductElementData.setCode(PRODUCT_CODE);
		duplicateProductElementData.setCatalogId(CATALOG_ID);
		duplicateProductElementData.setCatalogVersion(CATALOG_VERSION);

		given(duplicateProduct.getCode()).willReturn(PRODUCT_CODE);
		given(productExpressUpdateElementConverter.convert(duplicateProduct)).willReturn(duplicateProductElementData);

	}

	@Test
	public void testOnMessage()
	{
		listener.onMessage(product);
		final ProductExpressUpdateElementData queueElement = productExpressUpdateQueue.getLastItem();
		Assert.assertEquals(productElementData, queueElement);
	}

	@Test
	public void testAddingToQueue()
	{
		listener.onMessage(product);
		listener.onMessage(anotherProduct);
		final ProductExpressUpdateElementData queueElement = productExpressUpdateQueue.getLastItem();
		Assert.assertEquals(anotherProductElementData, queueElement);
		Assert.assertEquals(2, productExpressUpdateQueue.getItems().size());
	}

	@Test
	public void testDuplicateElementAdded()
	{
		listener.onMessage(product);
		listener.onMessage(product);
		Assert.assertEquals(1, productExpressUpdateQueue.getItems().size());

		listener.onMessage(duplicateProduct);
		Assert.assertEquals(1, productExpressUpdateQueue.getItems().size());

		listener.onMessage(anotherProduct);
		Assert.assertEquals(2, productExpressUpdateQueue.getItems().size());
		listener.onMessage(duplicateProduct);
		Assert.assertEquals(2, productExpressUpdateQueue.getItems().size());
	}

}
