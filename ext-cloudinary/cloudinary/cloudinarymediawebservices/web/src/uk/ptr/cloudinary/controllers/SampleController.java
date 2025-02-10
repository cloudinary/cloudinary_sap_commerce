/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.controllers;

import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.constants.WebservicescommonsConstants;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.pagination.WebPaginationUtils;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Sets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jersey.repackaged.com.google.common.collect.Lists;
import uk.ptr.cloudinary.data.UserData;
import uk.ptr.cloudinary.data.UserDataList;
import uk.ptr.cloudinary.dto.SampleWsDTO;
import uk.ptr.cloudinary.dto.TestMapWsDTO;
import uk.ptr.cloudinary.dto.UserWsDTO;
import uk.ptr.cloudinary.dto.UsersListWsDTO;
import uk.ptr.cloudinary.dto.UsersPageWsDTO;
import uk.ptr.cloudinary.facades.SampleFacades;

import static uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants.CLIENT_CREDENTIAL_AUTHORIZATION_NAME;
import static uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants.PASSWORD_AUTHORIZATION_NAME;
import static uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants.SAMPLE_LIST_DOUBLE_VALUE;
import static uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants.SAMPLE_LIST_STRING_VALUE;


/**
 * Sample Controller
 */
@Controller
@RequestMapping(value = "/sample")
@Tag(name = "Sample")
public class SampleController
{
	public static final String DEFAULT_FIELD_SET = "DEFAULT";
	private static final String USER_MAPPING = "firstName,lastName,description";

	@Resource
	private SampleFacades sampleFacades;

	@Resource(name = "sampleWsDTOValidator")
	private Validator sampleWsDTOValidator;

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	@Resource
	private WebPaginationUtils webPaginationUtils;

	@Autowired(required = false)
	private SampleWsDTO testBean;

	/**
	 * Sample method returning Cache-Control header and using Path Variable</br>
	 * Example :</br>
	 * GET http://localhost:9001/cloudinarymediawebservices/sample/dto/sampleValue
	 *
	 * @param pathVariable Sample path variable parameter
	 * @return SampleWsDTO object filled with pathVariable value
	 */
	@GetMapping(value = "/dto/{pathVariable}", produces = {"application/json", "application/xml"})
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
	@ResponseBody
	@Operation(operationId = "getSample", summary = "Method with path parameter returns sample DTO", description = "Sample method returning Cache-Control header and using Path Variable", security = @SecurityRequirement(name = "oauth", scopes = {CLIENT_CREDENTIAL_AUTHORIZATION_NAME, PASSWORD_AUTHORIZATION_NAME}))
	public SampleWsDTO getSample(
			@Parameter(description = "Sample path variable parameter. It should be returned in response DTO", required = true) @PathVariable final String pathVariable)
	{
		return sampleFacades.getSampleWsDTO(YSanitizer.sanitize(pathVariable));
	}

	/**
	 * Sample method showing how to validate object given in POST body parameter<br/>
	 * Example :</br>
	 * URL : http://localhost:9001/cloudinarymediawebservices/sample/dto</br>
	 * Method : POST</br>
	 * Header : Content-Type=application/json</br>
	 * POST body parameter :{ "value" : "sampleValue"}</br>
	 *
	 * @param sampleWsDTO - Request body parameter (DTO in xml or json format)</br>
	 * @return - The same object, which was send in POST body</br>
	 */
	@PostMapping(value = "/dto", consumes = {"application/json", "application/xml"}, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@Operation(operationId = "createSample", summary = "Method with body parameter", description = "Sample method handling POST body parameter", security = @SecurityRequirement(name = "oauth", scopes = {CLIENT_CREDENTIAL_AUTHORIZATION_NAME, PASSWORD_AUTHORIZATION_NAME}))
	public SampleWsDTO createSample(
			@Parameter(description = "Sample request body parameter (DTO in xml or json format). It should be returned as response DTO", required = true) @RequestBody final SampleWsDTO sampleWsDTO)
	{
		validate(sampleWsDTO, "sampleWsDTO", sampleWsDTOValidator);
		return sampleWsDTO;
	}

	protected void validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}

	/**
	 * Request handler for list response. Retrieves all user from userService and maps Collection of UserModel to
	 * UserListWsDTO Mapping is done according to configuration in WEB-INF/config/field-mapping.xml Sample url's:
	 * <ul>
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users?fields=users(info)
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users?fields=users(BASIC)
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users?fields=users(DEFAULT)
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users?fields=users(FULL)
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users?fields=users(firstName,addresses(street))
	 * </ul>
	 */
	@GetMapping(value = "/users", produces = {"application/json", "application/xml"})
	@ResponseBody
	@Operation(operationId = "getUsersList", summary = "Get users", description = "Sample method returning user list.", security = @SecurityRequirement(name = "oauth", scopes = {CLIENT_CREDENTIAL_AUTHORIZATION_NAME, PASSWORD_AUTHORIZATION_NAME}))
	public UsersListWsDTO getUsersList(
			@ApiFieldsParam(examples = "users(firstName,addresses(street)") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<UserData> users = sampleFacades.getUsers();
		final UserDataList userList = new UserDataList();
		userList.setUsers(users);
		return dataMapper.map(userList, UsersListWsDTO.class, fields);
	}

	/**
	 * Request handler for paginated response. Retrieves user from userService and maps result to UsersPageWsDTO Mapping is
	 * done according to configuration in WEB-INF/config/field-mapping.xml Sample url's:
	 * <ul>
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/usersPaged
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/usersPaged?currentPage=1
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/usersPaged?currentPage=0?pageSize=1
	 * </ul>
	 */
	@GetMapping(value = "/usersPaged", produces = {"application/json", "application/xml"})
	@ResponseBody
	@Operation(operationId = "getUsers", summary = "Get users", description = "Sample method returning user page.", security = @SecurityRequirement(name = "oauth", scopes = {CLIENT_CREDENTIAL_AUTHORIZATION_NAME, PASSWORD_AUTHORIZATION_NAME}))
	@Parameter(name = WebservicescommonsConstants.PAGE_SIZE, description = "Page size", required = false, schema = @Schema(type = "int"), in = ParameterIn.QUERY)
	@Parameter(name = WebservicescommonsConstants.CURRENT_PAGE, description = "Current page number", required = false, schema = @Schema(type = "int"), in = ParameterIn.QUERY)
	@Parameter(name = WebservicescommonsConstants.NEEDS_TOTAL, description = "Request total count", required = false, schema = @Schema(type = "boolean"), in = ParameterIn.QUERY)
	public UsersPageWsDTO getUsers(
			@ApiFieldsParam @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields,
			@Parameter(hidden = true) @RequestParam final Map<String, String> params)
	{
		final SearchPageData<Object> pageData = webPaginationUtils.buildSearchPageData(params);

		final SearchPageData<UserData> users = sampleFacades.getUsers(pageData);
		return dataMapper.map(users, UsersPageWsDTO.class, fields);
	}

	/**
	 * Request handler for particular user. Retrieves single user from userService and maps UserModel to UserWsDTO Mapping
	 * is done according to configuration in WEB-INF/config/field-mapping.xml Sample url's:
	 * <ul>
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users/user1
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users/user1?fields=info
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users/user1?fields=BASIC
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users/user1?fields=DEFAULT
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users/user1?fields=FULL
	 * <li>http://localhost:9001/cloudinarymediawebservices/sample/users/user1?fields=firstName,addresses(street)
	 * </ul>
	 */
	@GetMapping(value = "/users/{id}", produces = {"application/json", "application/xml"})
	@ResponseBody
	@Operation(operationId = "getUser", summary = "Get user information", description = "Sample method returning user information. Operation permitted for ROLE_CLIENT", security = @SecurityRequirement(name = "oauth", scopes = CLIENT_CREDENTIAL_AUTHORIZATION_NAME))
	public UserWsDTO getUser(@Parameter(description = "User identifier", required = true) @PathVariable final String id,
			@ApiFieldsParam(examples = "firstName,addresses(street)") @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final UserData data = sampleFacades.getUser(id);
		return dataMapper.map(data, UserWsDTO.class, fields);
	}

	/**
	 * Sample method for updating data of particular user using PATCH request.
	 *
	 * @param userId User identifier
	 * @param user   Username and info as DTO in xml or json format
	 */
	@PatchMapping(value = "/users/{userId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	@ResponseStatus(HttpStatus.OK)
	@Operation(operationId = "updateUser", summary = "Update username and info", description = "Sample method updating username and info", security = @SecurityRequirement(name = "oauth", scopes = {CLIENT_CREDENTIAL_AUTHORIZATION_NAME, PASSWORD_AUTHORIZATION_NAME}))
	public void updateUser(@Parameter(description = "User identifier", required = true) @PathVariable final String userId,
			@Parameter(description = "User data as DTO in xml or json format", required = true) @RequestBody final UserWsDTO user)
	{
		final UserData userData = sampleFacades.getUser(userId);
		if (userData == null)
		{
			throw new UnknownIdentifierException("Cannot find user with id '" + YSanitizer.sanitize(userId) + "'");
		}
		dataMapper.map(user, userData, USER_MAPPING, false);
		sampleFacades.updateUser(userId, userData);
	}

	/**
	 * Request handler for getting object with map. Created to test adapters for particular fields.
	 * <p>
	 * Url: http://localhost:9001/cloudinarymediawebservices/sample/map
	 */
	@GetMapping(value = "/map", produces = {"application/json", "application/xml"})
	@ResponseBody
	@Operation(operationId = "getTestMap", summary = "Get object with map inside", description = "Sample method returning object with map.")
	public TestMapWsDTO getTestMap()
	{
		return sampleFacades.getMap();
	}


	@PostMapping(value = "plain/string")
	@ResponseBody
	@Operation(operationId = "doProcessString", summary = "Process String object", description = "Sample method returning string object")
	public String doProcessString(
			@Parameter(description = "Request parameter which will be changed to response string", required = true) @RequestBody final String val)
	{
		return YSanitizer.sanitize(val) + "1";
	}


	@PostMapping(value = "plain/long")
	@ResponseBody
	@Operation(operationId = "doProcessLong", summary = "Process Long object", description = "Sample method returning Long object")
	public Long doProcessLong(
			@Parameter(description = "Request param which will be used to calculate response value", required = true) @RequestBody final Long value)
	{
		return value < Long.MAX_VALUE ? (value + 1L) : value;
	}


	@PostMapping(value = "plain/double")
	@ResponseBody
	@Operation(operationId = "doProcessDouble", summary = "Process Double object", description = "Sample method returning string object")
	public double doProcessDouble(
			@Parameter(description = "Request param which will be used to calculate response value", required = true) @RequestBody final double value)
	{
		return value < Double.MAX_VALUE ? (value + 1) : value;
	}


	@GetMapping(value = "plain/list")
	@ResponseBody
	@Operation(operationId = "getList", summary = "Get list object", description = "Sample method returning list object.")
	public List<Object> getList()
	{
		return Lists.newArrayList(SAMPLE_LIST_STRING_VALUE, SAMPLE_LIST_DOUBLE_VALUE);
	}

	@GetMapping(value = "plain/map")
	@ResponseBody
	@Operation(operationId = "getMap", summary = "Get map object", description = "Sample method returning map object.")
	public Map<String, Object> getMap()
	{
		final Map<String, Object> map = new HashMap<>();
		map.put("a", "Ala");
		map.put("b", 1);
		map.put("c", Sets.newHashSet("a", "b", "c"));
		return map;
	}

	@GetMapping(value = "/testBean")
	@ResponseBody
	@Operation(operationId = "getTestBeanText", summary = "Get bean object", description = "Sample method returning bean object.")
	public SampleWsDTO getTestBeanText()
	{
		return testBean;
	}
}
