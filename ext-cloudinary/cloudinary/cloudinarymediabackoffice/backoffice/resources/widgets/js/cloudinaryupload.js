var cloudName;
//= 'portaltech-reply';
var uploadPreset = 'qhrqbvmo';
var widgetUpload = 'widgetUpload';
var cldname;
var apiKey;
var environment;
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
                url:         '/occ/v2/apparel-uk/cloudinary/configuration',
                contentType: "application/json; charset=utf-8",
                headers:     { 'Authorization': 'Bearer ' + appAuthToken }
            })
            .done(function (data) {
            cloudName = data.cloudName;
            apiKey = data.apiKey;
            environment = data.environment;
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
    var myCropWidget;
var data = SampleDataAccess.GetCloudinaryConfiguration().done(function (data) {
myCropWidget = cloudinary.createMediaLibrary({
  cloud_name: cloudName,
  api_key: apiKey,
  integration : {
          type: "CloudinarySAPCC",//the integration identifier e.g. “SAP CC”,
          platform: "SAP Commerce Cloud 2211",//hosting app name + version e.g. SAP CC 1.2.3,
          version: "1.3.0",//integration version e.g. 1.0.0
          environment: environment//e.g “prod”/“stag”/“dev”
      },
  multiple: false,
  max_files: 1,
  }, {
       insertHandler: function (data) {
         data.assets.forEach(asset => {
         zk.Widget.$("$txtBxId").setValue(JSON.stringify(asset));
         zk.Widget.$("$mediaName").setValue(asset.public_id+"."+asset.format);
         zk.Widget.$('$eventBtn').fire('onClick');
         })
       }
     }
  )
});


function uploadImage(){
     myCropWidget.show();
}

