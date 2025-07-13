package com.nstut.fast_food_shop;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VnpayWebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        setContentView(webView);

        String url = getIntent().getStringExtra("paymentUrl");
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("https://webhook.site/")) {
                    if (url.contains("vnp_ResponseCode=00")) {
                        Toast.makeText(VnpayWebViewActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VnpayWebViewActivity.this, "Thanh toán thất bại hoặc bị huỷ!", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                    return true;
                }
                return false;
            }
        });

        webView.loadUrl(url);
    }
}
