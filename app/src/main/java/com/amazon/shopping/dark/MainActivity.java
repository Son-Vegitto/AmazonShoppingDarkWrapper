package com.amazon.shopping.dark;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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
             * Dark toolbar to match Amazon's dark appearance.
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
             * Don't display the page title in the toolbar.
             */
            builder.setShowTitle(false);

            /*
             * Allow the URL portion of the toolbar to
             * collapse when the page scrolls, if supported
             * by Edge Canary.
             */
            builder.setUrlBarHidingEnabled(true);

            CustomTabsIntent customTabsIntent =
                    builder.build();

            /*
             * Force the Custom Tab to use Edge Canary
             * instead of URLCheck or another browser.
             */
            customTabsIntent.intent.setPackage(
                    EDGE_CANARY_PACKAGE
            );

            /*
             * Request a separate Android task.
             *
             * This should leave the user's existing Edge
             * tabs/session intact.
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

        } catch (ActivityNotFoundException e) {

            /*
             * Edge Canary isn't available or doesn't
             * provide the required Custom Tabs service.
             */
            openWithDefaultBrowser();
        }
    }

    /**
     * Open Amazon using Android's normal default browser.
     *
     * In your setup this is URLCheck.
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

        } catch (ActivityNotFoundException ignored) {

            // No browser is available.
        }
    }

    /**
     * Open Amazon using a specifically selected
     * browser package.
     *
     * This is used for Chrome, Samsung Internet,
     * or other browsers discovered by SettingsActivity.
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

        } catch (ActivityNotFoundException e) {

            /*
             * If the selected browser is no longer installed,
             * fall back to the Android default browser.
             */
            openWithDefaultBrowser();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
