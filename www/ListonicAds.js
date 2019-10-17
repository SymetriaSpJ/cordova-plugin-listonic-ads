var exec = cordova.require('cordova/exec');

var ListonicAds = function() {};

ListonicAds.prototype.prepare = function(options, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'prepare', [options]);
};


ListonicAds.prototype.show = function(options, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'show', [options]);
};

ListonicAds.prototype.showInterstitial = function(options, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'showInterstitial', [options]);
};

ListonicAds.prototype.setOptions = function(options, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    if (!options.width) {
        options.width = screen.width || document.querySelector('body').clientWidth || 320;
    }

    exec(successCallback, errorCallback, 'ListonicAds', 'setOptions', [options]);
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

ListonicAds.prototype.setDebugMode = function(options) {
    var errorCallback = function() {};
    var successCallback = function() {};

    exec(successCallback, errorCallback, 'ListonicAds', 'setDebugMode', [options]);
};

ListonicAds.prototype.hasConsent = function(onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'hasConsent', []);
};

ListonicAds.prototype.updateGdprConsentsData = function(options, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'updateGdprConsentsData', [options]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = ListonicAds;
}