package com.amazon.shopping.dark;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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

        finish();
    }

    /**
     * Open Amazon directly in Edge Canary.
     *
     * This deliberately uses ACTION_VIEW rather than
     * Custom Tabs so we can test whether Edge will give
     * us the standalone-style presentation.
     */
    private void openWithEdgeCanary() {

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(AMAZON_URL)
                    );

            intent.setPackage(
                    EDGE_CANARY_PACKAGE
            );

            /*
             * Browser/application identifier.
             */
            intent.putExtra(
                    "com.android.browser.application_id",
                    EDGE_CANARY_PACKAGE
            );

            /*
             * Request a separate Android task.
             *
             * This should NOT interfere with the user's
             * existing Edge tabs/session.
             */
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            );

            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            /*
             * Edge Canary isn't available.
             * Fall back to the normal default browser.
             */
            openWithDefaultBrowser();
        }
    }

    /**
     * Open using the user's Android default browser.
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

        } catch (ActivityNotFoundException ignored) {
            // No browser available.
        }
    }

    /**
     * Open using a specifically selected browser.
     *
     * This allows Chrome, Samsung Internet, or another
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

        } catch (ActivityNotFoundException e) {

            openWithDefaultBrowser();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
