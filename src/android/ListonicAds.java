package com.fitatu.phonegap.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.widget.LinearLayout;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import java.util.HashMap;

import com.listonic.ad.companion.base.AdCompanion;
import com.listonic.ad.companion.display.DisplayAdContainer;
import com.listonic.ad.companion.display.LegacyDisplayAdPresenter;
import com.listonic.ad.companion.display.InterstitialDisplayAdPresenter;

import java.io.IOError;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Looper;

public class ListonicAds extends CordovaPlugin {

    private ViewGroup parentView;

    private LegacyDisplayAdPresenter presenter = null;

    DisplayAdContainer listonicAd;

    boolean isAdVisible = false;

    String currentZone = null;

    public static final String ConsentStringKey = "IABConsent_ConsentString";
    public static final String ParsedPurposeConsentKey = "IABConsent_ParsedPurposeConsents";
    public static final String ParsedVendorConsentKey = "IABConsent_ParsedVendorConsents";

    InterstitialDisplayAdPresenter interstitialPresenter;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject options = args.optJSONObject(0);

        if ("show".equals(action)) {
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
        } else if ("showInterstitial".equals(action)) {
            showInterstitial(options, callbackContext);
        }

        return false;
    }

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);

        try {
            AdCompanion.INSTANCE.initialize(
                cordova.getActivity().getApplication(),
                null,
                false
            );
        } catch (Throwable error) {
            System.out.println("#debug ListonicAds creation error");
        }

        initializeBannerView(webView);
        initializeInterstitial();
    }

    private void initializeBannerView(CordovaWebView webView) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final CordovaWebView wv = webView;
                ViewGroup wvParentView = (ViewGroup) getWebView(wv).getParent();

                if (parentView == null) {
                    parentView = new LinearLayout(webView.getContext());
                }

                if (wvParentView != null && wvParentView != parentView) {
                    ViewGroup rootView = (ViewGroup)(getWebView(webView).getParent());

                    wvParentView.removeView(getWebView(webView));

                    getWebView(webView).setLayoutParams(
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
                    parentView.addView(getWebView(webView));
                    rootView.setClipChildren(false);
                    rootView.setClipToPadding(false);
                    setAllParentsClip(parentView, false);
                    rootView.addView(parentView);
                }

                parentView.bringToFront();
                parentView.requestLayout();
                parentView.requestFocus();
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
        cordova.getActivity().runOnUiThread(new Runnable() {
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
        cordova.getActivity().runOnUiThread(new Runnable() {
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
            AdCompanion.INSTANCE.startLogging(cordova.getActivity().getApplication());
        } else {
            AdCompanion.INSTANCE.stopLogging(cordova.getActivity().getApplication());
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
                cordova.getActivity().getApplication().getBaseContext()
        );

        return prefs.getString(key, defaultValue);
    }

    private void saveStringInSharedPreferences(String key, String string) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
            cordova.getActivity().getApplication().getBaseContext()
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

    private void initializeInterstitial() {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                interstitialPresenter = new InterstitialDisplayAdPresenter(
                    cordova.getActivity(),
                    "Interstitial",
                    null,
                    null,
                    new HashMap<String, String>()
                );

                interstitialPresenter.create();
                interstitialPresenter.start();
            }
        });
    }

    private void showInterstitial(JSONObject options, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
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