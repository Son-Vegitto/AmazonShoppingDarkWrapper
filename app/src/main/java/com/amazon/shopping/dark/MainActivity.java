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

                applyAmazonFixes(view);
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

        // Android/WebView algorithmic darkening.
        try {
            if (WebViewFeature.isFeatureSupported(
                    WebViewFeature.ALGORITHMIC_DARKENING)) {

                WebSettingsCompat.setAlgorithmicDarkeningAllowed(
                        settings,
                        true
                );
            }
        } catch (Throwable ignored) {
            // Continue normally if the WebView does not support it.
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

    private void applyAmazonFixes(WebView view) {

        String javascript =
                "javascript:(function() {" +

                /*
                 * =====================================================
                 * 1. IMAGE PROTECTION
                 * =====================================================
                 */

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

                "document.querySelectorAll('img, picture img, video, canvas').forEach(function(el) {" +
                "el.style.setProperty('filter', 'none', 'important');" +
                "el.style.setProperty('mix-blend-mode', 'normal', 'important');" +
                "});" +


                /*
                 * =====================================================
                 * 2. MAKE THE DEVICE PRODUCT LABELS READABLE
                 * =====================================================
                 */

                "function fixDeviceSection() {" +

                "var headings = document.querySelectorAll('h1,h2,h3,h4,h5,h6,span,div');" +

                "headings.forEach(function(heading) {" +

                "var text = (heading.innerText || '').trim();" +

                "if (text === 'Save on Amazon Devices') {" +

                "var section = heading;" +

                /*
                 * Walk upward to find a reasonable section container.
                 */
                "for (var i = 0; i < 6 && section.parentElement; i++) {" +
                "section = section.parentElement;" +
                "}" +

                /*
                 * Make text inside the section readable while
                 * preserving the dark background.
                 */
                "section.querySelectorAll('span,div,p,a').forEach(function(el) {" +

                "var value = (el.innerText || '').trim();" +

                "if (value.length > 0 && value.length < 120) {" +
                "el.style.setProperty('color', '#ffffff', 'important');" +
                "}" +

                "});" +

                "}" +

                "});" +
                "}" +

                "fixDeviceSection();" +


                /*
                 * =====================================================
                 * 3. HIDE AMAZON 'OPEN IN APP' BANNER
                 * =====================================================
                 */

                "function hideAmazonAppBanner() {" +

                "var elements = document.querySelectorAll('body *');" +

                "elements.forEach(function(el) {" +

                "var text = (el.innerText || '').trim();" +

                "if (!text || text.length > 300) return;" +

                "var lower = text.toLowerCase();" +

                "if (" +
                "lower.indexOf('open in app') !== -1 || " +
                "lower.indexOf('open in the amazon app') !== -1 || " +
                "lower.indexOf('open in amazon shopping') !== -1" +
                ") {" +

                /*
                 * Only hide elements that look like the banner,
                 * rather than hiding arbitrary page text.
                 */
                "var rect = el.getBoundingClientRect();" +

                "if (rect.top < 250 && rect.height < 250) {" +
                "el.style.setProperty('display', 'none', 'important');" +
                "}" +

                "}" +

                "});" +
                "}" +

                "hideAmazonAppBanner();" +


                /*
                 * =====================================================
                 * 4. WATCH FOR AMAZON DYNAMICALLY RECREATING THE BANNER
                 * =====================================================
                 */

                "if (!window.amazonDarkObserver) {" +

                "window.amazonDarkObserver = new MutationObserver(function() {" +

                "hideAmazonAppBanner();" +
                "fixDeviceSection();" +

                "document.querySelectorAll('img, picture img, video, canvas').forEach(function(el) {" +
                "el.style.setProperty('filter', 'none', 'important');" +
                "el.style.setProperty('mix-blend-mode', 'normal', 'important');" +
                "});" +

                "});" +

                "window.amazonDarkObserver.observe(document.documentElement, {" +
                "childList: true," +
                "subtree: true" +
                "});" +

                "}" +

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
