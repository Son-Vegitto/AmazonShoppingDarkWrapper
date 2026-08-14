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

    public static final String PREFS_NAME =
            "amazon_launcher_preferences";

    public static final String PREF_BROWSER =
            "browser_package";

    public static final String DEFAULT_BROWSER =
            "__DEFAULT_BROWSER__";

    private static final String EDGE_CANARY_PACKAGE =
            "com.microsoft.emmx.canary";

    private static final String AMAZON_URL =
            "https://www.amazon.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        /*
         * Edge Canary is the default choice.
         */
        String browserPackage =
                prefs.getString(
                        PREF_BROWSER,
                        EDGE_CANARY_PACKAGE
                );

        openAmazon(browserPackage);
    }

    private void openAmazon(String browserPackage) {

        /*
         * "Default browser" means normal Android URL handling.
         *
         * On your phone this should go:
         * Amazon Shopping (Dark) → URLCheck → your selected browser.
         */
        if (DEFAULT_BROWSER.equals(browserPackage)) {
            openDefaultBrowser();
            return;
        }

        /*
         * Make sure the selected browser still exists.
         */
        if (!isPackageInstalled(browserPackage)) {

            /*
             * Fall back to Android's normal browser handling.
             */
            openDefaultBrowser();
            return;
        }

        openCustomTab(browserPackage);
    }

    private void openCustomTab(String browserPackage) {

        try {

            CustomTabsIntent customTabsIntent =
                    new CustomTabsIntent.Builder()
                            .setShowTitle(false)
                            .setShareState(
                                    CustomTabsIntent.SHARE_STATE_ON
                            )
                            .build();

            /*
             * Explicitly select the browser.
             *
             * This is what lets Edge Canary bypass URLCheck.
             */
            customTabsIntent.intent.setPackage(
                    browserPackage
            );

            customTabsIntent.launchUrl(
                    this,
                    Uri.parse(AMAZON_URL)
            );

        } catch (ActivityNotFoundException e) {

            /*
             * If the selected browser can't launch a Custom Tab,
             * fall back to the normal browser.
             */
            openDefaultBrowser();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to open Amazon.",
                    Toast.LENGTH_LONG
            ).show();
        }

        finish();
    }

    private void openDefaultBrowser() {

        try {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(AMAZON_URL)
            );

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
