<%@ page trimDirectiveWhitespaces="true"%>

<script src="https://product-gallery.cloudinary.com/all.js"></script>

<spring:htmlEscape defaultHtmlEscape="true" />

<input id="cloud_name" type="hidden" value="${cloudName}"/>
<input id="product_code" type="hidden" value="${sapCCProductCode}"/>
<input id="image_height" type="hidden" value="${cloudinaryConfig.cloudinaryCarouselHeight}"/>
<input id="image_width" type="hidden" value="${cloudinaryConfig.cloudinaryCarouselWidth}"/>
<input id="image_transformation" type="hidden" value="${cloudinaryConfig.transformations}"/>
<input id="image_carouselLocation" type="hidden" value="${cloudinaryConfig.cloudinaryCarouselLocation}"/>
<input id="image_carouselOffset" type="hidden" value="${cloudinaryConfig.cloudinaryCarouselOffset}"/>
<input id="image_zoomType" type="hidden" value="${cloudinaryConfig.cloudinaryZoomType}"/>
<input id="image_zoomTrigger" type="hidden" value="${cloudinaryConfig.cloudinaryZoomTrigger}"/>

<div class="image-gallery js-gallery">
    <span class="image-gallery__zoom-icon glyphicon glyphicon-resize-full"></span>
    <div id="my-gallery" style="max-width:80%;margin:auto">
    </div>
</div>

<script type="text/javascript">
  var cloudName = document.getElementById("cloud_name").value;
  var productCode = document.getElementById("product_code").value;
  var transformationString = document.getElementById("image_transformation").value;
  var height = document.getElementById("image_height").value;
  var width = document.getElementById("image_width").value;
  var carouselLocation = document.getElementById("image_carouselLocation").value;
  var carouselOffset = document.getElementById("image_carouselOffset").value;
  var zoomType = document.getElementById("image_zoomType").value;
  var zoomTrigger = document.getElementById("image_zoomTrigger").value;

   if(height == ""){
     height = 100;
    }
   if(width == ""){
       width = 100;
    }
   if(transformationString == ""){
         transformationString = "{'crop' : 'fill'}";
    }
   if(carouselLocation == ""){
         carouselLocation = 'bottom';
    }
    if(carouselOffset == ""){
         carouselOffset = 5;
    }
    if(zoomType == ""){
         zoomType = "inline";
    }
    if(zoomTrigger == ""){
        zoomTrigger = "click";
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
     carouselLocation: carouselLocation,
     carouselOffset: parseInt(carouselOffset),
     zoomProps: {
              type: zoomType,
              trigger: zoomTrigger
            }
   });
   myGallery.render();
</script>


