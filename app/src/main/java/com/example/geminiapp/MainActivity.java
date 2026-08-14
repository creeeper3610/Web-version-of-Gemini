package com.example.geminiapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private static WebView mWebView; // 静的変数にしてメモリ上に強力に保持

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 常駐サービス（通知表示）を起動
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Android 13以降のために、通知権限のポップアップを自動要求
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        // WebViewがまだ作られていなければ、アプリケーションコンテキスト（最軽量）で動的生成
        if (mWebView == null) {
            mWebView = new WebView(getApplicationContext());
            
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);       // JSを有効化
            webSettings.setDomStorageEnabled(true);       // ログイン維持に必須
            webSettings.setDatabaseEnabled(true);
            
            // スマホ表示にするためのUserAgent偽装（PC版を回避）
            webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // Googleログインや別ページ遷移もアプリ内で維持
                    view.loadUrl(url);
                    return true;
                }
            });

            mWebView.loadUrl("https://google.com");
        }

        // 現在の画面にWebViewを貼り付ける
        setContentView(mWebView);
    }

    @Override
    public void onBackPressed() {
        // 戻るボタンを押した際、前のページに戻れるなら戻る
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
