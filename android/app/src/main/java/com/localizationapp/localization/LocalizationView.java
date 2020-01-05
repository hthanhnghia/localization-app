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

import com.facebook.react.bridge.ReactContext;
import com.localizationapp.R;

import java.util.Locale;

public class LocalizationView extends LinearLayout {
    private Context context;
    private Activity activity;


    public LocalizationView (Context context) {
        super(context);
        this.context = context;
        this.activity = ((ReactContext) getContext()).getCurrentActivity();
        init();
    }

    public void init() {
        inflate(context, R.layout.localization, this);

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
}
