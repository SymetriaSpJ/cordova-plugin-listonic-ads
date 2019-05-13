package com.fitatu.phonegap.plugin;


import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;
import android.widget.Toast;

import android.app.Activity;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;

import android.graphics.Color;

//import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;

import com.listonic.ad.companion.base.AdCompanion;
import com.listonic.ad.companion.display.DisplayAdContainer;
import com.listonic.ad.companion.display.DisplayAdPresenter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.ContextWrapper;

public class ListonicAds extends CordovaPlugin {

    private RelativeLayout adViewLayout = null;

    private ViewGroup parentView;

    @Override
    public void initialize(CordovaInterface cordova, final CordovaWebView webView) {
        super.initialize(cordova, webView);

        System.out.println("#debug ListonicAds initialize start");

        Activity activity = cordova.getActivity();

        System.out.println("#debug ListonicAds initialize 2");

        AdCompanion.INSTANCE.initialize(activity.getApplication(), null, false);

        System.out.println("#debug ListonicAds initialize 3 (before ui thread)");

        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {


                System.out.println("#debug ListonicAds initialize 4");

                final CordovaWebView wv = webView;

                System.out.println("#debug ListonicAds initialize 5");

                ViewGroup wvParentView = (ViewGroup) getWebView(wv).getParent();

                System.out.println("#debug ListonicAds initialize 6");
                if (parentView == null) {
                    parentView = new LinearLayout(webView.getContext());
                }
                System.out.println("#debug ListonicAds initialize 7");
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
                System.out.println("#debug ListonicAds initialize 8");
                DisplayAdContainer listonicAd = new DisplayAdContainer(webView.getContext());
                System.out.println("#debug ListonicAds initialize 9");
                DisplayAdPresenter presenter = new DisplayAdPresenter(
                        "home_page",
                        listonicAd,
                        getLifecycleOwner(cordova.getActivity().getApplicationContext())
                        cordova.getActivity()
                );
                System.out.println("#debug ListonicAds initialize 10");

//                View adMock = new LinearLayout(webView.getContext());
//                LinearLayout.LayoutParams adMockParams = new LinearLayout.LayoutParams(280, 100);
//                adMockParams.gravity = Gravity.CENTER;
//                adMock.setLayoutParams(adMockParams);
//                adMock.setBackgroundColor(Color.parseColor("#0000FF"));

                System.out.println("#debug ListonicAds initialize 11");
                parentView.addView(listonicAd);
                System.out.println("#debug ListonicAds initialize 12");
                parentView.bringToFront();
                System.out.println("#debug ListonicAds initialize 13");
                parentView.requestLayout();
                System.out.println("#debug ListonicAds initialize 14");
                parentView.requestFocus();
                System.out.println("#debug ListonicAds initialize 15");
            }
        });
        System.out.println("#debug ListonicAds initialize end (after ui thread)");

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

    private LifecycleOwner getLifecycleOwner(Context context) {
        while (!(context instanceof LifecycleOwner)) {
            context = context.getBaseContext();
        }
        return (LifecycleOwner) context;
    }
}




// code for full screen (PoC)
/*
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                if (adViewLayout == null) {
                    adViewLayout = new RelativeLayout(cordova.getActivity());
                    adViewLayout.setBackgroundColor(Color.parseColor("#000000"));

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    );

                    try {
                        ((ViewGroup) (((View) webView.getClass().getMethod("getView")
                            .invoke(webView)).getParent()))
                            .addView(adViewLayout, params);
                    } catch (Exception e) {
                        ((ViewGroup) webView).addView(adViewLayout, params);
                    }
                }

                adViewLayout.bringToFront();
*/