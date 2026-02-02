<%@ page trimDirectiveWhitespaces="true"%>

<script src="https://product-gallery.cloudinary.com/all.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

<spring:htmlEscape defaultHtmlEscape="true" />
<input id="cloud_name" type="hidden" value="${cloudName}"/>
<input id="product_code" type="hidden" value="${sapCCProductCode}"/>
<input id="spin_code" type="hidden" value="${spinCode}"/>
<input id="c_name" type="hidden" value="${cName}"/>
<input id="cloudinaryGalleryConfigJsonString" type="hidden" value='${cloudinaryConfig.cloudinaryGalleryConfigJsonString}'/>

<div class="image-gallery-cloudinary js-gallery">
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
      var spinURL;
      if(cName){
        spinURL = "https://"+cName;
      }else{
        spinURL = "https://res.cloudinary.com/"+cloudName;
      }
          spinURL += "/image/list/"+spinCode+".json";
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

       var jsonConfigString = document.getElementById("cloudinaryGalleryConfigJsonString").value;

       var galleryConfigJson;

       if(jsonConfigString){
              try{
              galleryConfigJson = JSON.parse(jsonConfigString);
              }catch(err){
              console.log("Incorrect json string");
              }
              }
       var galleryBaseJson = {
                              "container": "#my-gallery",
                              "cloudName": cloudName,
                              "queryParam": "AJ",

                              "mediaAssets": media_assets,
                               ...galleryConfigJson
                             };


                             if(cName){
                                     galleryBaseJson.privateCdn = true;
                                     galleryBaseJson.secureDistribution = cName;
                                   }
          const myGallery= cloudinary.galleryWidget(galleryBaseJson);
          myGallery.render();


</script>



