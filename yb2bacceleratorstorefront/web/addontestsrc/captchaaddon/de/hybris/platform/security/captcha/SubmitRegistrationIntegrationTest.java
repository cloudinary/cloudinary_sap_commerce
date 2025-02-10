/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.security.captcha;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.security.captcha.controllers.MySubmitRegistrationController;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:captchaaddon/web/spring/captchaaddon-web-spring.xml",
	"classpath:captchaaddon/test/captchaaddon-mock-test-spring.xml"})
@ComponentScan
public class SubmitRegistrationIntegrationTest
{
	@Resource // the bean is mocked in captchaaddon-mock-test-spring.xml
	private ReCaptchaAspect reCaptchaAspect;

	@Resource
	private MySubmitRegistrationController controller;

	@Test
	public void testReCaptchaAspectIsUsed_submitRegistration() throws Throwable
	{
		reset(reCaptchaAspect);

		controller.submitRegistration();

		verify(reCaptchaAspect).advise(any());

	}
}
