package com.payments.app.seey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebIntegrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String request_url = "https://api.instagram.com/oauth/authorize?client_id=317516206735709&redirect_uri=https://httpstat.us/200&scope=user_profile,user_media&response_type=code";

        //String request_url = "https://api.instagram.com/oauth/authorize?client_id=317516206735709&redirect_uri=app://seey&scope=user_profile,user_media&response_type=code";

        setContentView(R.layout.activity_web_integration);


        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(request_url));

        /*

        WebView webView = findViewById(R.id.signInWebview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(request_url);
        webView.setWebViewClient(webViewClient);*/
    }

    WebViewClient webViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("https://httpstat.us/")) {
                finish();
                return true;
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.contains("code=")) {
                Uri uri = Uri.EMPTY.parse(url);
                String access_token = uri.getEncodedFragment();
                access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
               App.Log("Instagram key "+access_token);
            }
        }
    };

}