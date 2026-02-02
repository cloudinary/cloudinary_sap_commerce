<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:url value="/login/register/termsandconditions" var="getTermsAndConditionsUrl"/>

<div class="user-register__headline">
	<spring:theme code="register.new.customer" />
</div>
<p>
	<spring:theme code="register.description" />
</p>

<form:form method="post" modelAttribute="registerForm" action="${action}">
	<formElement:formSelectBoxDefaultEnabled idKey="register.title"
		labelKey="register.title" selectCSSClass="form-control"
		path="titleCode" mandatory="true" skipBlank="false"
		skipBlankMessageKey="form.select.none" items="${titles}" />
	<formElement:formInputBox idKey="register.firstName"
		labelKey="register.firstName" path="firstName" inputCSS="form-control"
		mandatory="true" />
	<formElement:formInputBox idKey="register.lastName"
		labelKey="register.lastName" path="lastName" inputCSS="form-control"
		mandatory="true" />
	<c:choose>
	<c:when test="${registerForm.otpForRegistrationEnabled}">
		<label class="control-label" for="email">
			<spring:theme code="register.email" />
		</label>
		<form:input name="email" id="email" path="email" class="form-control"
			value="${registerForm.email}" required="required"/>
		<div name="helpMessage" id="helpMessage" path="helpMessage" class="help-block display-none">
			<spring:theme code="register.email.invalid" />
		</div>
		<form:input name="otpForRegistrationEnabled" id="otpForRegistrationEnabled" path="otpForRegistrationEnabled" type="hidden"
			value="${registerForm.otpForRegistrationEnabled}"/>
		<input name="secondsForRegistration" id="secondsForRegistration" path="secondsForRegistration" type="hidden"
			value="${secondsForRegistration}"/>
		<form:input name="verificationTokenId" id="verificationTokenId" path="verificationTokenId" type="hidden"
			value="${registerForm.verificationTokenId}"/>
		<div class="send-verification-token-for-registration">
			<div class="row">
				<div class="col-xs-11">
					<formElement:formInputBox idKey="register.otp.token"
						labelKey="register.otp.token" path="verificationTokenCode" inputCSS="form-control"
						mandatory="true" />
					<div class="send_otp_token_for_registration_error_info">
						<span class="help-block"><spring:theme code="otp.token.rate.limit.error" /></span>
					</div>
				</div>
				<div class="col-xs-1 send-button-container">
					<a href="#" class="send-verification-token-for-registration-link disabled-link">
						<spring:theme code="register.button.send.otp.token" />
					</a>
					<a href="#" class="resend-verification-token-for-registration-link disabled-link">
						<spring:theme code="register.button.resend.otp.token" />
					</a>
				</div>
			</div>
			<div class="sent_otp_token_for_registration_info">
				<div class="green-circle">
					<span class="glyphicon glyphicon-ok"></span>
				</div>
					<span class="info-text"><spring:theme code="register.info.sent.otp.token"/></span>
			</div>
		</div>
	</c:when>
	<c:otherwise>
	    <formElement:formInputBox idKey="register.email"
		    labelKey="register.email" path="email" inputCSS="form-control"
		     mandatory="true" />
	   </c:otherwise>
    </c:choose>
	<formElement:formPasswordBox idKey="password" labelKey="register.pwd"
		path="pwd" inputCSS="form-control password-strength" mandatory="true" />
	<formElement:formPasswordBox idKey="register.checkPwd"
		labelKey="register.checkPwd" path="checkPwd" inputCSS="form-control"
		mandatory="true" />
    <c:if test="${ not empty consentTemplateData }">
        <form:hidden path="consentForm.consentTemplateId" value="${consentTemplateData.id}" />
        <form:hidden path="consentForm.consentTemplateVersion" value="${consentTemplateData.version}" />
        <div class="checkbox">
            <label class="control-label uncased">
                <form:checkbox path="consentForm.consentGiven" disabled="true"/>
                <c:out value="${consentTemplateData.description}" />

            </label>
        </div>
		<div class="help-block">
			<spring:theme code="registration.consent.link" />
		</div>

    </c:if>

	<spring:theme code="register.termsConditions" arguments="${getTermsAndConditionsUrl}" var="termsConditionsHtml" htmlEscape="false" />
	<template:errorSpanField path="termsCheck">
		<div class="checkbox">
			<label class="control-label uncased">
				<form:checkbox id="registerChkTermsConditions" path="termsCheck" disabled="true"/>
				${ycommerce:sanitizeHTML(termsConditionsHtml)}
			</label>
		</div>
	</template:errorSpanField>

	<input type="hidden" id="recaptchaChallangeAnswered"
		value="${fn:escapeXml(requestScope.recaptchaChallangeAnswered)}" />
	<div class="form_field-elements control-group js-recaptcha-captchaaddon"></div>
	<div class="form-actions clearfix">
		<ycommerce:testId code="register_Register_button">
			<button type="submit" class="btn btn-default btn-block" disabled="disabled">
				<spring:theme code='${actionNameKey}' />
			</button>
		</ycommerce:testId>
	</div>

</form:form>
