package com.localizationapp.localization;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.react.bridge.ReactContext;
import com.localizationapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LocalizationView extends LinearLayout implements AsyncResponse{
    private Context context;
    private Activity activity;
    private LocalizationParser parser;

    public LocalizationView (Context context) {
        super(context);
        this.context = context;
        this.activity = ((ReactContext) context).getCurrentActivity();
        this.parser = new LocalizationParser();
        this.parser.delegate = this;
        init();
    }

    public void init() {
        inflate(context, R.layout.localization, this);
        this.parser.execute();

        ViewGroup parentView = findViewById(R.id.localization_buttons);

        for(int i=0; i < parentView.getChildCount(); i++) {
            View childView = parentView.getChildAt(i);
            if (childView instanceof Button) {
                Button localizationButton = (Button) childView;
                localizationButton.setOnClickListener(view -> {
                    String updatedLocale = (String) view.getTag();

                    // Create a new Locale object
                    Locale locale = new Locale(updatedLocale);
                    Locale.setDefault(locale);

                    // Create a new configuration object
                    Resources res = context.getResources();
                    Configuration config = res.getConfiguration();

                    if (Build.VERSION.SDK_INT >= 24) {
                        config.setLocale(locale);
                        res.updateConfiguration(config, res.getDisplayMetrics());
                    }
                    else if (Build.VERSION.SDK_INT >= 17) {
                        config.setLocale(locale);
                        context.createConfigurationContext(config);
                    } else {
                        config.locale = locale;
                        res.updateConfiguration(config, res.getDisplayMetrics());
                    }
                    activity.recreate();
                });
            }
        }
    }

    private String getStringResourceByName(String name) {
        String packageName = context.getPackageName();
        int identifier = getResources().getIdentifier (name,"string",packageName);
        String s = "";
        if (identifier != 0){
            s = getResources().getString(identifier);
        }
        return s;
    }

    @Override
    public void processFinish(HashMap<String, HashMap<String, String>> output) {
        String locale;
        if (Build.VERSION.SDK_INT >= 24){
            locale = context.getResources().getConfiguration().getLocales().get(0).getISO3Language();
        } else {
            locale = context.getResources().getConfiguration().locale.getISO3Language();
        }

        HashMap<String, String> localizedStrings = output.get(LocalizationParser.languageCodes.get(locale));

        if (localizedStrings != null) {
            HashMap<String, String> replacedStrings = new HashMap<>();

            for (HashMap.Entry<String, String> entry : localizedStrings.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                String resourcedString = getStringResourceByName(key);

                if (resourcedString != "") {
                    replacedStrings.put(resourcedString, value);
                }
            }

            // Breadth-first search to get all the TextViews to apply the localization
            List<View> visited = new ArrayList<>();
            List<View> unvisited = new ArrayList<>();
            unvisited.add(this);

            while (!unvisited.isEmpty()) {
                View child = unvisited.remove(0);
                visited.add(child);
                if (child instanceof TextView) {
                    TextView textView = (TextView) child;
                    String currentText = textView.getText().toString();
                    if (replacedStrings.containsKey(currentText)) {
                        textView.setText(replacedStrings.get(currentText));
                    }
                } else if (child instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) child;
                    final int childCount = group.getChildCount();
                    for (int i = 0; i < childCount; i++) unvisited.add(group.getChildAt(i));
                }
            }
        }
    }

}
