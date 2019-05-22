var exec = cordova.require('cordova/exec');

var ListonicAds = function() {
    console.log('ListonicAds instanced');
};

ListonicAds.prototype.show = function(onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'show', []);
};

ListonicAds.prototype.hide = function(onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'hide', []);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = ListonicAds;
}