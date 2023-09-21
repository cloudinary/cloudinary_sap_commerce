<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<body>

<link href="https://unpkg.com/cloudinary-video-player@1.9.9/dist/cld-video-player.min.css" rel="stylesheet">
<script src="https://unpkg.com/cloudinary-core@latest/cloudinary-core-shrinkwrap.min.js" type="text/javascript"></script>
<script src="https://unpkg.com/cloudinary-video-player@1.9.9/dist/cld-video-player.min.js"
    type="text/javascript"></script>

<video
  id="cloudinaryVideoPlayer"
  controls
  muted
  class="cld-video-player cld-video-player-skin-dark"
  data-cld-source-types='["mp4", "ogg", "webm"]'
  data-cld-public-id="${componentVideo.cloudinaryPublicId}"
  data-cld-source-transformation= '${transformation}'>
</video>

<script type="text/javascript">
    var cld = cloudinary.Cloudinary.new({ cloud_name: "${cloudName}", secure: true});
    var player = cld.videoPlayer('cloudinaryVideoPlayer');
</script>

</body>
</html>