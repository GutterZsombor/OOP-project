package com.example.oop_project.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.oop_project.hunter.BountyHunter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {
    private static final String TAG = "JsonHelper";

    /*public static List<BountyHunter> loadBountyHunters(Context context, String fileName) {
        List<BountyHunter> bountyHunters = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            InputStreamReader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<BountyHunter>>() {
            }.getType();
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
    }*/

    public static List<BountyHunter> loadBountyHunters(Context context, String fileName) {
        List<BountyHunter> bountyHunters = new ArrayList<>();
        try {
            File file = new File(context.getFilesDir(), fileName);
            FileReader reader = new FileReader(file);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<BountyHunter>>() {}.getType();
            bountyHunters = gson.fromJson(reader, listType);

            reader.close();

            if (bountyHunters == null) {
                Log.w(TAG, "Parsed JSON resulted in a null list. Check JSON format.");
                return new ArrayList<>();
            }

        } catch (IOException e) {
            Log.e(TAG, "Error loading or parsing JSON from " + fileName + ": " + e.getMessage(), e);
            return new ArrayList<>();
        }
        return bountyHunters;
    }



    public static boolean saveBountyHunters(Context context, List<BountyHunter> bountyHunters, String fileName) {
        File file = new File(context.getFilesDir(), fileName); // Get the file object
        String absolutePath = file.getAbsolutePath();  // Get the absolute path

        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(bountyHunters);

            Log.d("JsonHelper", "Attempting to save to: " + absolutePath); // Log before writing
            Log.d("JsonHelper", "JSON data: " + jsonString); // Log the JSON being written

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonString);
            fileWriter.close();

            Log.d("JsonHelper", "Successfully saved data to: " + absolutePath);
            return true;

        } catch (IOException e) {
            Log.e("JsonHelper", "Error saving to " + absolutePath + ": " + e.getMessage(), e); // Use absolutePath
            return false;
        }
    }

    public static void copyJsonIfNotExists(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (!file.exists()) {
            try (InputStream inputStream = context.getAssets().open(fileName);
                 FileOutputStream outputStream = new FileOutputStream(file)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                Log.d(TAG, "Copied " + fileName + " to internal storage.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy " + fileName + ": " + e.getMessage(), e);
            }
        }
    }
    public static void saveUpdatedHunter(Context context, BountyHunter updatedHunter, String fileName) {
        List<BountyHunter> allHunters = JsonHelper.loadBountyHunters(context, fileName);

        for (int i = 0; i < allHunters.size(); i++) {
            if (allHunters.get(i).getName().equals(updatedHunter.getName())) {
                allHunters.set(i, updatedHunter);
                break;
            }
        }

        boolean saved = JsonHelper.saveBountyHunters(context, allHunters, fileName);
        if (!saved) {
            Log.d(TAG, "Failed to save updated hunter.");
        }
    }

    public static void DONOTUSEcopyJson(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        if (file.exists()) {
            try (InputStream inputStream = context.getAssets().open(fileName);
                 FileOutputStream outputStream = new FileOutputStream(file)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                Log.d(TAG, "Copied " + fileName + " to internal storage.");
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy " + fileName + ": " + e.getMessage(), e);
            }
        }
    }

}


