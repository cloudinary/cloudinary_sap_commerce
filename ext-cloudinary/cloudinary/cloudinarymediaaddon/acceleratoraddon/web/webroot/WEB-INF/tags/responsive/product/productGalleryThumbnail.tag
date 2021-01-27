<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="galleryImages" required="true" type="java.util.List" %>

<div class="carousel gallery-carousel js-gallery-carousel hidden-xs hidden-sm">
    <c:forEach items="${galleryImages}" var="container" varStatus="varStatus">
    <c:choose>
                                <c:when test="${empty isResponsiveEnabled || !isResponsiveEnabled}">
                                <a href="#" class="item"> <img class="lazyOwl" data-src="${fn:escapeXml(container.thumbnail.url)}" alt="${fn:escapeXml(container.thumbnail.altText)}"></a>
                                </c:when>
                                <c:otherwise>
                                <a href="#" class="item"> <img class="cld-responsive" data-src="${fn:escapeXml(container.thumbnail.url)}" alt="${fn:escapeXml(container.thumbnail.altText)}"></a>
                                </c:otherwise>
                            </c:choose>
    </c:forEach>
</div>
