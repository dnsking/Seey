package com.payments.app.seey.rapyd;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.payments.app.seey.App;
import com.payments.app.seey.R;

public class RapydPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapyd_payment);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getIntent().getStringExtra(App.Content));
        webView.setWebViewClient(webViewClient);
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
            /*if (url.contains("code=")) {
                Uri uri = Uri.EMPTY.parse(url);
                String access_token = uri.getEncodedFragment();
                access_token = access_token.substring(access_token.lastIndexOf("=") + 1);
                App.Log("Instagram key "+access_token);
            }*/
        }
    };
}