<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="cloudinarymediacore" uri="http://www.hybris.de/jsp/cloudinarymediacore" %>

<c:choose>
 <c:when test="${not empty mediaContainerPk}">
 <cloudinarymediacore:img mediaQualifier="${mediaContainerPk}" format="300Wx300H"/>
</c:when>
<c:otherwise>
<c:choose>
			<c:when test="${empty isResponsiveEnabled || !isResponsiveEnabled}">
				<c:forEach items="${medias}" var="media">
                	<c:if test="${ycommerce:validateUrlScheme(media.url)}">
                		<c:choose>
                			<c:when test="${empty imagerData}">
                				<c:set var="imagerData">"${ycommerce:encodeJSON(media.width)}":"${ycommerce:encodeJSON(media.url)}"</c:set>
                			</c:when>
                			<c:otherwise>
                				<c:set var="imagerData">${imagerData},"${ycommerce:encodeJSON(media.width)}":"${ycommerce:encodeJSON(media.url)}"</c:set>
                			</c:otherwise>
                		</c:choose>
                		<c:if test="${empty altText}">
                			<c:set var="altTextHtml" value="${fn:escapeXml(media.altText)}"/>
                		</c:if>
                	</c:if>
                </c:forEach>
			</c:when>
			<c:otherwise>
				<c:forEach items="${medias}" var="media">
                	<c:if test="${media.format == null}">
                		<c:set var="imagerData">${media.url}</c:set>
                		<c:if test="${empty altText}">
                			<c:set var="altTextHtml" value="${fn:escapeXml(media.altText)}"/>
                		</c:if>
                	</c:if>
                </c:forEach>
			</c:otherwise>
		</c:choose>


<c:url value="${urlLink}" var="simpleResponsiveBannerUrl" />


<c:choose>
		<c:when test="${empty isResponsiveEnabled || !isResponsiveEnabled}">
			<div class="simple-banner banner__component--responsive">
            	<c:set var="imagerDataJson" value="{${imagerData}}"/>
            	<c:choose>
            		<c:when test="${empty simpleResponsiveBannerUrl || simpleResponsiveBannerUrl eq '#' || !ycommerce:validateUrlScheme(simpleResponsiveBannerUrl)}">
            			<img class="js-responsive-image" data-media='${fn:escapeXml(imagerDataJson)}' alt='${altTextHtml}' title='${altTextHtml}' style="">

            		</c:when>
            		<c:otherwise>
            			<a href="${fn:escapeXml(simpleResponsiveBannerUrl)}">
            				<img class="js-responsive-image" data-media='${fn:escapeXml(imagerDataJson)}' title='${altTextHtml}' alt='${altTextHtml}' style="">
            			</a>
            		</c:otherwise>
            	</c:choose>


            </div>
		</c:when>
		<c:otherwise>
			<div class="simple-banner banner__component--responsive">

                        	<c:choose>
                        		<c:when test="${empty simpleResponsiveBannerUrl || simpleResponsiveBannerUrl eq '#' || !ycommerce:validateUrlScheme(simpleResponsiveBannerUrl)}">
                        			<img class="" data-src='${imagerData}' alt='${altTextHtml}' title='${altTextHtml}' style="">

                        		</c:when>
                        		<c:otherwise>
                        			<a href="${fn:escapeXml(simpleResponsiveBannerUrl)}">
                        				<img class="" data-src='${imagerData}' title='${altTextHtml}' alt='${altTextHtml}' style="">
                        			</a>
                        		</c:otherwise>
                        	</c:choose>


                        </div>
		</c:otherwise>
	</c:choose>
</c:otherwise>
</c:choose>