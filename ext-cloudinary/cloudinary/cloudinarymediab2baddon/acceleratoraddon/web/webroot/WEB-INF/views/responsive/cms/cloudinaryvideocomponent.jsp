<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<body>

<link href="https://unpkg.com/cloudinary-video-player@1.9.9/dist/cld-video-player.min.css" rel="stylesheet">
<script src="https://unpkg.com/cloudinary-core@latest/cloudinary-core-shrinkwrap.min.js" type="text/javascript"></script>
<script src="https://unpkg.com/cloudinary-video-player@1.9.9/dist/cld-video-player.min.js"
    type="text/javascript"></script>

     <c:choose>
    	<c:when test="${showComponent}">
    	   <video id="cloudinaryVideoPlayer" playsinline controls muted autoplay class="cld-video-player">
           </video>
           <script type="text/javascript">
               var cld = cloudinary.Cloudinary.new({ cloud_name: "${cloudName}", secure: true});
               var player = cld.videoPlayer('cloudinaryVideoPlayer', ${playerJsonData});
               player.source(${sourceJsonData});
           </script>
        </c:when>
        <c:otherwise></c:otherwise>
   </c:choose>
</body>
</html>