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
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("show".equals(action)) {
            show(args, callbackContext);
            return true;
        } else if ("hide".equals(action)) {
            hide(args, callbackContext);
            return true;
        }

        return false;
    }

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);
        cordovaInstance = cordova;

        AdCompanion.INSTANCE.initialize(activity.getApplication(), null, false);

        initializeBannerView(webView);
    }

    private View initializeBannerView(CordovaWebView webView) {
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
                LinearLayout.LayoutParams listonicAdParams = new LinearLayout.LayoutParams(280, 100);
                listonicAdParams.gravity = Gravity.CENTER;
                listonicAd.setLayoutParams(listonicAdParams);
                listonicAd.setBackgroundColor(Color.parseColor("#0000FF"));

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

    private void show(JSONArray args, CallbackContext callbackContext) {
        presenter = new LegacyDisplayAdPresenter(
                "goals",
                listonicAd,
                new HashMap<String, String>(),
                null
        );
        presenter.onCreate();
        presenter.onStart();

        parentView.addView(listonicAd);

        callbackContext.success("Success!");
    }

    private void hide(JSONArray args, CallbackContext callbackContext) {
        presenter.onDestroy();
        parentView.removeView(listonicAd);

        callbackContext.success("Success!");
    }
}



//                View adMock = new LinearLayout(webView.getContext());
//                LinearLayout.LayoutParams adMockParams = new LinearLayout.LayoutParams(280, 100);
//                adMockParams.gravity = Gravity.CENTER;
//                adMock.setLayoutParams(adMockParams);
//                adMock.setBackgroundColor(Color.parseColor("#0000FF"));