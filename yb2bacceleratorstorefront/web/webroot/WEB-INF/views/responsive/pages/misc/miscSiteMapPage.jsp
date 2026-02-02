<%@ page contentType="text/plain" language="java" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<?xml version="1.0" encoding="UTF-8"?>
<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
	<c:forEach items="${siteMapUrls}" var="loc">
		<sitemap>
			<loc>${fn:escapeXml(loc)}</loc>
		</sitemap>
	</c:forEach>
</sitemapindex>

