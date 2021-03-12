var exec = cordova.require('cordova/exec');

var ListonicAds = function() {};

ListonicAds.prototype.initBanner = function() {
    var successCallback = function() {};
    var errorCallback = function() {};

    exec(successCallback, errorCallback, 'ListonicAds', 'initBanner', []);
};

ListonicAds.prototype.initInterstitial = function() {
    var successCallback = function() {};
    var errorCallback = function() {};

    exec(successCallback, errorCallback, 'ListonicAds', 'initInterstitial', []);
};

ListonicAds.prototype.show = function(options, onSuccess, onError) {
    var successCallback = function(obj) {
        onSuccess(obj);
    };

    var errorCallback = function(obj) {
        onError(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'show', [options]);
};

ListonicAds.prototype.showInterstitial = function(options, onSuccess, onError) {
    var successCallback = function(obj) {
        onSuccess(obj);
    };

    var errorCallback = function(obj) {
        onError(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'showInterstitial', [options]);
};

ListonicAds.prototype.setOptions = function(options, onSuccess, onError) {
    var successCallback = function(obj) {
        onSuccess(obj);
    };

    var errorCallback = function(obj) {
        onError(obj);
    };

    if (!options.width) {
        options.width = screen.width || document.querySelector('body').clientWidth || 320;
    }

    exec(successCallback, errorCallback, 'ListonicAds', 'setOptions', [options]);
};

ListonicAds.prototype.hide = function(onSuccess, onError) {
    var successCallback = function(obj) {
        onSuccess(obj);
    };

    var errorCallback = function(obj) {
        onError(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'hide', []);
};

ListonicAds.prototype.setDebugMode = function(options) {
    var successCallback = function() {};
    var errorCallback = function() {};

    exec(successCallback, errorCallback, 'ListonicAds', 'setDebugMode', [options]);
};

ListonicAds.prototype.hasConsent = function(onSuccess, onError) {
    var successCallback = function(obj) {
        onSuccess(obj);
    };

    var errorCallback = function(obj) {
        onError(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'hasConsent', []);
};

ListonicAds.prototype.updateGdprConsentsData = function(options, onSuccess, onError) {
    var successCallback = function(obj) {
        onSuccess(obj);
    };

    var errorCallback = function(obj) {
        onError(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'updateGdprConsentsData', [options]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = ListonicAds;
}