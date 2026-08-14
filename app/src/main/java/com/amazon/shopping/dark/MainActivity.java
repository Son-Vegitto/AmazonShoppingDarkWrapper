package com.amazon.shopping.dark;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String AMAZON_URL = "https://www.amazon.com/";
    private static final String EDGE_CANARY_PACKAGE =
            "com.microsoft.emmx.canary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Immediately open Amazon using the saved browser selection.
        openAmazon();
    }

    private void openAmazon() {

        String browser = getSharedPreferences(
                "settings",
                MODE_PRIVATE
        ).getString(
                "browser",
                "edge_canary"
        );

        switch (browser) {

            case "edge_canary":
                openWithEdgeCanary();
                break;

            case "chrome":
                openWithPackage(
                        "com.android.chrome"
                );
                break;

            case "samsung_internet":
                openWithPackage(
                        "com.sec.android.app.sbrowser"
                );
                break;

            case "default":
            default:
                openWithDefaultBrowser();
                break;
        }
    }

    /**
     * Opens Amazon directly in Edge Canary.
     *
     * This intentionally does NOT use a Custom Tab.
     * Edge receives a normal ACTION_VIEW intent, but we
     * explicitly target the Canary package.
     */
    private void openWithEdgeCanary() {

        try {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(AMAZON_URL)
            );

            intent.setPackage(EDGE_CANARY_PACKAGE);

            /*
             * Used by some Android browser/launcher
             * implementations to identify an application-
             * associated URL.
             */
            intent.putExtra(
                    "com.android.browser.application_id",
                    EDGE_CANARY_PACKAGE
            );

            /*
             * Ask Android for a separate task rather than
             * simply reusing the existing Edge task.
             */
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            );

            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            // Edge Canary isn't installed.
            openWithDefaultBrowser();
        }

        finish();
    }

    /**
     * Opens the URL using a specifically selected browser.
     */
    private void openWithPackage(String packageName) {

        try {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(AMAZON_URL)
            );

            intent.setPackage(packageName);

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            );

            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            openWithDefaultBrowser();
        }

        finish();
    }

    /**
     * Lets Android use the user's normal default browser.
     *
     * In your setup this can be URLCheck.
     */
    private void openWithDefaultBrowser() {

        try {

            Intent intent = new Intent(
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

        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
