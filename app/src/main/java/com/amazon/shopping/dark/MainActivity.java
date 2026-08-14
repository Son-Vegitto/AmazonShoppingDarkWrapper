package com.amazon.shopping.dark;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String EDGE_CANARY_PACKAGE =
            "com.microsoft.emmx.canary";

    private static final String AMAZON_URL =
            "https://www.amazon.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        openAmazonInEdgeCanary();
    }

    private void openAmazonInEdgeCanary() {

        try {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(AMAZON_URL)
            );

            /*
             * Directly launch Edge Canary.
             *
             * This bypasses URLCheck and lets Edge handle
             * Amazon using its normal browser environment.
             */
            intent.setPackage(EDGE_CANARY_PACKAGE);

            /*
             * Start the browser without attempting to manipulate
             * or clear Edge's existing task/session.
             */
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            Toast.makeText(
                    this,
                    "Microsoft Edge Canary is not installed.",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to open Amazon in Edge Canary.",
                    Toast.LENGTH_LONG
            ).show();
        }

        finish();
    }
}
