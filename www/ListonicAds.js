var exec = cordova.require('cordova/exec');

var ListonicAds = function(options) {
    console.log('ListonicAds instanced with options', options);
};

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

ListonicAds.prototype.hide = function(options, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'ListonicAds', 'hide', [options]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = ListonicAds;
}