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

public class ListonicAds extends CordovaPlugin {

    private RelativeLayout adViewLayout = null;

    private ViewGroup parentView;

    private Activity activity;

    private LegacyDisplayAdPresenter presenter;

    CordovaInterface cordovaInstance;

    DisplayAdContainer listonicAd;

    @Override
    public boolean execute(String action, JSONObject options, CallbackContext callbackContext) throws JSONException {
        if ("show".equals(action)) {
            show(options, callbackContext);
            return true;
        } else if ("hide".equals(action)) {
            hide(options, callbackContext);
            return true;
        } else if ("prepare".equals(action)) {
            prepare(options, callbackContext);
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
                    parentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
                    getWebView(webView).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));
                    parentView.setBackgroundColor(Color.parseColor("#FF0000"));

                    parentView.addView(getWebView(webView));
                    rootView.addView(parentView);
                }

                System.out.println("#debug ListonicAds initializeBannerView 4");

                listonicAd = new DisplayAdContainer(webView.getContext());
//                LinearLayout.LayoutParams listonicAdParams = new LinearLayout.LayoutParams(280, 100);
//                listonicAdParams.gravity = Gravity.CENTER;
//                listonicAd.setLayoutParams(listonicAdParams);
//                listonicAd.setBackgroundColor(Color.parseColor("#0000FF"));

                System.out.println("#debug ListonicAds initializeBannerView 5");

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

    private void show(JSONObject options, CallbackContext callbackContext) {
        cordovaInstance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("#debug ListonicAds show start");
                System.out.println("#debug ListonicAds show options.zone", options.zone);
                presenter = new LegacyDisplayAdPresenter(
                        options.zone,
                        listonicAd,
                        new HashMap<String, String>(),
                        null
                );
                System.out.println("#debug ListonicAds show 2");
                presenter.onCreate();
                System.out.println("#debug ListonicAds show 3");
                presenter.onStart();
                System.out.println("#debug ListonicAds show 4");

                parentView.addView(listonicAd);
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
                parentView.removeView(listonicAd);
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