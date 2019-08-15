package com.fitatu.phonegap.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
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
import com.listonic.ad.companion.display.DisplayAdContainer;
import com.listonic.ad.companion.display.LegacyDisplayAdPresenter;
import com.listonic.ad.companion.base.InterceptedUrlCallback;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOError;

import android.support.constraint.ConstraintLayout;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ListonicAds extends CordovaPlugin {

    private RelativeLayout adViewLayout = null;

    private ViewGroup parentView;

    private Activity activity;

    private LegacyDisplayAdPresenter presenter = null;

    CordovaInterface cordovaInstance;

    DisplayAdContainer listonicAd;

    boolean isAdVisible = false;

    String currentZone = null;

    HashMap<String, LegacyDisplayAdPresenter> cachedAds = new HashMap<String, LegacyDisplayAdPresenter>();

    public static final String ConsentStringKey = "IABConsent_ConsentString";
    public static final String ParsedPurposeConsentKey = "IABConsent_ParsedPurposeConsents";
    public static final String ParsedVendorConsentKey = "IABConsent_ParsedVendorConsents";

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
        } else if ("hasConsent".equals(action)) {
            hasConsent(options, callbackContext);
        } else if ("updateGdprConsentsData".equals(action)) {
             updateGdprConsentsData(options, callbackContext);
         }

        return false;
    }

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);
        cordovaInstance = cordova;

        try {
            AdCompanion.INSTANCE.initialize(
                cordovaInstance.getActivity().getApplication(),
                null,
                false
            );
        } catch (Throwable error) {
            System.out.println("#debug ListonicAds creation error");
        }

        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread t, Throwable e) {
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String stacktrace = sw.toString();
                        System.out.println("HERE ERROR UITHREAD");
                        System.out.println(stacktrace);

                    }
                });
            }
        });

        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String stacktrace = sw.toString();
                System.out.println("HERE ERROR MAIN THREAD");
                System.out.println(stacktrace);
            }
        });

        initializeBannerView(webView);
    }

    private void initializeBannerView(CordovaWebView webView) {
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
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
                parentView.addView(listonicAd);

                if (presenter != null) {
                    presenter.onStop();
                    presenter.onDestroy();
                    presenter = null;
                }

                try {
                    presenter = new LegacyDisplayAdPresenter(
                        zone,
                        listonicAd,
                        map,
                        null
                    );

                    presenter.onCreate();
                    presenter.onStart();
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
                    presenter.onStop();
                    presenter.onDestroy();
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

    private void hasConsent(JSONObject options, CallbackContext callbackContext) {
        Boolean hasConsent = AdCompanion.INSTANCE.hasConsent(cordovaInstance.getActivity().getApplication());

        String consentString = readStringFromSharedPreferences(ConsentStringKey, null);
        String parsedPurposeConsentsString = readStringFromSharedPreferences(ParsedPurposeConsentKey, null);
        String parsedVendorConsentsString = readStringFromSharedPreferences(ParsedVendorConsentKey, null);

        System.out.println("#debug ListonicAds hasConsent " + hasConsent);
        System.out.println("#debug ListonicAds ConsentStringKey " + consentString);
        System.out.println("#debug ListonicAds ParsedPurposeConsentKey " + parsedPurposeConsentsString);
        System.out.println("#debug ListonicAds ParsedVendorConsentKey " + parsedVendorConsentsString);

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
            presenter.onStart();
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);

        if (presenter != null) {
            presenter.onStop();
        }
    }
}