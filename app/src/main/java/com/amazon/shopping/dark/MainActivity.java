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

        // Black system bars.
        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);
        window.getDecorView().setBackgroundColor(Color.BLACK);

        // Native white system icons.
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

        // WebView.
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

                protectImages(view);
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

        // Cookies.
        CookieManager cookies = CookieManager.getInstance();
        cookies.setAcceptCookie(true);
        cookies.setAcceptThirdPartyCookies(webView, true);

        // Android/WebView algorithmic darkening.
        // If unsupported, continue normally.
        try {
            if (WebViewFeature.isFeatureSupported(
                    WebViewFeature.ALGORITHMIC_DARKENING)) {

                WebSettingsCompat.setAlgorithmicDarkeningAllowed(
                        settings,
                        true
                );
            }
        } catch (Throwable ignored) {
            // Do not allow darkening failure to crash the app.
        }

        webView.loadUrl("https://www.amazon.com/");

        // Back navigation.
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

    /**
     * Protect product images from unwanted CSS transformations.
     *
     * Amazon's "Save on Amazon Devices" product cards can contain
     * image assets that WebView's darkening algorithm renders completely
     * black. This restores normal rendering for image elements without
     * disabling dark mode for the rest of the website.
     */
    private void protectImages(WebView view) {

        String javascript =
                "javascript:(function() {" +

                "var style = document.getElementById('amazon-dark-image-fix');" +

                "if (!style) {" +

                "style = document.createElement('style');" +
                "style.id = 'amazon-dark-image-fix';" +

                "style.innerHTML = `" +

                "img {" +
                "filter: none !important;" +
                "mix-blend-mode: normal !important;" +
                "}" +

                "picture img {" +
                "filter: none !important;" +
                "mix-blend-mode: normal !important;" +
                "}" +

                "video {" +
                "filter: none !important;" +
                "mix-blend-mode: normal !important;" +
                "}" +

                "canvas {" +
                "filter: none !important;" +
                "mix-blend-mode: normal !important;" +
                "}" +

                "`;" +

                "document.head.appendChild(style);" +

                "}" +

                // Also remove any inline filters from image elements.
                "document.querySelectorAll('img, picture img, video, canvas').forEach(function(el) {" +
                "el.style.setProperty('filter', 'none', 'important');" +
                "el.style.setProperty('mix-blend-mode', 'normal', 'important');" +
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
