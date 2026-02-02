<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:choose>
    <c:when test="${header.accept=='application/xml'}">
<% response.setContentType("application/xml"); %>
<?xml version='1.0' encoding='UTF-8'?>
<errors>
   <error>
      <message>There is no resource for path ${requestScope['jakarta.servlet.forward.request_uri']}</message>
      <type>UnknownResourceError</type>
   </error>
</errors>
</c:when>
    <c:otherwise><% response.setContentType("application/json"); %>{
   "errors" : [ {
      "message": "There is no resource for path ${requestScope['jakarta.servlet.forward.request_uri']}",
      "type": "UnknownResourceError"
   } ]
}
</c:otherwise>
</c:choose>
