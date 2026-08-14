package com.amazon.shopping.dark;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private static final String CUSTOM_TABS_SERVICE =
            "android.support.customtabs.action.CustomTabsService";

    private static final String EDGE_CANARY_PACKAGE =
            "com.microsoft.emmx.canary";

    private final List<BrowserOption> browsers =
            new ArrayList<>();

    private Spinner browserSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buildSettingsScreen();
    }

    private void buildSettingsScreen() {

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        int padding = dp(24);

        root.setPadding(
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

        title.setTextColor(
                Color.BLACK
        );

        root.addView(
                title,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(60)
                )
        );

        TextView description =
                new TextView(this);

        description.setText(
                "Choose which browser should open Amazon."
        );

        description.setTextSize(16);

        root.addView(
                description,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(55)
                )
        );

        browsers.clear();

        /*
         * Always provide the normal Android browser option.
         */
        browsers.add(
                new BrowserOption(
                        "Default browser (URLCheck)",
                        MainActivity.DEFAULT_BROWSER
                )
        );

        /*
         * Prefer Edge Canary at the top because it is your
         * desired default.
         */
        if (isCustomTabsProviderInstalled(
                EDGE_CANARY_PACKAGE
        )) {

            browsers.add(
                    new BrowserOption(
                            "Edge Canary",
                            EDGE_CANARY_PACKAGE
                    )
            );
        }

        /*
         * Discover all other Custom Tabs providers.
         */
        List<BrowserOption> discovered =
                discoverCustomTabsBrowsers();

        for (BrowserOption option : discovered) {

            boolean alreadyAdded = false;

            for (BrowserOption existing : browsers) {

                if (existing.packageName.equals(
                        option.packageName
                )) {
                    alreadyAdded = true;
                    break;
                }
            }

            if (!alreadyAdded) {
                browsers.add(option);
            }
        }

        /*
         * Alphabetize everything except Default browser
         * and Edge Canary, which remain at the top.
         */
        if (browsers.size() > 2) {

            List<BrowserOption> sortable =
                    new ArrayList<>(
                            browsers.subList(2, browsers.size())
                    );

            Collections.sort(
                    sortable,
                    new Comparator<BrowserOption>() {

                        @Override
                        public int compare(
                                BrowserOption a,
                                BrowserOption b
                        ) {
                            return a.label.compareToIgnoreCase(
                                    b.label
                            );
                        }
                    }
            );

            while (browsers.size() > 2) {
                browsers.remove(
                        browsers.size() - 1
                );
            }

            browsers.addAll(sortable);
        }

        String[] names =
                new String[browsers.size()];

        for (int i = 0;
             i < browsers.size();
             i++) {

            names[i] =
                    browsers.get(i).label;
        }

        browserSpinner =
                new Spinner(this);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        names
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        browserSpinner.setAdapter(adapter);

        SharedPreferences prefs =
                getSharedPreferences(
                        MainActivity.PREFS_NAME,
                        MODE_PRIVATE
                );

        String current =
                prefs.getString(
                        MainActivity.PREF_BROWSER,
                        EDGE_CANARY_PACKAGE
                );

        int selected = 0;

        for (int i = 0;
             i < browsers.size();
             i++) {

            if (browsers.get(i)
                    .packageName
                    .equals(current)) {

                selected = i;
                break;
            }
        }

        browserSpinner.setSelection(
                selected
        );

        root.addView(
                browserSpinner,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(60)
                )
        );

        Button save =
                new Button(this);

        save.setText(
                "Save"
        );

        LinearLayout.LayoutParams
                saveParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(55)
                );

        saveParams.topMargin =
                dp(24);

        root.addView(
                save,
                saveParams
        );

        save.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        int position =
                                browserSpinner
                                        .getSelectedItemPosition();

                        if (position < 0 ||
                                position >= browsers.size()) {
                            return;
                        }

                        String packageName =
                                browsers.get(position)
                                        .packageName;

                        prefs.edit()
                                .putString(
                                        MainActivity.PREF_BROWSER,
                                        packageName
                                )
                                .apply();

                        finish();
                    }
                }
        );

        setContentView(root);
    }

    private List<BrowserOption>
    discoverCustomTabsBrowsers() {

        List<BrowserOption> result =
                new ArrayList<>();

        Intent serviceIntent =
                new Intent(
                        CUSTOM_TABS_SERVICE
                );

        PackageManager pm =
                getPackageManager();

        List<ResolveInfo> services =
                pm.queryIntentServices(
                        serviceIntent,
                        0
                );

        Set<String> seen =
                new HashSet<>();

        for (ResolveInfo resolveInfo :
                services) {

            ServiceInfo serviceInfo =
                    resolveInfo.serviceInfo;

            if (serviceInfo == null) {
                continue;
            }

            String packageName =
                    serviceInfo.packageName;

            if (packageName == null ||
                    seen.contains(packageName)) {
                continue;
            }

            seen.add(packageName);

            String label;

            try {

                label =
                        pm.getApplicationLabel(
                                pm.getApplicationInfo(
                                        packageName,
                                        0
                                )
                        ).toString();

            } catch (Exception e) {

                label = packageName;
            }

            result.add(
                    new BrowserOption(
                            label,
                            packageName
                    )
            );
        }

        return result;
    }

    private boolean
    isCustomTabsProviderInstalled(
            String packageName
    ) {

        Intent intent =
                new Intent(
                        CUSTOM_TABS_SERVICE
                );

        intent.setPackage(
                packageName
        );

        List<ResolveInfo> services =
                getPackageManager()
                        .queryIntentServices(
                                intent,
                                0
                        );

        return !services.isEmpty();
    }

    private int dp(int value) {

        return Math.round(
                value *
                        getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    private static class BrowserOption {

        final String label;
        final String packageName;

        BrowserOption(
                String label,
                String packageName
        ) {
            this.label = label;
            this.packageName = packageName;
        }
    }
}
