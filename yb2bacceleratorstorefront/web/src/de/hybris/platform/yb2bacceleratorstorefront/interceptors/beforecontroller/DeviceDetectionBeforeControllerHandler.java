/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.interceptors.beforecontroller;

import de.hybris.platform.acceleratorfacades.device.DeviceDetectionFacade;
import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeControllerHandler;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;


/**
 * An interceptor to set up the request the detected device information.
 */
public class DeviceDetectionBeforeControllerHandler implements BeforeControllerHandler
{
	@Resource(name = "deviceDetectionFacade")
	private DeviceDetectionFacade deviceDetectionFacade;

	@Override
	public boolean beforeController(final HttpServletRequest request, final HttpServletResponse response, final HandlerMethod handler)
	{
		// Detect the device information for the current request
		deviceDetectionFacade.initializeRequest(request);
		return true;
	}
}
