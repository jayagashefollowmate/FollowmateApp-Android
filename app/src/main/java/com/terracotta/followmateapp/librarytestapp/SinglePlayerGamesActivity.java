package com.terracotta.followmateapp.librarytestapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.inscripts.cometchat.sdk.CometChat;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.utils.Logger;
import com.terracotta.followmateapp.R;
import com.terracotta.followmateapp.helper.Keys;
import com.terracotta.followmateapp.helper.SharedPreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class SinglePlayerGamesActivity extends ActionBarActivity {

    private CometChat cometChat;
    private WebView webView;
    private ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_games);
        webView = (WebView) findViewById(R.id.games_webview);
        cometChat = CometChat.getInstance(getApplicationContext(),SharedPreferenceHelper.get(Keys.SharedPreferenceKeys.API_KEY));
        pBar = (ProgressBar) findViewById(com.inscripts.R.id.progressBarWebView);
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                if (progress < 100 && pBar.getVisibility() == View.GONE) {
                    pBar.setVisibility(View.VISIBLE);
                }
                pBar.setProgress(progress);
                if (progress == 100) {
                    pBar.setVisibility(View.GONE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(SinglePlayerGamesActivity.this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                cookieManager.setCookie(url, "cc_platform_cod=android;");
                cookieSyncManager.sync();
            }
        });
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        cometChat.getSinglePlayerGamesUrl(new Callbacks() {
           @Override
           public void successCallback(JSONObject jsonObject) {
               try {
                   final String url = jsonObject.getString("url");
                   Logger.debug(url);
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           webView.loadUrl(url);
                       }
                   });

               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }

           @Override
           public void failCallback(JSONObject jsonObject) {
                Logger.debug(jsonObject.toString());
           }
       });
    }

}
