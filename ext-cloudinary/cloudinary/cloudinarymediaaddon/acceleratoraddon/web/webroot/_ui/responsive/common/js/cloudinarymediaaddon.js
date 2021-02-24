
var url = "https://unpkg.com/cloudinary-core@latest/cloudinary-core-shrinkwrap.min.js";
    var pgw_script_tag = document.createElement('script');
      pgw_script_tag.src = url
      pgw_script_tag.type = 'text/javascript';
      pgw_script_tag.charset = 'utf-8';
      document.getElementsByTagName('head')[0].appendChild(pgw_script_tag);
      var cldname;
      var apiKey;
      var environment;
      var cloudinaryImageWidthLimitMin;
      var cloudinaryImageWidthLimitMax;
      var cloudinaryByteStep;
      var SampleDataAccess = function () {
      // The OAuth2 Access Token.
          var appAuthToken;
          var app = {};

      app.GetCloudinaryConfiguration = getCloudinaryConfiguration;

      function getCloudinaryConfiguration()
          {
              var dfd = new $.Deferred();

              getAccessTokenByPost()
              .done(function () {
                  return $.ajax({
                      method:      "get",
                      url:         '/rest/v2/apparel-uk/cloudinary/configuration',
                      contentType: "application/json; charset=utf-8",
                      headers:     { 'Authorization': 'Bearer ' + appAuthToken }
                  })
                  .done(function (data) {
                  cloudName = data.cloudName;
                  apiKey = data.apiKey;
                  environment = data.environment;
                  cloudinaryImageWidthLimitMin = data.cloudinaryImageWidthLimitMin;
                  cloudinaryImageWidthLimitMax = data.cloudinaryImageWidthLimitMax;
                  cloudinaryByteStep = data.cloudinaryByteStep;
                      dfd.resolve(data);
                  })
                  .fail(function () {
                      // in a real app, you should check for a 401 and if so delete the Access Token
                      // so that it will reload on the next request.
                      //alert("failed to get articles.");
                      dfd.fail();
                  });
              });
              return dfd.promise();
          }
      // Checks that there is a saved Access Token.  If one does not exist, it gets one from the
          // OAuth2 Server using the Client Credentials Flow and saves it.

          function getAccessTokenByPost() {
              if (appAuthToken) {
                  // just return a resolved promise.
                  return $.Deferred().resolve().promise();
              }
              else {
                  var postData = {
                      grant_type:    'client_credentials',
                      client_id:     'client-side',
                      client_secret: 'secret'
                  };
                  return $.post('/authorizationserver/oauth/token', postData, null, 'json')
                          .done(function (data) {

                              appAuthToken = data.access_token;

                          })
                          .fail(function (jqXHR, status, error) {
                              console.log("getting Access Token failed.");
                          });
              }
          }
          return app;
          }();

          $(document).ready(function(){
          var data = SampleDataAccess.GetCloudinaryConfiguration().done(function (data) {
          var cl = cloudinary.Cloudinary.new({cloud_name: cloudName});

                      var currentClass = $('img').attr('class');
                      var updatedClass = 'cld-responsive ';
                      if(currentClass!=null){
                      updatedClass = updatedClass + currentClass;
                      }
                      $('img').addClass(updatedClass);

                      var my_breakpoints = [];
                      if(cloudinaryImageWidthLimitMin!=null && cloudinaryImageWidthLimitMax!=null){
                      var breakpoint = cloudinaryImageWidthLimitMin;
                      while(breakpoint < cloudinaryImageWidthLimitMax){
                      my_breakpoints.push(breakpoint);
                      breakpoint = breakpoint + cloudinaryByteStep;
                      }
                      my_breakpoints.push(cloudinaryImageWidthLimitMax);
                      }
                      //my_breakpoints = [50, 90, 130, 170, 200, 300, 450, 550, 700, 850, 2000];
                      cl.config({breakpoints:my_breakpoints, responsive_use_breakpoints:"true"});
                      cl.responsive();
          });
          });


