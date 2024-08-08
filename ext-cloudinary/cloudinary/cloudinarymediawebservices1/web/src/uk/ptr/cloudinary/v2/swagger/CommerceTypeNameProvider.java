/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.swagger;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.swagger.annotations.ApiModel;
import springfox.documentation.schema.DefaultTypeNameProvider;
import springfox.documentation.swagger.common.SwaggerPluginSupport;


@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER - 10)
public class CommerceTypeNameProvider extends DefaultTypeNameProvider
{
	private static final String TYPE_NAME_SUFFIX = "WsDTO";

	@Override
	public String nameFor(final Class<?> type)
	{
		final ApiModel annotation = AnnotationUtils.findAnnotation(type, ApiModel.class);
		final String defaultTypeName = getDefaultTypeName(type);
		return annotation != null ? StringUtils.defaultIfEmpty(annotation.value(), defaultTypeName) : defaultTypeName;
	}

	private String getDefaultTypeName(final Class<?> type)
	{
		return StringUtils.removeEndIgnoreCase(super.nameFor(type), TYPE_NAME_SUFFIX);
	}
}
