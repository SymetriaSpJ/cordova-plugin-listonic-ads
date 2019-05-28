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

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOError;

import android.support.constraint.ConstraintLayout;

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

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        JSONObject options = args.optJSONObject(0);

        if ("show".equals(action)) {
            show(options, callbackContext);
            return true;
        } else if ("hide".equals(action)) {
            hide(options, callbackContext);
            return true;
        } else if ("prepare".equals(action)) {
            prepare(options, callbackContext);
            return true;
        } else if ("setOptions".equals(action)) {
            setOptions(options, callbackContext);
            return true;
        } else if ("setDebugMode".equals(action)) {
            setDebugMode(options, callbackContext);
            return true;
        }

        return false;
    }

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);
        cordovaInstance = cordova;
        AdCompanion.INSTANCE.initialize(cordovaInstance.getActivity().getApplication(), null, false);
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


    private void prepare(final JSONObject options, final CallbackContext callbackContext) {
        System.out.println("#debug ListonicAds prepare start");

//        Iterator<String> keys = options.keys();

//        while(keys.hasNext()) {
//            String zoneName = keys.next();
//
//            System.out.println("#debug ListonicAds prepare zoneName " + zoneName);
//
//            try {
//                String sex = "";
//                String age = "";
//                HashMap<String, String> map = new HashMap<>();
//                sex = options.getString("sex");
//                age = options.getString("age");
//
//                map.put("sex", sex);
//                map.put("age", age);
//
//                LegacyDisplayAdPresenter presenter = new LegacyDisplayAdPresenter(
//                        zone,
//                        listonicAd,
//                        map,
//                        null
//                );
//                presenter.onCreate();
//
//                cachedAds.put(
//                    zoneName,
//                    presenter
//                );
//            } catch(JSONException e) {
//                throw new IOError(e);
//            }
//        }


//        try {
//            String sex = "";
//            String age = "";
//            HashMap<String, String> map = new HashMap<>();
//            sex = options.getString("sex");
//            age = options.getString("age");
//
//            map.put("sex", sex);
//            map.put("age", age);
//
//            LegacyDisplayAdPresenter presenter1 = new LegacyDisplayAdPresenter(
//                    "start",
//                    listonicAd,
//                    map,
//                    null
//            );
//            presenter1.onCreate();
//            cachedAds.put(
//                    "start",
//                    presenter1
//            );
//
//            LegacyDisplayAdPresenter presenter2 = new LegacyDisplayAdPresenter(
//                    "settings",
//                    listonicAd,
//                    map,
//                    null
//            );
//            presenter2.onCreate();
//            cachedAds.put(
//                    "settings",
//                    presenter2
//            );
//
//            LegacyDisplayAdPresenter presenter3 = new LegacyDisplayAdPresenter(
//                    "trainings",
//                    listonicAd,
//                    map,
//                    null
//            );
//            presenter3.onCreate();
//            cachedAds.put(
//                    "trainings",
//                    presenter3
//            );
//        } catch(JSONException e) {
//            throw new IOError(e);
//        }
    }

    private void setOptions(JSONObject options, CallbackContext callbackContext) {
        if (options.has("width") && options.has("height")) {
            cordovaInstance.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Integer width = null;
                    Integer height = null;

                    try {
                        float factor = cordovaInstance.getActivity().getApplication().getBaseContext().getResources().getDisplayMetrics().density;
                        width = (int)(options.getInt("width") * factor);
                        height = (int)(options.getInt("height") * factor);

                        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                        layoutParams.gravity = Gravity.CENTER;

                        listonicAd.setLayoutParams(layoutParams);

                        callbackContext.success("Success!");

                    } catch(JSONException e) {
                        throw new IOError(e);
                    }
                }
            });
        }

    }

    private void show(JSONObject options, CallbackContext callbackContext) {
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("#debug ListonicAds show start");

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
                    System.out.println("#debug ListonicAds show CANCELLING BECAUSE SAME ZONE AND VISIBLE");
                    return;
                } else {
                    System.out.println("#debug ListonicAds show CHANGES REQUIRED, SHOWING NEW AD");
                }

                if (listonicAd != null) {
                    listonicAd.setVisibility(View.GONE);
                    parentView.removeView(listonicAd);
                    listonicAd = null;
                }

                listonicAd = new DisplayAdContainer(webView.getContext());
                listonicAd.setBackgroundColor(Color.parseColor("#F7F8F9"));
                listonicAd.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                );

                listonicAd.setVisibility(View.VISIBLE);
                parentView.addView(listonicAd);

                if (presenter != null) {
                    presenter.onDestroy();
                    presenter = null;
                }

                presenter = new LegacyDisplayAdPresenter(
                    zone,
                    listonicAd,
                    map,
                    null
                );

                presenter.onCreate();
                presenter.onStart();

//                System.out.println("#debug ListonicAds show X1");
//                LegacyDisplayAdPresenter oldPresenter = cachedAds.get(zone);
//                cachedAds.remove(zone);
//                System.out.println("#debug ListonicAds show X2");
//                oldPresenter.onStart();
//                System.out.println("#debug ListonicAds show X3");
//                LegacyDisplayAdPresenter newPresenter = new LegacyDisplayAdPresenter(
//                    zone,
//                    listonicAd,
//                    map,
//                    null
//                );
//                System.out.println("#debug ListonicAds show X4");
//                newPresenter.onCreate();
//                System.out.println("#debug ListonicAds show X5");
//                oldPresenter.onStop();
//                System.out.println("#debug ListonicAds show X6");
//                oldPresenter.onDestroy();
//                System.out.println("#debug ListonicAds show X7");
//                newPresenter.onStart();
//                cachedAds.put(zone, newPresenter);
//                System.out.println("#debug ListonicAds show X8");



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
                System.out.println("#debug ListonicAds hide start");

                if (isAdVisible == false) {
                    System.out.println("#debug ListonicAds hide CANCELLING BECAUSE AD NOT VISIBLE");
                    return;
                } else {
                    System.out.println("#debug ListonicAds hide CHANGES REQUIRED, HIDING AD");
                }

                if (listonicAd != null) {
                    listonicAd.setVisibility(View.GONE);
                }

                if (presenter != null) {
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
}