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

            // Directly target Edge Canary.
            // This bypasses URLCheck.
            intent.setPackage(EDGE_CANARY_PACKAGE);

            // Ask Android/Edge for a new browsing context rather
            // than trying to manipulate the existing Edge task.
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            Toast.makeText(
                    this,
                    "Microsoft Edge Canary is not installed.",
                    Toast.LENGTH_LONG
            ).show();
        }

        /*
         * The launcher APK itself doesn't need to remain visible.
         * Edge becomes the visible application.
         */
        finish();
    }
}
