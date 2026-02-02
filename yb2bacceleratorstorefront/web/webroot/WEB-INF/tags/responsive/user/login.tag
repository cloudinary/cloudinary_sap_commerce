<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="actionNameKey" required="true" type="java.lang.String"%>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="hideDescription" value="checkout.login.loginAndCheckout" />

<div class="login-page__headline">
	<spring:theme code="login.title" />
</div>

<c:if test="${actionNameKey ne hideDescription}">
	<p>
		<spring:theme code="login.description" />
	</p>
</c:if>

<form:form action="${action}" method="post" modelAttribute="loginForm">
	<c:if test="${not empty message}">
		<span class="has-error"> <spring:theme code="${message}" />
		</span>
	</c:if>

	<c:choose>
		<c:when test="${loginForm.otpVerificationTokenEnabled}">
			<label class="control-label" for="otpUserName">
				<spring:theme code="login.email" />
			</label>
			<form:input name="otpUserName" id="otpUserName" path="otpUserName" class="form-control"
			            value="${loginForm.otpUserName}"/>
			<form:input name="lastOtpUserName" id="lastOtpUserName" path="lastOtpUserName" type="hidden"/>

			<div name="helpMessage" id="helpMessage" path="helpMessage" class="help-block display-none">
				<spring:theme code="register.email.invalid" />
			</div>

			<label class="control-label" for="otpPassword">
				<spring:theme code="login.password" />
			</label>
			<form:password name="otpPassword" id="otpPassword" path="otpPassword" class="form-control"/>

			<div class="forgotten-password">
				<ycommerce:testId code="login_forgotPassword_link">
					<a href="#" data-link="<c:url value='/login/pw/request'/>" class="js-password-forgotten"
					data-cbox-title="<spring:theme code="forgottenPwd.title"/>">
						<spring:theme code="login.link.forgottenPwd" />
					</a>
				</ycommerce:testId>
			</div>
			<input name="secondsForLogin" id="secondsForLogin" path="secondsForLogin" type="hidden"
				value="${secondsForLogin}"/>
			<form:input name="j_username" id="j_username" path="j_username" type="hidden"
			            value="${loginForm.j_username}"/>
			<label class="control-label" for="j_password">
				<spring:theme code="login.otp.token" />
			</label>
			<div class="send-verification-token">
				<div class="row">
					<div class="col-xs-11">
						<form:input name="j_password" id="j_password" path="j_password" class="form-control"/>
						<div class="send_otp_token_for_error_info">
							<span class="help-block"><spring:theme code="otp.token.rate.limit.error" /></span>
						</div>
					</div>
					<div class="col-xs-1 send-button-container">
						<a href="#" class="send-verification-token-link disabled-link">
						   <spring:theme code="login.button.send.otp.token" />
						</a>
						<a href="#" class="resend-verification-token-link disabled-link">
							<spring:theme code="login.button.resend.otp.token" />
						</a>
					</div>
				</div>
				<div class="sent_otp_token_info">
					<div class="green-circle">
					  <span class="glyphicon glyphicon-ok"></span>
					</div>
					<span class="info-text"><spring:theme code="login.info.sent.otp.token"/></span>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<formElement:formInputBox idKey="j_username" labelKey="login.email"
						path="j_username" mandatory="true" />
			<formElement:formPasswordBox idKey="j_password"
				labelKey="login.password" path="j_password" inputCSS="form-control"
				mandatory="true" />

			<div class="forgotten-password">
				<ycommerce:testId code="login_forgotPassword_link">
					<a href="#" data-link="<c:url value='/login/pw/request'/>" class="js-password-forgotten"
					data-cbox-title="<spring:theme code="forgottenPwd.title"/>">
						<spring:theme code="login.link.forgottenPwd" />
					</a>
				</ycommerce:testId>
			</div>
		</c:otherwise>
	</c:choose>

	<ycommerce:testId code="loginAndCheckoutButton">
		<button type="submit" class="btn btn-primary btn-block">
			<spring:theme code="${actionNameKey}" />
		</button>
	</ycommerce:testId>

	<c:if test="${expressCheckoutAllowed}">
		<button type="submit" class="btn btn-default btn-block expressCheckoutButton"><spring:theme code="text.expresscheckout.header" /></button>
		<input id="expressCheckoutCheckbox" name="expressCheckoutEnabled" type="checkbox" class="form left doExpressCheckout display-none" />
	</c:if>

</form:form>

