package com.amazon.shopping.dark;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
     * Attempt to launch Amazon using Edge Canary's
     * Chromium web-app/shortcut launch mechanism.
     *
     * These web-app extras are internal Chromium/Edge
     * implementation details and are being tested here
     * specifically to see whether Edge Canary recognizes
     * them as a standalone web-app launch.
     */
    private void openWithEdgeCanary() {

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(AMAZON_URL)
                    );

            /*
             * Force Edge Canary.
             *
             * This bypasses URLCheck when Edge Canary
             * is selected.
             */
            intent.setPackage(
                    EDGE_CANARY_PACKAGE
            );

            /*
             * Chromium web-app launch metadata.
             *
             * 3 is the Chromium shortcut source value
             * suggested for this type of launch.
             */
            intent.putExtra(
                    "org.chromium.chrome.browser.webapp_source",
                    3
            );

            /*
             * Identify Amazon as the web-app ID.
             */
            intent.putExtra(
                    "com.microsoft.emmx.webapp_id",
                    AMAZON_URL
            );

            /*
             * Request a separate Android task.
             *
             * This should avoid reusing the user's existing
             * Edge browser task and should not close or
             * replace existing Edge tabs.
             */
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            );

            /*
             * Launch Edge Canary.
             */
            startActivity(intent);

            /*
             * This wrapper has finished its job.
             */
            finishWithoutAnimation();

        } catch (ActivityNotFoundException e) {

            /*
             * Edge Canary isn't available or cannot handle
             * the request. Fall back to the default browser.
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
     * Open Amazon using a specifically selected browser.
     *
     * Used for Chrome, Samsung Internet, or another
     * browser discovered by SettingsActivity.
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
             * Selected browser is no longer available.
             * Fall back to the default browser.
             */
            openWithDefaultBrowser();
        }
    }

    /**
     * Finish this wrapper without showing its closing
     * transition animation.
     */
    private void finishWithoutAnimation() {

        finish();

        if (Build.VERSION.SDK_INT >= 34) {

            overrideActivityTransition(
                    OVERRIDE_TRANSITION_CLOSE,
                    0,
                    0
            );

        } else {

            overridePendingTransition(
                    0,
                    0
            );
        }
    }
}
