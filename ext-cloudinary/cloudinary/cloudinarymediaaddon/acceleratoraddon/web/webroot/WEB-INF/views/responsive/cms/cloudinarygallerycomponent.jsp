<%@ page trimDirectiveWhitespaces="true"%>

<script src="https://product-gallery.cloudinary.com/all.js"></script>

<spring:htmlEscape defaultHtmlEscape="true" />

<input id="cloud_name" type="hidden" value="${cloudName}"/>
<input id="product_code" type="hidden" value="${sapCCProductCode}"/>

<div class="image-gallery js-gallery">
    <span class="image-gallery__zoom-icon glyphicon glyphicon-resize-full"></span>
    <div id="my-gallery" style="max-width:80%;margin:auto">
    </div>
</div>

<script type="text/javascript">
  var cloudName = document.getElementById("cloud_name").value;
  var productCode = document.getElementById("product_code").value;

   var dataObject = {
                          "container": "#my-gallery",
                              "cloudName": cloudName,
                              "mediaAssets": [{
                                  "tag": productCode,
                                  "mediaType": "image"
                              }, {
                                  "tag": productCode,
                                  "mediaType": "video"
                              }, {
                                  "tag": productCode,
                                  "mediaType": "spin"
                              }],
                              ${cloudinaryConfig.cloudinaryGalleryConfigJsonString}
                        };

     const myGallery= cloudinary.galleryWidget(dataObject);
     myGallery.render();
</script>


