<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<body>

<link href="https://unpkg.com/cloudinary-video-player@1.5.1/dist/cld-video-player.min.css" rel="stylesheet">
<script src="https://unpkg.com/cloudinary-core@latest/cloudinary-core-shrinkwrap.min.js" type="text/javascript"></script>
<script src="https://unpkg.com/cloudinary-video-player@1.5.1/dist/cld-video-player.min.js"
    type="text/javascript"></script>

     <c:choose>
    	<c:when test="${showComponent}">
    	   <video id="cloudinaryVideoPlayer"
                  controls
                  muted
                  class="cld-video-player cld-video-player-skin-dark">
           </video>
           <script type="text/javascript">
            var cld = cloudinary.Cloudinary.new({ cloud_name: "${cloudName}", secure: true});
            var player = cld.videoPlayer('cloudinaryVideoPlayer', ${playerJsonData});
            player.source({
                publicId: "${componentVideo.cloudinaryPublicId}",
                sourceTypes: ${sourceJsonData},
                transformation: [{ raw_transformation: ${transformationString} }]
              });
           </script>
        </c:when>
        <c:otherwise></c:otherwise>
   </c:choose>
</body>
</html>