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

public class ListonicAds extends CordovaPlugin {

    private RelativeLayout adViewLayout = null;

    private ViewGroup parentView;

    private Activity activity;

    private LegacyDisplayAdPresenter presenter = null;

    CordovaInterface cordovaInstance;

    DisplayAdContainer listonicAd;

    View adContainer;

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
        }

        return false;
    }

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);
        cordovaInstance = cordova;
        System.out.println("#debug ListonicAds initialize start");
        AdCompanion.INSTANCE.initialize(cordovaInstance.getActivity().getApplication(), null, false);
        System.out.println("#debug ListonicAds initialize 2");
        initializeBannerView(webView);
        System.out.println("#debug ListonicAds initialize end");
    }

    private void initializeBannerView(CordovaWebView webView) {
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("#debug ListonicAds initializeBannerView 1");

                final CordovaWebView wv = webView;
                ViewGroup wvParentView = (ViewGroup) getWebView(wv).getParent();

                System.out.println("#debug ListonicAds initializeBannerView 2");
                if (parentView == null) {
                    parentView = new LinearLayout(webView.getContext());
                }
                System.out.println("#debug ListonicAds initializeBannerView 3");
                if (wvParentView != null && wvParentView != parentView) {
                    ViewGroup rootView = (ViewGroup)(getWebView(webView).getParent());

                    wvParentView.removeView(getWebView(webView));

                    ((LinearLayout) parentView).setOrientation(LinearLayout.VERTICAL);
                    parentView.setLayoutParams(
                        new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            0.0F
                        )
                    );
                    getWebView(webView).setLayoutParams(
                        new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            1.0F
                        )
                    );

                    parentView.setBackgroundColor(Color.parseColor("#F7F8F9"));
                    parentView.addView(getWebView(webView));
                    rootView.addView(parentView);
                }

                View adContainer = new LinearLayout(webView.getContext());
                LinearLayout.LayoutParams adContainerParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                );
                adContainerParams.gravity = Gravity.CENTER;
                adContainer.setLayoutParams(adContainerParams);
                adContainer.setBackgroundColor(Color.parseColor("#F7F8F9"));

                System.out.println("#debug ListonicAds initializeBannerView 4");

                listonicAd = new DisplayAdContainer(webView.getContext());
                listonicAd.setBackgroundColor(Color.parseColor("#F7F8F9"));
                listonicAd.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );

                System.out.println("#debug ListonicAds initializeBannerView 5");

                parentView.addView(adContainer);
                adContainer.addView(listonicAd);
                parentView.bringToFront();
                parentView.requestLayout();
                parentView.requestFocus();
                System.out.println("#debug ListonicAds initializeBannerView 6");
            }
        });
    }

    private View getWebView(CordovaWebView webView) {
        try {
            return (View) webView.getClass().getMethod("getView").invoke(webView);
        } catch (Exception e) {
            return (View) webView;
        }
    }

    private void prepare(JSONObject options, CallbackContext callbackContext) {
        System.out.println("#debug ListonicAds prepare start");
    }

    private void setOptions(JSONObject options, CallbackContext callbackContext) {
        System.out.println("#debug ListonicAds setPresenterOptions start");
        Integer myGravity = null;
        Integer width = null;
        Integer height = null;
        LinearLayout.LayoutParams layoutParams = null;

        try {
            System.out.println("#debug ListonicAds setOptions 1");
            if (options.has("width") && options.has("height")) {
                float factor = cordovaInstance.getActivity().getApplication().getBaseContext().getResources().getDisplayMetrics().density;
                width = (int)(options.getInt("width") * factor);
                height = (int)(options.getInt("height") * factor);
                layoutParams = new LinearLayout.LayoutParams(width, height);
            }

//            if (options.has("gravity")) {
//                if (options.getString("gravity").equals("CENTER")) {
//                    myGravity = Gravity.CENTER;
//                } else if (options.getString("gravity").equals("CENTER_VERTICAL")) {
//                    myGravity = Gravity.CENTER_VERTICAL;
//                } else {
//                    myGravity = Gravity.CENTER | Gravity.CENTER_VERTICAL;
//                }
//            }

            cordovaInstance.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("#debug ListonicAds setOptions 4 on uithread");

                    if (layoutParams != null) {
                        adContainer.setLayoutParams(layoutParams);
                    }

                    System.out.println("#debug ListonicAds setOptions 5 on uithread");

//                    if (myGravity != null) {
//                        adContainer.setGravity(myGravity);
//                    }

                    System.out.println("#debug ListonicAds setOptions 6 on ui thread");
                    callbackContext.success("Success!");
                }
            });

        } catch(JSONException e) {
            System.out.println("#debug ListonicAds setOptions json error");
            throw new IOError(e);
        }
    }

    private void show(JSONObject options, CallbackContext callbackContext) {
        System.out.println("#debug ListonicAds show start");
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("#debug ListonicAds show 1");
                String zone = "";

                try {
                    zone = options.getString("zone");
                    System.out.println("#debug ListonicAds show zone try" + zone);
                } catch(JSONException e) {
                    System.out.println("#debug ListonicAds show json error");
                    throw new IOError(e);
                }

                if (presenter != null) {
                    presenter.onDestroy();
                }

                System.out.println("#debug ListonicAds show zone" + zone);
//                System.out.println("#debug ListonicAds show options.zone", options.getString("zone"));
                presenter = new LegacyDisplayAdPresenter(
//                        "home_page"
                        zone,
                        listonicAd,
                        new HashMap<String, String>(),
                        null
                );
                System.out.println("#debug ListonicAds show 2");
                presenter.onCreate();
                System.out.println("#debug ListonicAds show 3");
                presenter.onStart();
                System.out.println("#debug ListonicAds show 4");


                System.out.println("#debug ListonicAds show end");

                callbackContext.success("Success!");
            }
        });
    }

    private void hide(JSONObject options, CallbackContext callbackContext) {
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("#debug ListonicAds hide start");
                if (presenter != null) {
                    presenter.onStop();
                }
//                parentView.removeView(listonicAd);
                System.out.println("#debug ListonicAds hide end");

                callbackContext.success("Success!");
            }
        });
    }
}



//                View adMock = new LinearLayout(webView.getContext());
//                LinearLayout.LayoutParams adMockParams = new LinearLayout.LayoutParams(280, 100);
//                adMockParams.gravity = Gravity.CENTER;
//                adMock.setLayoutParams(adMockParams);
//                adMock.setBackgroundColor(Color.parseColor("#0000FF"));