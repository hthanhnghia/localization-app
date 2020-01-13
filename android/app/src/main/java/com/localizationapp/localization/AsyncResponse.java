package com.localizationapp.localization;

import java.util.HashMap;

public interface AsyncResponse {
    void processFinish(HashMap<String, HashMap<String, String>> output);
}