var exec = cordova.require('cordova/exec');

var ListonicAds = function() {
    console.log('ListonicAds instanced');
};

ListonicAds.prototype.show = function(msg, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'show', [msg]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = ListonicAds;
}