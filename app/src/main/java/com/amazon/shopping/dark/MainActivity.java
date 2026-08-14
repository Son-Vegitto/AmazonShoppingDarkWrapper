package com.amazon.shopping.dark;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME =
            "amazon_shopping_settings";

    public static final String PREF_BROWSER =
            "browser";

    public static final String DEFAULT_BROWSER =
            "default";

    private static final String AMAZON_URL =
            "https://www.amazon.com/";

    private static final String EDGE_CANARY_PACKAGE =
            "com.microsoft.emmx.canary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openAmazon();
    }

    /**
     * Determine which browser the user selected
     * in SettingsActivity.
     */
    private void openAmazon() {

        String browser =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                ).getString(
                        PREF_BROWSER,
                        EDGE_CANARY_PACKAGE
                );

        if (EDGE_CANARY_PACKAGE.equals(browser)) {

            openWithEdgeCanary();

        } else if (DEFAULT_BROWSER.equals(browser)) {

            openWithDefaultBrowser();

        } else {

            openWithPackage(browser);
        }
    }

    /**
     * Open Amazon using Edge Canary's Custom Tabs
     * implementation.
     */
    private void openWithEdgeCanary() {

        try {

            CustomTabsIntent.Builder builder =
                    new CustomTabsIntent.Builder();

            /*
             * Dark toolbar.
             */
            CustomTabColorSchemeParams darkParams =
                    new CustomTabColorSchemeParams.Builder()
                            .setToolbarColor(
                                    0xFF121212
                            )
                            .build();

            builder.setDefaultColorSchemeParams(
                    darkParams
            );

            /*
             * Don't display the page title.
             */
            builder.setShowTitle(false);

            /*
             * Allow Edge to hide/collapse the URL bar
             * while scrolling, if supported.
             */
            builder.setUrlBarHidingEnabled(true);

            CustomTabsIntent customTabsIntent =
                    builder.build();

            /*
             * Force Edge Canary as the Custom Tab provider.
             *
             * This bypasses URLCheck when Edge Canary
             * is selected.
             */
            customTabsIntent.intent.setPackage(
                    EDGE_CANARY_PACKAGE
            );

            /*
             * Request a separate Android task.
             *
             * This should preserve the user's existing
             * Edge tabs/session.
             */
            customTabsIntent.intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            );

            /*
             * Launch Amazon.
             */
            customTabsIntent.launchUrl(
                    this,
                    Uri.parse(AMAZON_URL)
            );

            /*
             * Remove the wrapper Activity without
             * displaying a transition animation.
             */
            finishWithoutAnimation();

        } catch (ActivityNotFoundException e) {

            /*
             * Edge Canary isn't available.
             */
            openWithDefaultBrowser();
        }
    }

    /**
     * Open Amazon using Android's normal default browser.
     *
     * In your setup this is normally URLCheck.
     */
    private void openWithDefaultBrowser() {

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(AMAZON_URL)
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            );

            startActivity(intent);

            finishWithoutAnimation();

        } catch (ActivityNotFoundException ignored) {

            finishWithoutAnimation();
        }
    }

    /**
     * Open Amazon using a specifically selected
     * browser package.
     *
     * Used for Chrome, Samsung Internet, Edge
     * variants, or another browser discovered
     * by SettingsActivity.
     */
    private void openWithPackage(
            String packageName
    ) {

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(AMAZON_URL)
                    );

            intent.setPackage(
                    packageName
            );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            );

            startActivity(intent);

            finishWithoutAnimation();

        } catch (ActivityNotFoundException e) {

            /*
             * Selected browser is unavailable.
             * Fall back to the default browser.
             */
            openWithDefaultBrowser();
        }
    }

    /**
     * Finish this wrapper Activity without showing
     * a closing/opening transition animation.
     */
    private void finishWithoutAnimation() {

        finish();

        /*
         * Android 14/API 34 and newer.
         */
        if (Build.VERSION.SDK_INT >= 34) {

            overrideActivityTransition(
                    OVERRIDE_TRANSITION_CLOSE,
                    0,
                    0
            );

        } else {

            /*
             * Older Android versions.
             */
            overridePendingTransition(
                    0,
                    0
            );
        }
    }
}
