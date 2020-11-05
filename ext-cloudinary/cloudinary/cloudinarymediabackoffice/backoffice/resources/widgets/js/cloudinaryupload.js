var cloudName = 'portaltech-reply';
var uploadPreset = 'qhrqbvmo';
var widgetUpload = 'widgetUpload';

var myCropWidget = cloudinary.createUploadWidget({
    cloudName: cloudName,
    uploadPreset: uploadPreset,
    cropping: false},
    (error, result) => {
        if (!error && result && result.event === "success") {
                zk.Widget.$("$txtBxId").setValue(JSON.stringify(result.info));
                zk.Widget.$('$eventBtn').fire('onClick');
            }
        }
     )

function uploadImage(){
     myCropWidget.open();
}

