<%@ page trimDirectiveWhitespaces="true"%>

<script src="https://product-gallery.cloudinary.com/all.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

<spring:htmlEscape defaultHtmlEscape="true" />
<input id="cloud_name" type="hidden" value="${cloudName}"/>
<input id="product_code" type="hidden" value="${sapCCProductCode}"/>
<input id="spin_code" type="hidden" value="${spinCode}"/>§

<div class="image-gallery js-gallery">
    <span class="image-gallery__zoom-icon glyphicon glyphicon-resize-full"></span>
    <div id="my-gallery" style="max-width:80%;margin:auto">
    </div>
</div>

<script type="text/javascript">
  var cloudName = document.getElementById("cloud_name").value;
  var productCode = document.getElementById("product_code").value;
  var spinCode = document.getElementById("spin_code").value;

  var spinURL = "https://res.cloudinary.com/"+cloudName+"/image/list/"+productCode+".json";

     var dataObject = {
                          "container": "#my-gallery",
                              "cloudName": cloudName,
                              "mediaAssets": [{
                                  "tag": productCode,
                                  "mediaType": "image"
                              }, {
                                  "tag": productCode,
                                  "mediaType": "video"
                              }],
                              ${cloudinaryConfig.cloudinaryGalleryConfigJsonString}
                        };
    var spinDataObject = {
                                "container": "#my-gallery",
                                    "cloudName": cloudName,
                                    "mediaAssets": [{
                                        "tag": productCode,
                                        "mediaType": "image"
                                    }, {
                                        "tag": productCode,
                                        "mediaType": "video"
                                    }, {
                                        "tag": spinCode,
                                        "mediaType": "spin"
                                    }],
                                    ${cloudinaryConfig.cloudinaryGalleryConfigJsonString}
                              };

$(document).ready(function() {
$.ajax({
            type: "GET",
            url: spinURL,
            success: function (data, status, jqXHR) {
            const mySpinGallery= cloudinary.galleryWidget(spinDataObject);
            mySpinGallery.render();
            },
            error: function (xhr) {
            const myGallery= cloudinary.galleryWidget(dataObject);
            myGallery.render();
            }
        });
});
</script>


