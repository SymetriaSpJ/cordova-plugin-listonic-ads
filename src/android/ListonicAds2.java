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

        AdCompanion.INSTANCE.initialize(activity.getApplication(), null, false);

        activity.runOnUiThread(new Runnable() {
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

                    ((LinearLayout) parentView).setOrientation(LinearLayout.VERTICAL);
                    parentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
                    getWebView(webView).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));

                    parentView.setBackgroundColor(Color.parseColor("#FF0000"));

                    parentView.addView(getWebView(webView));
                    rootView.addView(parentView);
                }
                DisplayAdContainer listonicAd = new DisplayAdContainer(webView.getContext());
                LinearLayout.LayoutParams listonicAdParams = new LinearLayout.LayoutParams(280, 100);
                listonicAdParams.gravity = Gravity.CENTER;
                listonicAd.setLayoutParams(listonicAdParams);
                listonicAd.setBackgroundColor(Color.parseColor("#0000FF"));


                LegacyDisplayAdPresenter presenter = new LegacyDisplayAdPresenter(
                        "home_page",
                        listonicAd,
                        new HashMap<String, String>(),
                        null
                );

                presenter.onCreate();
                presenter.onStart();

//                View adMock = new LinearLayout(webView.getContext());
//                LinearLayout.LayoutParams adMockParams = new LinearLayout.LayoutParams(280, 100);
//                adMockParams.gravity = Gravity.CENTER;
//                adMock.setLayoutParams(adMockParams);
//                adMock.setBackgroundColor(Color.parseColor("#0000FF"));

                parentView.addView(listonicAd);
                parentView.bringToFront();
                parentView.requestLayout();
                parentView.requestFocus();
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

            Toast.makeText(webView.getContext(), msg, Toast.LENGTH_LONG).show();
//            callbackContext.success(value.asString());
            callbackContext.success("Success!");
        }
    }
}