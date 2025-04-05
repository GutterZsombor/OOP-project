package com.example.oop_project.util;

import android.content.Context;
import android.util.Log;

import com.example.oop_project.hunter.BountyHunter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {
    private static final String TAG = "JsonHelper";

    public static List<BountyHunter> loadBountyHunters(Context context, String fileName) {
        List<BountyHunter> bountyHunters = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            InputStreamReader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<BountyHunter>>() {}.getType();
            bountyHunters = gson.fromJson(reader, listType);

            reader.close();
            inputStream.close();

            if (bountyHunters == null) {
                Log.w(TAG, "Parsed JSON resulted in a null list. Check JSON format.");
                return new ArrayList<>(); // Return an empty list instead of null
            }

        } catch (IOException e) {
            Log.e(TAG, "Error loading or parsing JSON from " + fileName + ": " + e.getMessage(), e);
            // Consider showing a user-friendly error message here in a real app
            return new ArrayList<>(); // Return an empty list to avoid NullPointerExceptions
        }
        return bountyHunters;
    }
}
