package com.nstut.fast_food_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StripeWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private static final String TAG = "StripeWebView";
    private String baseUrl = "https://677fa46842a2.ngrok-free.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_web_view);

        webView = findViewById(R.id.webView);
        String checkoutUrl = getIntent().getStringExtra("checkoutUrl");

        Log.d(TAG, "Loading checkout URL: " + checkoutUrl);

        setupWebView();
        webView.loadUrl(checkoutUrl);
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "URL loading: " + url);

                // Kiểm tra nếu URL chứa success
                if (url.contains("/success.html") || url.contains("success")) {
                    Log.d(TAG, "Payment successful!");
                    handlePaymentSuccess();
                    return true;
                }

                // Kiểm tra nếu URL chứa cancel
                if (url.contains("/cancel.html") || url.contains("cancel")) {
                    Log.d(TAG, "Payment cancelled!");
                    handlePaymentCancel();
                    return true;
                }

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page finished loading: " + url);

                // Kiểm tra lại URL sau khi trang load xong
                if (url.contains("/success.html") || url.contains("success")) {
                    handlePaymentSuccess();
                } else if (url.contains("/cancel.html") || url.contains("cancel")) {
                    handlePaymentCancel();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "Page started loading: " + url);
            }
        });
    }

    private void handlePaymentSuccess() {
        Log.d(TAG, "Handling payment success");
        Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("payment_status", "success");
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void handlePaymentCancel() {
        Log.d(TAG, "Handling payment cancel");
        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("payment_status", "cancelled");
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // Người dùng nhấn back -> coi như hủy thanh toán
            handlePaymentCancel();
        }
    }
}