/*global cordova, module*/

module.exports = {
    gallery: function (name, successCallback, errorCallback) {
                       cordova.exec(successCallback, errorCallback, "TestCPlugin", "gallery", [name]);
         }

};
module.exports = {

   camera: function (name, successCallback, errorCallback) {
           cordova.exec(successCallback, errorCallback, "TestCPlugin", "camera", [name]);
       }
};
