package com.nstut.fast_food_shop;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StripeWebViewActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        String checkoutUrl = getIntent().getStringExtra("checkoutUrl");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("/success")) {
                    Toast.makeText(StripeWebViewActivity.this, "Payment successful", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                } else if (url.contains("/cancel")) {
                    Toast.makeText(StripeWebViewActivity.this, "Payment canceled", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                }
                return false;
            }
        });

        webView.loadUrl(checkoutUrl);
    }
}