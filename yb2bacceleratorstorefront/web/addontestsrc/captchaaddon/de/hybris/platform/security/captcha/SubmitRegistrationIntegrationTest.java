/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.security.captcha;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.security.captcha.controllers.MySubmitRegistrationController;

import de.hybris.platform.store.services.BaseStoreService;
import jakarta.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:captchaaddon/web/spring/captchaaddon-web-spring.xml")
@ComponentScan
@Ignore
public class SubmitRegistrationIntegrationTest
{
	@MockitoBean
	private ReCaptchaAspect reCaptchaAspect;

	@MockitoBean
	private SiteConfigService siteConfigService;

	@MockitoBean
	private BaseStoreService baseStoreService;

	@Resource
	private MySubmitRegistrationController controller;

	@Resource
	private ApplicationContext context;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		((ConfigurableApplicationContext) context).getBeanFactory()
				.registerSingleton("reCaptchaAspect", reCaptchaAspect);
		((ConfigurableApplicationContext) context).getBeanFactory()
				.registerSingleton("siteConfigService", siteConfigService);
		((ConfigurableApplicationContext) context).getBeanFactory()
				.registerSingleton("baseStoreService", baseStoreService);
	}

	@Test
	public void testReCaptchaAspectIsUsed_submitRegistration() throws Throwable
	{
		reset(reCaptchaAspect);

		controller.submitRegistration();

		verify(reCaptchaAspect).advise(any());

	}
}
