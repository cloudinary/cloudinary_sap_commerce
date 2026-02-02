<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>

<c:if test="${empty showAddToCart ? ycommerce:checkIfPickupEnabledForStore() : showAddToCart and ycommerce:checkIfPickupEnabledForStore() and product.availableForPickup}">
	<c:set var="actionUrl" value="${fn:replace(url,'{productCode}', ycommerce:encodeUrl(product.code))}" scope="request"/>
	<storepickup:clickPickupInStore product="${product}" cartPage="false"/>
	<storepickup:pickupStorePopup/>
	<c:remove var="actionUrl"/>
</c:if>



