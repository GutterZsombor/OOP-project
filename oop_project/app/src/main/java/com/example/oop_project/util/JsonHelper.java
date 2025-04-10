package com.example.oop_project.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.oop_project.hunter.BountyHunter;
import com.example.oop_project.hunter.Statistic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {

    // Both Gson and JSONObject is used for parsing JSON.
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
    public static JSONObject loadJson(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            return new JSONObject(new String(data));
        } catch (Exception e) {
            Log.e(TAG, "Error loading JSON: " + e.getMessage());
            return new JSONObject();
        }
    }

    public static void saveJson(Context context, JSONObject jsonObject, String fileName) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(jsonObject.toString());
            writer.close();
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "Error saving JSON: " + e.getMessage());
        }
    }


    public static int[] loadGlobalStats(Context context, String fileName) {
        JSONObject root = loadJson(context, fileName);
        JSONObject globalStats = root.optJSONObject("globalStats");
        if (globalStats == null) return new int[]{0, 0, 0, 0};
        return new int[]{
                globalStats.optInt("totalHired", 0),
                globalStats.optInt("localBattles", 0),
                globalStats.optInt("onlineBattles", 0),
                globalStats.optInt("trainingSessions", 0)
        };
    }


    public static void updateGlobalStats(Context context, JSONObject newStats, String fileName) {
        JSONObject root = loadJson(context, fileName);
        try {
            root.put("globalStats", newStats);
            saveJson(context, root, fileName);
        } catch (Exception e) {
            Log.e(TAG, "Error updating global stats: " + e.getMessage());
        }
    }


    public static Statistic loadHunterStatistic(Context context, String name, String fileName) {
        JSONObject root = loadJson(context, fileName);
        JSONArray hunters = root.optJSONArray("bountyHunterStats");
        if (hunters != null) {
            for (int i = 0; i < hunters.length(); i++) {
                JSONObject obj = hunters.optJSONObject(i);
                if (obj != null && name.equals(obj.optString("name"))) {
                    Statistic stat = new Statistic();

                    int sessions = obj.optInt("numberOfTrainingSessions", 0);
                    for (int s = 0; s < sessions; s++) {
                        stat.addNumberOfTrainingSessions();
                    }

                    JSONArray wins = obj.optJSONArray("defeated");
                    if (wins != null) {
                        for (int j = 0; j < wins.length(); j++) {
                            stat.addWin(wins.optString(j));
                        }
                    }
                    JSONArray losts = obj.optJSONArray("defeatedBy");
                    if (losts != null) {
                        for (int j = 0; j < losts.length(); j++) {
                            stat.addLost(losts.optString(j));
                        }
                    }
                    return stat;
                }
            }
        }
        return null;
    }


    public static void updateHunterStatistic(Context context, String name, Statistic stat, String fileName) {
        JSONObject root = loadJson(context, fileName);
        JSONArray hunters = root.optJSONArray("bountyHunterStats");
        if (hunters == null) hunters = new JSONArray();

        try {
            // Remove existing entry if exists
            for (int i = 0; i < hunters.length(); i++) {
                JSONObject obj = hunters.getJSONObject(i);
                if (name.equals(obj.getString("name"))) {
                    hunters.remove(i);
                    break;
                }
            }


            JSONObject hunterObj = new JSONObject();
            hunterObj.put("name", name);
            hunterObj.put("numberOfTrainingSessions", stat.getNumberOfTrainingSessions());

            JSONArray winArray = new JSONArray();
            for (String nameOfLoser : stat.getWins()) {
                winArray.put(nameOfLoser);
            }
            hunterObj.put("defeated", winArray);

            JSONArray lostArray = new JSONArray();
            for (String nameOfWinner : stat.getLost()) {
                lostArray.put(nameOfWinner);
            }
            hunterObj.put("defeatedBy", lostArray);

            hunters.put(hunterObj);
            root.put("bountyHunterStats", hunters);
            saveJson(context, root, fileName);

        } catch (Exception e) {
            Log.e(TAG, "Error updating hunter stats: " + e.getMessage());
        }
    }

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

    public static BountyHunter loadOriginalHunter(Context context, String name) {
        List<BountyHunter> allHunters = JsonHelper.loadBountyHunters(context, "bounty_hunters.json");
        for (BountyHunter hunter : allHunters) {
            if (hunter.getName().equals(name)) {
                return hunter;
            }
        }
        return null; // Return null if not found
    }


}


