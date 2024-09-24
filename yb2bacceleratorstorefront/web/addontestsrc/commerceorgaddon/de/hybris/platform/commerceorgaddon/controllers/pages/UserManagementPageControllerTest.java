/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceorgaddon.controllers.pages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessage;
import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.ProfileValidator;
import de.hybris.platform.b2bcommercefacades.company.B2BUserGroupFacade;
import de.hybris.platform.cms2.data.PagePreviewCriteriaData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceorgaddon.breadcrumb.impl.MyCompanyBreadcrumbBuilder;
import de.hybris.platform.commerceorgaddon.controllers.ControllerConstants;
import de.hybris.platform.commerceorgaddon.forms.B2BCustomerForm;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;


@UnitTest
public class UserManagementPageControllerTest
{
	private static final String TITLE_FOR_PAGE = "User Management Page Test Title";
	private static final String CMS_PAGE_MODEL = "cmsPage";
	private static final String ERROR_MESSAGES_HOLDER = "accErrorMsgs";

	@InjectMocks
	private UserManagementPageController controller;

	@Mock
	private BindingResult bindingResult;
	@Mock
	private HttpServletRequest request;
	@Mock
	private RedirectAttributes redirectModel;
	@Mock
	private ProfileValidator profileValidator;
	@Mock
	private TitleData titleData;
	@Mock
	private MyCompanyBreadcrumbBuilder myCompanyBreadcrumbBuilder;
	@Mock
	private ContentPageModel contentPageModel;
	@Mock
	private I18NService i18NService;
	@Mock
	private CMSPageService cmsPageService;
	@Mock
	private MessageSource messageSource;
	@Mock
	private PageTitleResolver pageTitleResolver;
	@Mock
	private CMSPreviewService cmsPreviewService;
	@Mock
	private AbstractPageModel abstractPageModel;
	@Mock
	private PageTemplateModel pageTemplateModel;
	@Mock
	private UserFacade userFacade;
	@Mock
	private B2BUserGroupFacade b2bUserGroupFacade;
	@Mock
	private Breadcrumb breadcrumb;

	private final PagePreviewCriteriaData pagePreviewCriteriaData = new PagePreviewCriteriaData();
	private final Model model = Mockito.spy(new BindingAwareModelMap());


	@Before
	public void prepare() throws CMSItemNotFoundException
	{
		MockitoAnnotations.initMocks(this);
		final Locale locale = new Locale("en");
		final List<Breadcrumb> breadcrumbsList = Lists.newArrayList(breadcrumb);

		BDDMockito.given(myCompanyBreadcrumbBuilder.createManageUserBreadcrumb()).willReturn(breadcrumbsList);
		BDDMockito.given(i18NService.getCurrentLocale()).willReturn(locale);
		BDDMockito.given(cmsPageService.getPageForLabelOrId(Mockito.anyString(), Mockito.anyObject())).willReturn(contentPageModel);
		BDDMockito.given(messageSource.getMessage(Mockito.anyString(), Mockito.any(Object[].class), Mockito.eq(locale)))
				.willReturn("ANY STRING");
		BDDMockito.given(pageTitleResolver.resolveContentPageTitle(Mockito.anyString())).willReturn(TITLE_FOR_PAGE);
		BDDMockito.given(model.containsAttribute(CMS_PAGE_MODEL)).willReturn(Boolean.TRUE);
		BDDMockito.given(cmsPreviewService.getPagePreviewCriteria()).willReturn(pagePreviewCriteriaData);
		BDDMockito.given(model.containsAttribute("b2BCustomerForm")).willReturn(Boolean.TRUE);
		BDDMockito.given(abstractPageModel.getMasterTemplate()).willReturn(pageTemplateModel);

		BDDMockito.given(userFacade.getTitles()).willReturn(Collections.singletonList(titleData));
		BDDMockito.given(b2bUserGroupFacade.getUserGroups()).willReturn(Collections.singletonList("test"));
	}

	@Test
	public void testCreateUserHasBindingErrors() throws CMSItemNotFoundException
	{
		final B2BCustomerForm b2BCustomerForm = new B2BCustomerForm();
		doNothing().when(profileValidator).validate(b2BCustomerForm, bindingResult);
		when(bindingResult.hasErrors()).thenReturn(true);

		final String createUserPage = controller.createUser(b2BCustomerForm, bindingResult, model, redirectModel);

		Mockito.verify(model, times(2)).addAttribute("action", "manageUsers");
		Mockito.verify(model).addAttribute(b2BCustomerForm);

		assertEquals("form.global.error", ((List<GlobalMessage>) model.getAttribute(ERROR_MESSAGES_HOLDER)).get(0).getCode());
		assertEquals(ControllerConstants.Views.Pages.MyCompany.MyCompanyManageUserAddEditFormPage, createUserPage);
	}
}
