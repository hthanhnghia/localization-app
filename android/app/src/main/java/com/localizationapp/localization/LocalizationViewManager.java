package com.localizationapp.localization;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

public class LocalizationViewManager extends SimpleViewManager<LocalizationView> {
    public static final String REACT_CLASS = "LocalizationView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public LocalizationView createViewInstance(ThemedReactContext context) {
        return new LocalizationView (context);
    }
}

