package com.fitatu.phonegap.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.app.Activity;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.graphics.Color;
import android.content.ContextWrapper;
import java.util.HashMap;

import com.listonic.ad.companion.base.AdCompanion;
import com.listonic.ad.companion.base.AdCompanionCallback;
import com.listonic.ad.companion.display.DisplayAdContainer;
import com.listonic.ad.companion.display.LegacyDisplayAdPresenter;
import com.listonic.ad.companion.base.InterceptedUrlCallback;
import com.listonic.ad.companion.display.InterstitialDisplayAdPresenter;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOError;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ListonicAds extends CordovaPlugin {

    private RelativeLayout adViewLayout = null;

    private ViewGroup parentView;

    private Activity activity;

    private LegacyDisplayAdPresenter presenter = null;

    CordovaInterface cordovaInstance;

    CordovaWebView webViewInstance;

    DisplayAdContainer listonicAd;

    boolean isAdVisible = false;

    String currentZone = null;

    HashMap<String, LegacyDisplayAdPresenter> cachedAds = new HashMap<String, LegacyDisplayAdPresenter>();

    public static final String ConsentStringKey = "IABConsent_ConsentString";
    public static final String ParsedPurposeConsentKey = "IABConsent_ParsedPurposeConsents";
    public static final String ParsedVendorConsentKey = "IABConsent_ParsedVendorConsents";

    InterstitialDisplayAdPresenter interstitialPresenter;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject options = args.optJSONObject(0);

        if ("initBanner".equals(action)) {
            initBanner(options, callbackContext);
            return true;
        } else if ("initInterstitial".equals(action)) {
            initInterstitial(options, callbackContext);
            return true;
        } else if ("show".equals(action)) {
            show(options, callbackContext);
            return true;
        } else if ("hide".equals(action)) {
            hide(options, callbackContext);
            return true;
        } else if ("setDebugMode".equals(action)) {
            setDebugMode(options, callbackContext);
            return true;
        } else if ("updateGdprConsentsData".equals(action)) {
            updateGdprConsentsData(options, callbackContext);
            return true;
        } else if ("showInterstitial".equals(action)) {
            showInterstitial(options, callbackContext);
            return true;
        }

        return false;
    }

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);
        cordovaInstance = cordova;
        webViewInstance = webView;

        try {
            AdCompanion.INSTANCE.initialize(
                    cordovaInstance.getActivity().getApplication(),
                    new AdCompanionCallback() {
                        @Override
                        public boolean onUrlIntercepted(@Nullable String s) {
                            return false;
                        }

                        @Override
                        public boolean hasConsentForAdvertising() {
                            return false;
                        }
                    },
                    false
            );
        } catch (Throwable error) {
             System.out.println("#debug ListonicAds creation error: " + error.getMessage());
        }
    }

    private void initBanner(JSONObject options, CallbackContext callbackContext) {
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup wvParentView = (ViewGroup) getWebView(webViewInstance).getParent();

                if (parentView == null) {
                    parentView = new LinearLayout(webViewInstance.getContext());
                }

                if (wvParentView != null && wvParentView != parentView) {
                    ViewGroup rootView = (ViewGroup)(getWebView(webViewInstance).getParent());

                    wvParentView.removeView(getWebView(webViewInstance));

                    getWebView(webViewInstance).setLayoutParams(
                        new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            1.0F
                        )
                    );
                    ((LinearLayout) parentView).setOrientation(LinearLayout.VERTICAL);
                    parentView.setLayoutParams(
                        new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0.0F
                        )
                    );

                    parentView.setBackgroundColor(Color.parseColor("#F7F8F9"));
                    parentView.setClipChildren(false);
                    parentView.setClipToPadding(false);
                    parentView.addView(getWebView(webViewInstance));
                    rootView.setClipChildren(false);
                    rootView.setClipToPadding(false);
                    setAllParentsClip(parentView, false);
                    rootView.addView(parentView);
                }

                parentView.bringToFront();
                parentView.requestLayout();
                parentView.requestFocus();

                callbackContext.success("Success!");
            }
        });
    }

    public static void setAllParentsClip(View v, boolean enabled) {
        while (v.getParent() != null && v.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) v.getParent();
            viewGroup.setClipChildren(enabled);
            viewGroup.setClipToPadding(enabled);
            v = viewGroup;
        }
    }

    private View getWebView(CordovaWebView webView) {
        try {
            return (View) webView.getClass().getMethod("getView").invoke(webView);
        } catch (Exception e) {
            return (View) webView;
        }
    }

    private void show(JSONObject options, CallbackContext callbackContext) {
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String zone = "";
                String sex = "";
                String age = "";
                HashMap<String, String> map = new HashMap<>();

                try {
                    zone = options.getString("zone");
                    sex = options.getString("sex");
                    age = options.getString("age");
                    map.put("sex", sex);
                    map.put("age", age);
                } catch(JSONException e) {
                    throw new IOError(e);
                }

                if (isAdVisible == true && currentZone.equals(zone)) {
                    return;
                }

                if (listonicAd != null) {
                    listonicAd.setVisibility(View.GONE);
                    parentView.removeView(listonicAd);
                    listonicAd = null;
                }

                try {
                    listonicAd = new DisplayAdContainer(webView.getContext());
                    listonicAd.setBackgroundColor(Color.parseColor("#F7F8F9"));
                    listonicAd.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    );
                } catch (Throwable error) {
                    System.out.println("#debug ListonicAds DisplayAdContainer error");
                }

                listonicAd.setVisibility(View.VISIBLE);
                parentView.addView(listonicAd, 0);

                if (presenter != null) {
                    presenter.stop();
                    presenter.destroy();
                    presenter = null;
                }

                try {
                    presenter = new LegacyDisplayAdPresenter(
                        zone,
                        listonicAd,
                        map,
                        null
                    );

                    presenter.create();
                    presenter.start();
                } catch (Throwable error) {
                    System.out.println("#debug ListonicAds LegacyDisplayAdPresenter error");
                }

                isAdVisible = true;
                currentZone = zone;

                callbackContext.success("Success!");
            }
        });
    }

    private void hide(JSONObject options, CallbackContext callbackContext) {
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isAdVisible == false) {
                    return;
                }

                if (listonicAd != null) {
                    listonicAd.setVisibility(View.GONE);
                }

                if (presenter != null) {
                    presenter.stop();
                    presenter.destroy();
                    presenter = null;
                }

                isAdVisible = false;

                callbackContext.success("Success!");
            }
        });
    }

    private void setDebugMode(JSONObject options, CallbackContext callbackContext) {
        Boolean isDebug = false;
        try {
            isDebug = options.getBoolean("isDebug");
        } catch(JSONException e) {
            throw new IOError(e);
        }

        if (isDebug) {
            AdCompanion.INSTANCE.startLogging(cordovaInstance.getActivity().getApplication());
        } else {
            AdCompanion.INSTANCE.stopLogging(cordovaInstance.getActivity().getApplication());
        }

        callbackContext.success("Success!");
    }

    private void updateGdprConsentsData(JSONObject options, CallbackContext callbackContext) {
        String consentString;
        String parsedVendorConsentsString;
        String parsedPurposeConsentsString;

        try {
            consentString = options.getString("consentString");
            parsedVendorConsentsString = options.getString("parsedVendorConsentsString");
            parsedPurposeConsentsString = options.getString("parsedPurposeConsentsString");
        } catch(JSONException e) {
            throw new IOError(e);
        }

        saveStringInSharedPreferences(ConsentStringKey, consentString);
        saveStringInSharedPreferences(ParsedPurposeConsentKey,parsedPurposeConsentsString);
        saveStringInSharedPreferences(ParsedVendorConsentKey, parsedVendorConsentsString);

        callbackContext.success("Success!");
    }

    private String readStringFromSharedPreferences(String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
        cordovaInstance.getActivity().getApplication().getBaseContext()
        );

        return prefs.getString(key, defaultValue);
    }

    private void saveStringInSharedPreferences(String key, String string) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
            cordovaInstance.getActivity().getApplication().getBaseContext()
        );

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, string);
        editor.apply();
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);

        if (presenter != null) {
            presenter.start();
        }

        if (interstitialPresenter != null) {
            interstitialPresenter.start();
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);

        if (presenter != null) {
            presenter.stop();
        }

        if (interstitialPresenter != null) {
            interstitialPresenter.stop();
        }
    }

    private void initInterstitial(JSONObject options, CallbackContext callbackContext) {
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitialPresenter = new InterstitialDisplayAdPresenter(
                    cordovaInstance.getActivity(),
                    "Interstitial",
                    null,
                    null,
                    new HashMap<String, String>()
                );

                interstitialPresenter.create();
                interstitialPresenter.start();

                callbackContext.success("Success!");
             }
        });
    }

    private void showInterstitial(JSONObject options, CallbackContext callbackContext) {
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Boolean isInterstitialShown = interstitialPresenter.show();

                if (isInterstitialShown == false) {
                    callbackContext.error("Failed to show interstitial! isInterstitialShown" + isInterstitialShown);
                } else {
                    callbackContext.success("Success! isInterstitialShown" + isInterstitialShown);
                }
            }
        });
    }
}