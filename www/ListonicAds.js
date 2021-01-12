cordova.define("cordova-plugin-listonic-ads.ListonicAds", function(require, exports, module) {
    var exec = cordova.require('cordova/exec');

    var ListonicAds = function() {};

    ListonicAds.prototype.show = function(options, onSuccess, onError) {
        var errorCallback = function(obj) {
            onError(obj);
        };

        var successCallback = function(obj) {
            onSuccess(obj);
        };

        exec(successCallback, errorCallback, 'ListonicAds', 'show', [options]);
    };

    ListonicAds.prototype.showInterstitial = function(options, successCallback, errorCallback) {
//    var errorCallback = function(obj) {
//        onError(obj);
//    };
//
//    var successCallback = function(obj) {
//        onSuccess(obj);
//    };

        console.log('########## options', options);

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
});
