/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.swagger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import springfox.documentation.spring.web.json.Json;
import springfox.documentation.swagger2.web.Swagger2Controller;


@ControllerAdvice(assignableTypes = Swagger2Controller.class)
public class ApiDocsAdvice implements ResponseBodyAdvice<Object>
{
	private static final Logger LOG = Logger.getLogger(ApiDocsAdvice.class);
	private static final String OPERATION_ID_FIELD = "operationId";
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean supports(final MethodParameter returnType, final Class<? extends HttpMessageConverter<?>> converterType)
	{
		return true;
	}

	@Override
	public Object beforeBodyWrite(final Object body, final MethodParameter returnType, final MediaType selectedContentType,
			final Class<? extends HttpMessageConverter<?>> selectedConverterType, final ServerHttpRequest request,
			final ServerHttpResponse response)
	{
		try
		{
			final JsonNode document = objectMapper.readTree(((Json) body).value());
			final List<JsonNode> allNodesWithOperationId = getAllNodesWithOperationId(document);
			final Set<String> allOperationsIdsTextSet = getOperationIdsText(allNodesWithOperationId);
			sanitize(allNodesWithOperationId, allOperationsIdsTextSet);
			return document;
		}
		catch (final IOException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}
			return body;
		}
	}

	private List<JsonNode> getAllNodesWithOperationId(final JsonNode parent)
	{
		final List<JsonNode> operationIdsNodes = new LinkedList<>();
		collectNodesByFieldName(parent, OPERATION_ID_FIELD, operationIdsNodes);
		return operationIdsNodes;
	}

	private void collectNodesByFieldName(final JsonNode parent, final String fieldName, final List<JsonNode> collectedNodes)
	{
		if (parent.has(fieldName))
		{
			collectedNodes.add(parent);
		}

		for (final JsonNode child : parent)
		{
			collectNodesByFieldName(child, fieldName, collectedNodes);
		}
	}

	private Set<String> getOperationIdsText(final List<JsonNode> allOperationIdsNodes)
	{
		return allOperationIdsNodes.stream().map(node -> node.get(OPERATION_ID_FIELD).textValue()).filter(StringUtils::isNoneEmpty)
				.collect(Collectors.toSet());
	}

	private void sanitize(final List<JsonNode> allOperationIdsNodes, final Set<String> allOperationsIdsTextSet)
	{
		final String firstDuplicationSuffix = "_1";

		for (final JsonNode node : allOperationIdsNodes)
		{
			final String text = node.get(OPERATION_ID_FIELD).textValue();
			if (StringUtils.endsWith(text, firstDuplicationSuffix))
			{
				final String operationIdWithoutSuffix = StringUtils.removeEnd(text, firstDuplicationSuffix);
				if (!allOperationsIdsTextSet.contains(operationIdWithoutSuffix))
				{
					((ObjectNode) node).set(OPERATION_ID_FIELD, new TextNode(operationIdWithoutSuffix));
				}
			}
		}
	}

}
