package com.amazon.shopping.dark;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

public class MainActivity extends AppCompatActivity {

    private static final String EDGE_CANARY_PACKAGE =
            "com.microsoft.emmx.canary";

    private static final String AMAZON_URL =
            "https://www.amazon.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openAmazonInEdgeCustomTab();
    }

    private void openAmazonInEdgeCustomTab() {

        try {
            CustomTabsIntent customTabsIntent =
                    new CustomTabsIntent.Builder()
                            .setShowTitle(false)
                            .build();

            /*
             * Directly target Microsoft Edge Canary.
             * This bypasses URLCheck.
             */
            customTabsIntent.intent.setPackage(
                    EDGE_CANARY_PACKAGE
            );

            customTabsIntent.launchUrl(
                    this,
                    Uri.parse(AMAZON_URL)
            );

        } catch (ActivityNotFoundException e) {

            Toast.makeText(
                    this,
                    "Microsoft Edge Canary is not installed.",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Edge Canary could not open Amazon.",
                    Toast.LENGTH_LONG
            ).show();
        }

        finish();
    }
}
