package com.example.zzy.aiad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.annotation.TargetApi;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private WebView mWebView;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //从assets导入html
        mWebView = findViewById(R.id.webview);
        mWebView.loadUrl("file:///android_asset/web.html");
        //打开js支持
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JsInteration(), "android");
        //设置链接也在app内打开
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals("file:///android_asset/test2.html")) {
                    Log.e(TAG, "shouldOverrideUrlLoading: " + url);
                    startActivity(new Intent(MainActivity.this,Main2Activity.class));
                    return true;
                } else {
                    mWebView.loadUrl(url);
                    return false;
                }
            }
        });
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onClick(View v) {

        mWebView.evaluateJavascript("sum(1,2)", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onReceiveValue value=" + value);
            }
        });
    }
    public class JsInteration {

        @JavascriptInterface
        public String back() {
            return "hello world";
        }
    }

}



