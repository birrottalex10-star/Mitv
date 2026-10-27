package com.mitv.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        web = new WebView(this);
        web.setBackgroundColor(0xFF0E1116);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);

        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new Bridge(), "Android");
        web.setFocusable(true);
        web.setFocusableInTouchMode(true);
        web.requestFocus();
        web.loadUrl("file:///android_asset/www/index.html");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (web != null) web.requestFocus();
    }

    private class Bridge {
        @JavascriptInterface
        public boolean launch(String csv) {
            PackageManager pm = getPackageManager();
            for (String raw : csv.split(",")) {
                final String pkg = raw.trim();
                if (pkg.isEmpty()) continue;
                Intent i = pm.getLeanbackLaunchIntentForPackage(pkg);
                if (i == null) i = pm.getLaunchIntentForPackage(pkg);
                if (i != null) {
                    final Intent launch = i;
                    launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(launch);
                        }
                    });
                    return true;
                }
            }
            return false;
        }
    }
  }
