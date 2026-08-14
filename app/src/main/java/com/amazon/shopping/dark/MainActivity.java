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
             * Dark toolbar to match the Amazon dark appearance.
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
             * collapse while scrolling, if supported
             * by Edge Canary.
             */
            builder.setUrlBarHidingEnabled(true);

            CustomTabsIntent customTabsIntent =
                    builder.build();

            /*
             * Force the Custom Tab to use Edge Canary.
             *
             * This bypasses URLCheck for this selection.
             */
            customTabsIntent.intent.setPackage(
                    EDGE_CANARY_PACKAGE
            );

            /*
             * Request a separate Android task.
             *
             * This should not close or replace the user's
             * existing Edge tabs/session.
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
             * This application is only a launcher/wrapper.
             * Remove it after Edge has been launched.
             */
            finish();

        } catch (ActivityNotFoundException e) {

            /*
             * Edge Canary isn't available or cannot handle
             * the Custom Tab request.
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

            /*
             * Remove the wrapper activity after launching
             * the browser.
             */
            finish();

        } catch (ActivityNotFoundException ignored) {

            /*
             * There is no browser available.
             */
            finish();
        }
    }

    /**
     * Open Amazon using a specifically selected browser.
     *
     * This is used for Chrome, Samsung Internet,
     * or another browser discovered by SettingsActivity.
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

            /*
             * Remove the wrapper activity after launching
             * the selected browser.
             */
            finish();

        } catch (ActivityNotFoundException e) {

            /*
             * The selected browser may have been removed
             * or disabled. Fall back to the default browser.
             */
            openWithDefaultBrowser();
        }
    }
}
