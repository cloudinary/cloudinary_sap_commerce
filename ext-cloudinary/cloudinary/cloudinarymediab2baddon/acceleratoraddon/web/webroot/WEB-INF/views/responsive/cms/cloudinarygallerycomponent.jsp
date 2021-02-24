<%@ page trimDirectiveWhitespaces="true"%>

<script src="https://product-gallery.cloudinary.com/all.js"></script>

<spring:htmlEscape defaultHtmlEscape="true" />
<input id="cloud_name" type="hidden" value="${cloudName}"/>
<input id="product_code" type="hidden" value="${sapCCProductCode}"/>
<input id="spin_code" type="hidden" value="${spinCode}"/>
<input id="c_name" type="hidden" value="${cName}"/>

<div class="image-gallery js-gallery">
    <span class="image-gallery__zoom-icon glyphicon glyphicon-resize-full"></span>
    <div id="my-gallery" style="max-width:80%;margin:auto">
    </div>
</div>

<script type="text/javascript">
  var cloudName = document.getElementById("cloud_name").value;
  var productCode = document.getElementById("product_code").value;
  var spinCode = document.getElementById("spin_code").value;
  var cName = document.getElementById("c_name").value;
  var media_assets = [];
      media_assets.push({
            	tag: productCode,
              mediaType: "image"
            });
      media_assets.push({
                  	tag: productCode,
                    mediaType: "video"
                  });

   if(spinCode != "")
      {
         var spinURL = "https://res.cloudinary.com/"+cloudName+"/image/list/"+spinCode+".json";
          fetch(spinURL)
           	.then(function(response) {
               if(200 == response.status){
                media_assets.push({
                                  	tag: spinCode,
                                    mediaType: "spin"
                                  });
               }
             });
       }
         if(cName){
                  var dataObject = {
                                      "container": "#my-gallery",
                                      "cloudName": cloudName,
                                      "privateCdn": true,
                                      "secureDistribution": "kostadinov-res.cloudinary.com",
                                      "mediaAssets": media_assets,
                                         ${cloudinaryConfig.cloudinaryGalleryConfigJsonString}
                                     };
                  const myGallery= cloudinary.galleryWidget(dataObject);
                  myGallery.render();
                 }
                else {
                  var dataObject = {
                                        "container": "#my-gallery",
                                        "cloudName": cloudName,
                                        "mediaAssets": media_assets,
                                        ${cloudinaryConfig.cloudinaryGalleryConfigJsonString}
                                    };

                 const myGallery= cloudinary.galleryWidget(dataObject);
                 myGallery.render();
           }

</script>



