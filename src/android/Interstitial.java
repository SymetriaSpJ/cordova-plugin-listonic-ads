package com.fitatu.ponegap.plugin.ListonicAds;

import com.listonic.ad.companion.display.InterstitialDisplayAdPresenter;

import org.apache.cordova.CallbackContext;
import org.json.JSONObject;

public class Interstitial extends Thread {
    private JSONObject options;
    private CallbackContext callbackContext;
    InterstitialDisplayAdPresenter interstitialPresenter;

    public Interstitial(InterstitialDisplayAdPresenter interstitialPresenter, JSONObject options, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        this.options = options;
        this.interstitialPresenter = interstitialPresenter;
    }

    public void run() {
        Boolean isInterstitialShown = interstitialPresenter.show();

        if (isInterstitialShown == false) {
            callbackContext.error("Failed to show interstitial! isInterstitialShown " + isInterstitialShown);
        } else {
            callbackContext.success("Success! isInterstitialShown " + isInterstitialShown);
        }
    }
}
