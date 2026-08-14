package com.amazon.shopping.dark;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowInsetsController;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();

        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);

        window.getDecorView().setBackgroundColor(Color.BLACK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();

            if (controller != null) {
                controller.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                                | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }
        }

        webView = new WebView(this);
        webView.setBackgroundColor(Color.BLACK);

        setContentView(webView);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request) {

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                injectDarkMode(view);
                hideOpenInAppBanner(view);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(true);

        settings.setMediaPlaybackRequiresUserGesture(true);

        CookieManager cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);
        cookies.setAcceptThirdPartyCookies(webView, true);

        /*
         * Use Android WebView algorithmic darkening when available.
         * Failure here will NOT prevent the application from starting.
         */
        try {
            if (WebViewFeature.isFeatureSupported(
                    WebViewFeature.ALGORITHMIC_DARKENING)) {

                WebSettingsCompat.setAlgorithmicDarkeningAllowed(
                        settings,
                        true
                );
            }
        } catch (Throwable ignored) {
            // Continue without algorithmic darkening.
        }

        webView.loadUrl("https://www.amazon.com/");

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        if (webView != null && webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            finish();
                        }
                    }
                }
        );
    }

    private void injectDarkMode(WebView view) {

        String javascript =
                "javascript:(function() {" +

                "var style = document.getElementById('amazon-dark-mode');" +

                "if (!style) {" +

                "style = document.createElement('style');" +
                "style.id = 'amazon-dark-mode';" +

                "style.innerHTML = `" +

                "html, body {" +
                "background-color:#000000 !important;" +
                "color:#ffffff !important;" +
                "}" +

                "body * {" +
                "border-color:#444444 !important;" +
                "}" +

                "a {" +
                "color:#7db7ff !important;" +
                "}" +

                "input, textarea, select {" +
                "background-color:#111111 !important;" +
                "color:#ffffff !important;" +
                "border-color:#555555 !important;" +
                "}" +

                "header, nav, footer, section, article, div {" +
                "background-color:transparent !important;" +
                "}" +

                "[style*='background-color: white'], " +
                "[style*='background-color:#fff'], " +
                "[style*='background-color: #fff'] {" +
                "background-color:#000000 !important;" +
                "}" +

                "`;" +

                "document.head.appendChild(style);" +

                "}" +

                "})();";

        view.evaluateJavascript(javascript, null);
    }

    private void hideOpenInAppBanner(WebView view) {

        String javascript =
                "javascript:(function() {" +

                "var selectors = [" +
                "'[id*=\"open-app\"]'," +
                "'[class*=\"open-app\"]'," +
                "'[id*=\"app-banner\"]'," +
                "'[class*=\"app-banner\"]'," +
                "'[id*=\"appDownload\"]'," +
                "'[class*=\"appDownload\"]'" +
                "];" +

                "selectors.forEach(function(selector) {" +

                "document.querySelectorAll(selector).forEach(function(element) {" +
                "element.style.display='none';" +
                "});" +

                "});" +

                "})();";

        view.evaluateJavascript(javascript, null);
    }

    @Override
    protected void onDestroy() {

        if (webView != null) {
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }

        super.onDestroy();
    }
}
