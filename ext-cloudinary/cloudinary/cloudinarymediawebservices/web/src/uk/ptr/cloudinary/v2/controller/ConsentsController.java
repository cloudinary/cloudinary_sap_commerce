/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.commercefacades.consent.ConsentFacade;
import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.commerceservices.consent.exceptions.CommerceConsentGivenException;
import de.hybris.platform.commerceservices.consent.exceptions.CommerceConsentWithdrawnException;
import de.hybris.platform.commercewebservicescommons.dto.consent.ConsentTemplateListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.consent.ConsentTemplateWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ConsentWithdrawnException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.webservicescommons.errors.exceptions.AlreadyExistsException;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import uk.ptr.cloudinary.consent.data.ConsentTemplateDataList;

import javax.annotation.Resource;
import javax.ws.rs.ForbiddenException;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}")
@Api(tags = "Consents")
public class ConsentsController extends BaseCommerceController
{
	private static final Logger LOG = LoggerFactory.getLogger(ConsentsController.class);

	@Resource(name = "consentFacade")
	private ConsentFacade consentFacade;

	@RequestMapping(value = "/consenttemplates", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@ApiOperation(nickname = "getConsentTemplates", value = "Fetch the list of consents", notes = "If user has not given or withdrawn consent to any of the template, no given or withdraw date is returned.")
	@ApiBaseSiteIdAndUserIdParam
	public ConsentTemplateListWsDTO getConsentTemplates(
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final ConsentTemplateDataList consentTemplateDataList = new ConsentTemplateDataList();
		final List<ConsentTemplateData> consentTemplateDatas = getUserFacade().isAnonymousUser() ?
				consentFacade.getConsentTemplatesWithConsents().stream().filter(ConsentTemplateData::isExposed)
						.collect(Collectors.toList()) :
				consentFacade.getConsentTemplatesWithConsents();
		consentTemplateDataList.setConsentTemplates(consentTemplateDatas);
		return getDataMapper().map(consentTemplateDataList, ConsentTemplateListWsDTO.class, fields);
	}

	@RequestMapping(value = "/consents", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(nickname = "doGiveConsent", value = "A user can give consent.")
	@ApiBaseSiteIdAndUserIdParam
	public ResponseEntity<ConsentTemplateWsDTO> doGiveConsent(
			@ApiParam(value = "Consent template ID.", required = true) @RequestParam final String consentTemplateId,
			@ApiParam(value = "Consent template version.", required = true) @RequestParam final Integer consentTemplateVersion)
	{
		if (getUserFacade().isAnonymousUser())
		{
			throw new ForbiddenException("An anonymous user can't give a consent");
		}

		try
		{
			consentFacade.giveConsent(consentTemplateId, consentTemplateVersion);
		}
		catch (final CommerceConsentGivenException e)
		{
			LOG.warn(e.getMessage(), e);
			throw new AlreadyExistsException(e.getMessage());
		}
		final ConsentTemplateData consentTemplate = consentFacade.getLatestConsentTemplate(consentTemplateId);
		final ConsentTemplateWsDTO consentTemplateWsDto = getDataMapper().map(consentTemplate, ConsentTemplateWsDTO.class);

		final String uriLocation = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").replaceQuery(StringUtils.EMPTY)
				.buildAndExpand(consentTemplateWsDto.getId()).toUriString().replace("/consents", "/consenttemplates");

		return ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.LOCATION, uriLocation).body(consentTemplateWsDto);
	}

	@RequestMapping(value = "/consenttemplates/{consentTemplateId}", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@ApiOperation(nickname = "getConsentTemplate", value = "Fetch the consent.", notes = "If user has not given or withdrawn consent to the template, no given or withdraw date is returned.")
	@ApiBaseSiteIdAndUserIdParam
	public ConsentTemplateWsDTO getConsentTemplate(
			@ApiParam(value = "Consent template id.", required = true) @PathVariable final String consentTemplateId,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{

		final ConsentTemplateData latestConsentTemplate = consentFacade.getLatestConsentTemplate(consentTemplateId);
		if (getUserFacade().isAnonymousUser() && !latestConsentTemplate.isExposed())
		{
			throw new NotFoundException("This consent template is not exposed to anonymous user");
		}
		return getDataMapper().map(latestConsentTemplate, ConsentTemplateWsDTO.class, fields);
	}

	@RequestMapping(value = "/consents/{consentCode}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	@ApiOperation(nickname = "removeConsent", value = "Withdraw the user consent for a given consent code.", notes = "If the user consent was given, the consent is withdrawn. If consent was already withdrawn then returns consent already withdrawn error. If there is no such consent then returns not found. If the current user is an anonymous user then returns access denied error.")
	@ApiBaseSiteIdAndUserIdParam
	public void removeConsent(
			@ApiParam(value = "Consent code.", required = true) @PathVariable(value = "consentCode") final String consentCode)
	{
		if (getUserFacade().isAnonymousUser())
		{
			throw new AccessDeniedException("Anonymous user cannot withdraw consent");
		}

		final String consentNotFoundMessage = "Consent with code [%s] was not found";
		try
		{
			consentFacade.withdrawConsent(consentCode);
		}
		catch (final CommerceConsentWithdrawnException e)
		{
			LOG.warn(e.getMessage(), e);
			throw new ConsentWithdrawnException(e.getMessage(), ConsentWithdrawnException.CONSENT_WITHDRAWN);
		}
		catch (final IllegalArgumentException e)
		{
			LOG.warn(String.format(consentNotFoundMessage, consentCode), e);
			throw new NotFoundException(String.format(consentNotFoundMessage, consentCode));
		}
		catch (final ModelNotFoundException e)
		{
			throw new NotFoundException(String.format(consentNotFoundMessage, consentCode));
		}
		catch (final AccessDeniedException e)
		{
			throw e;
		}
	}

}
