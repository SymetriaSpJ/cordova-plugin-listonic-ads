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

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);

        Activity activity = cordova.getActivity();

        System.out.println("#debug ListonicAds initialize start");

        AdCompanion.INSTANCE.initialize(activity.getApplication(), null, false);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                System.out.println("#debug ListonicAds initialize 1");

                final CordovaWebView wv = webView;
                ViewGroup wvParentView = (ViewGroup) getWebView(wv).getParent();

                System.out.println("#debug ListonicAds initialize 2");
                if (parentView == null) {
                    parentView = new LinearLayout(webView.getContext());
                }
                System.out.println("#debug ListonicAds initialize 3");
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
                System.out.println("#debug ListonicAds initialize 4");

                DisplayAdContainer listonicAd = new DisplayAdContainer(webView.getContext());
//                LinearLayout.LayoutParams listonicAdParams = new LinearLayout.LayoutParams(280, 100);
//                listonicAdParams.gravity = Gravity.CENTER;
//                listonicAd.setLayoutParams(listonicAdParams);
//                listonicAd.setBackgroundColor(Color.parseColor("#0000FF"));

                System.out.println("#debug ListonicAds initialize 5");
                LegacyDisplayAdPresenter presenter = new LegacyDisplayAdPresenter(
                        "future_diary",
                        listonicAd,
                        new HashMap<String, String>(),
                        null
                );
                System.out.println("#debug ListonicAds initialize 6");
                presenter.onCreate();
                presenter.onStart();
                System.out.println("#debug ListonicAds initialize 7");
//                View adMock = new LinearLayout(webView.getContext());
//                LinearLayout.LayoutParams adMockParams = new LinearLayout.LayoutParams(280, 100);
//                adMockParams.gravity = Gravity.CENTER;
//                adMock.setLayoutParams(adMockParams);
//                adMock.setBackgroundColor(Color.parseColor("#0000FF"));
                System.out.println("#debug ListonicAds initialize 8");
                parentView.addView(listonicAd);
                parentView.bringToFront();
                parentView.requestLayout();
                parentView.requestFocus();
                System.out.println("#debug ListonicAds initialize 9");
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

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("show".equals(action)) {
            show(args.getString(0), callbackContext);
            return true;
        }

        return false;
    }

    private void show(String msg, CallbackContext callbackContext) {
        if (msg == null || msg.length() == 0) {
            callbackContext.error("Empty message!");
        } else {
//            FirebaseRemoteConfig.getInstance().fetch(0);
//            FirebaseRemoteConfig.getInstance().activateFetched();
//            FirebaseRemoteConfigValue value = FirebaseRemoteConfig.getInstance().getValue(msg);

//            Toast.makeText(webView.getContext(), msg, Toast.LENGTH_LONG).show();
//            callbackContext.success(value.asString());
            callbackContext.success("Success!");
        }
    }
}