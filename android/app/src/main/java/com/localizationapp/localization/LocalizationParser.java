package com.localizationapp.localization;

import android.os.AsyncTask;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

public class LocalizationParser extends AsyncTask<String, Void, HashMap<String, HashMap<String, String>>> {
    public static final String LOCALIZATION_URL = "https://raw.githubusercontent.com/hthanhnghia/localization-file/master/Localization.csv";
    public static HashMap<String, HashMap<String, String>> localizationData;
    public static HashMap<String, String> languageCodes = new HashMap<String, String>() {{
        put("eng", "English");
        put("zho", "Chinese");
        put("hin", "Hindu");
    }};
    public AsyncResponse delegate = null;

    public LocalizationParser() {
        localizationData = new HashMap<String, HashMap<String, String>>() {{ }};
    }

    @Override
    protected HashMap<String, HashMap<String, String>> doInBackground(String... strings) {
        try {
            URL url = new URL(LOCALIZATION_URL);
            InputStream in = url.openStream();
            InputStreamReader inr = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(inr);
            CSVReader reader = new CSVReader(br);

            String[] nextLine;

            // Read the list of localized languages in the header (first line)
            String[] header = reader.readNext();
            String[] languages = new String[header.length];

            for (int i=1; i<header.length; i++) {
                languages[i] = header[i];
                localizationData.put(languages[i], new HashMap<>());
            }

            while ((nextLine = reader.readNext()) != null) {
                String stringID = nextLine[0];

                for(int i=1; i<languages.length; i++) {
                    HashMap<String, String> localizedString = localizationData.get(languages[i]);
                    localizedString.put(stringID, nextLine[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localizationData;
    }

    @Override
    protected void onPostExecute(HashMap<String, HashMap<String, String>> result) {
        delegate.processFinish(result);
    }
}
