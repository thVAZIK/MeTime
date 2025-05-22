package com.example.metime.Tools;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ApiKeyLoader {
    public static String getApiKey(Context context) {
        Properties props = new Properties();
        try (InputStream input = context.getAssets().open("apikeys.properties")) {
            props.load(input);
            String apiKey = props.getProperty("API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                Log.e("ApiKeyLoader","API key not found in apikeys.properties");
                return "";
            }
            return apiKey;
        } catch (Exception e) {
            Log.e("ApiKeyLoader","Error while reading apikeys.properties: " + e.getLocalizedMessage());
            return "";
        }
    }
}
