/*global cordova, module*/

module.exports = {
    gallery: function (name, successCallback, errorCallback) {
                       cordova.exec(successCallback, errorCallback, "TestCPlugin", "gallery", [name]);
         }
   camera: function (name, successCallback, errorCallback) {
           cordova.exec(successCallback, errorCallback, "TestCPlugin", "camera", [name]);
       }
};
