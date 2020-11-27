<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>


<script src="https://product-gallery.cloudinary.com/all.js"></script>

<spring:htmlEscape defaultHtmlEscape="true" />

<input id="cloud_name" type="hidden" value="${cloudName}"/>
<input id="product_code" type="hidden" value="${productCode}"/>
<input id="image_height" type="hidden" value="${configHeight}"/>
<input id="image_width" type="hidden" value="${configWidth}"/>
<input id="image_transformation" type="hidden" value="${galleryTransformation}"/>

<div class="image-gallery js-gallery">
    <span class="image-gallery__zoom-icon glyphicon glyphicon-resize-full"></span>
    <div id="my-gallery" style="max-width:80%;margin:auto">
    </div>
</div>

<script type="text/javascript">
 var cloudName = document.getElementById("cloud_name").value;
 var productCode = document.getElementById("product_code").value;
 var height = document.getElementById("image_height").value;
 var width = document.getElementById("image_width").value;
 var transformationString = document.getElementById("image_transformation").value;

  if(height == ""){
    height = 100;
   }
  if(width == ""){
      width = 100;
     }
  if(transformationString == ""){
        transformationString = "{'crop' : 'fill'}";
       }

  const myGallery= cloudinary.galleryWidget({
    container: "#my-gallery",
    cloudName: cloudName,
    mediaAssets: [
      {tag: productCode, transformation: JSON.parse(transformationString.replaceAll('\'',"\""))}, // by default mediaType: "image"
      {tag: productCode, mediaType: "video"},
      {tag: productCode, mediaType: "spin"}
    ],
    thumbnailProps: {
              width: parseInt(height),
              height: parseInt(width)
              },
    carouselLocation: 'bottom'
  });

  myGallery.render();
</script


