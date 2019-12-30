package com.localizationapp.localization;

import android.content.Context;
import android.widget.LinearLayout;

import com.localizationapp.R;

public class LocalizationView extends LinearLayout {
    private Context context;
    public LocalizationView (Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void init() {
        inflate(context, R.layout.localization, this);
    }
}
