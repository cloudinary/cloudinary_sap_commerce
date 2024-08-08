/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.commercefacades.order.data.PaymentModeData;
import de.hybris.platform.commercefacades.order.data.PaymentModeDataList;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentModeListWsDTO;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Controller
@RequestMapping(value = "/{baseSiteId}/paymentmodes")
@Api(tags = "Payment Modes")
public class PaymentModesController extends BaseController
{
	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource(name = "paymentModeService")
	private PaymentModeService paymentModeService;

	@Resource(name = "paymentModeConverter")
	private Converter<PaymentModeModel, PaymentModeData> paymentModeConverter;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(nickname = "getPaymentModes", value = "Gets all available payment modes.", notes = "Gets all payment modes defined for the base store.")
	@ApiBaseSiteIdParam
	public PaymentModeListWsDTO getPaymentModes(
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final PaymentModeDataList paymentModeList = new PaymentModeDataList();
		paymentModeList.setPaymentModes(paymentModeConverter.convertAll(paymentModeService.getAllPaymentModes()));
		return dataMapper.map(paymentModeList, PaymentModeListWsDTO.class);
	}
}
