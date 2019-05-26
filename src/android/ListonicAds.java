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
//                    parentView = new ConstraintLayout(webView.getContext());
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

//                    ConstraintLayout.LayoutParams parentViewParams = new ConstraintLayout.LayoutParams(
//                            ConstraintLayout.LayoutParams.MATCH_PARENT,
//                            ConstraintLayout.LayoutParams.MATCH_PARENT
//                    );
//                    parentViewParams.verticalWeight = 0.0F;
//                    parentViewParams.orientation = ConstraintLayout.LayoutParams.VERTICAL;
//                    parentView.setLayoutParams(parentViewParams);


                    parentView.setBackgroundColor(Color.parseColor("#F7F8F9"));
                    parentView.setClipChildren(false);
                    parentView.setClipToPadding(false);
                    parentView.addView(getWebView(webView));
                    rootView.setClipChildren(false);
                    rootView.setClipToPadding(false);
                    setAllParentsClip(parentView, false);
                    rootView.addView(parentView);
                }

                listonicAd = new DisplayAdContainer(webView.getContext());
                listonicAd.setBackgroundColor(Color.parseColor("#F7F8F9"));
                listonicAd.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );

                parentView.addView(listonicAd);
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

    private void prepare(JSONObject options, CallbackContext callbackContext) {
        System.out.println("#debug ListonicAds prepare start");
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
                String zone = "";

                try {
                    zone = options.getString("zone");
                } catch(JSONException e) {
                    throw new IOError(e);
                }

                if (presenter != null) {
                    presenter.onDestroy();
                }

                presenter = new LegacyDisplayAdPresenter(
                        zone,
                        listonicAd,
                        new HashMap<String, String>(),
                        null
                );

                presenter.onCreate();
                presenter.onStart();

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