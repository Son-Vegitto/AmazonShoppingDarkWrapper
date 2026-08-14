package com.amazon.shopping.dark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        String saved =
                prefs.getString(
                        PREF_BROWSER,
                        BROWSER_EDGE
                );

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        int padding = dp(24);

        layout.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        TextView title =
                new TextView(this);

        title.setText(
                "Amazon Shopping (Dark)"
        );

        title.setTextSize(22);

        title.setGravity(
                Gravity.CENTER_VERTICAL
        );

        layout.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(60)
                )
        );

        TextView description =
                new TextView(this);

        description.setText(
                "Open Amazon with:"
        );

        description.setTextSize(16);

        layout.addView(
                description,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(50)
                )
        );

        String[] browserNames = {
                "Edge Canary",
                "Default browser",
                "Chrome",
                "Samsung Internet"
        };

        String[] browserValues = {
                BROWSER_EDGE,
                BROWSER_DEFAULT,
                BROWSER_CHROME,
                BROWSER_SAMSUNG
        };

        Spinner spinner =
                new Spinner(this);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        browserNames
                );

        adapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);

        int selected = 0;

        for (int i = 0;
             i < browserValues.length;
             i++) {

            if (browserValues[i].equals(saved)) {
                selected = i;
                break;
            }
        }

        spinner.setSelection(selected);

        layout.addView(
                spinner,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(60)
                )
        );

        TextView save =
                new TextView(this);

        save.setText(
                "Save"
        );

        save.setTextSize(17);

        save.setGravity(
                Gravity.CENTER
        );

        save.setClickable(true);

        save.setFocusable(true);

        LinearLayout.LayoutParams
                saveParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(56)
                );

        saveParams.topMargin = dp(24);

        layout.addView(
                save,
                saveParams
        );

        save.setOnClickListener(
                v -> {

                    int position =
                            spinner.getSelectedItemPosition();

                    String value =
                            browserValues[position];

                    prefs.edit()
                            .putString(
                                    PREF_BROWSER,
                                    value
                            )
                            .apply();

                    finish();
                }
        );

        setContentView(layout);
    }

    private int dp(int value) {

        return Math.round(
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }
}
