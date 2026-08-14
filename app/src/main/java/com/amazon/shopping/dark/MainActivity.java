package com.amazon.shopping.dark;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

public class MainActivity extends AppCompatActivity {

    private static final String EDGE_CANARY_PACKAGE =
            "com.microsoft.emmx.canary";

    private static final String PREFS_NAME =
            "amazon_launcher_preferences";

    private static final String PREF_BROWSER =
            "browser";

    private static final String BROWSER_EDGE =
            "edge";

    private static final String BROWSER_DEFAULT =
            "default";

    private static final String BROWSER_CHROME =
            "chrome";

    private static final String BROWSER_SAMSUNG =
            "samsung";

    private static final String AMAZON_URL =
            "https://www.amazon.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String browser =
                prefs.getString(PREF_BROWSER, BROWSER_EDGE);

        /*
         * If this is the first launch, use Edge Canary.
         *
         * For now, the browser-selection UI will be added
         * separately so the launcher remains reliable.
         */
        openAmazon(browser);
    }

    private void openAmazon(String browser) {

        if (BROWSER_DEFAULT.equals(browser)) {
            openWithDefaultBrowser();
            return;
        }

        String packageName = getBrowserPackage(browser);

        if (packageName == null) {
            openWithDefaultBrowser();
            return;
        }

        if (!isPackageInstalled(packageName)) {
            openWithDefaultBrowser();
            return;
        }

        openWithCustomTab(packageName);
    }

    private void openWithCustomTab(String packageName) {

        try {

            CustomTabsIntent customTabsIntent =
                    new CustomTabsIntent.Builder()
                            .setShowTitle(false)
                            .setShareState(
                                    CustomTabsIntent.SHARE_STATE_ON
                            )
                            .build();

            /*
             * Force Custom Tabs to the selected browser.
             *
             * This is what keeps Edge Canary from being
             * replaced by URLCheck or another browser.
             */
            customTabsIntent.intent.setPackage(
                    packageName
            );

            customTabsIntent.launchUrl(
                    this,
                    Uri.parse(AMAZON_URL)
            );

        } catch (ActivityNotFoundException e) {

            openWithDefaultBrowser();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to open Amazon.",
                    Toast.LENGTH_LONG
            ).show();
        }

        finish();
    }

    private void openWithDefaultBrowser() {

        try {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(AMAZON_URL)
            );

            /*
             * No package is specified here.
             *
             * Android therefore uses the normal handler for
             * the URL. On your phone this should be URLCheck.
             */
            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            Toast.makeText(
                    this,
                    "No browser is available.",
                    Toast.LENGTH_LONG
            ).show();
        }

        finish();
    }

    private String getBrowserPackage(String browser) {

        switch (browser) {

            case BROWSER_EDGE:
                return EDGE_CANARY_PACKAGE;

            case BROWSER_CHROME:
                return "com.android.chrome";

            case BROWSER_SAMSUNG:
                return "com.sec.android.app.sbrowser";

            default:
                return null;
        }
    }

    private boolean isPackageInstalled(String packageName) {

        try {

            getPackageManager().getPackageInfo(
                    packageName,
                    0
            );

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}
