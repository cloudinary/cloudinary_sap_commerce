/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.config;

import de.hybris.platform.swagger.ApiDocInfo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;


/**
 * Configuration class for cloudinarymediawebservices.
 * Provide {@link ApiDocInfo} bean in this class.
 * This class can be changed as WebMvc configuration class of your web application if needed.
 *
 * @since 2211
 */
@Configuration
@ImportResource(value = "classpath*:/swagger/swaggerintegration/web/spring/*-web-spring.xml")
public class WebConfig
{
	/**
	 * Creating bean.
	 *
	 * @return {@link Bean} for {@link ApiDocInfo}.
	 */
	@Bean("apiDocInfo")
	public ApiDocInfo apiDocInfo()
	{
		return () -> "cloudinarymediawebservices";
	}

}
