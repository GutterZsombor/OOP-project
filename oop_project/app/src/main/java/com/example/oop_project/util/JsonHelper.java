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


    private static final String TAG = "JsonHelper";

    //newer functions
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
                globalStats.optInt("totalBountyHuntersHired", 0),
                globalStats.optInt("totalLocalBattles", 0),
                globalStats.optInt("totalOnlineBattles", 0),
                globalStats.optInt("totalTrainingSessions", 0)
        };
    }


    public static void updateGlobalStats(Context context, int[] globalStats, String fileName) {
        JSONObject root = loadJson(context, fileName);
        if (root == null) {
            Log.e(TAG, "Failed to load JSON file");
            return;
        }

        try {
            // Get or create the globalStats object
            JSONObject stats = root.optJSONObject("globalStats");
            if (stats == null) {
                stats = new JSONObject();
            }



            stats.put("totalBountyHuntersHired", globalStats[0]);


            stats.put("totalLocalBattles", globalStats[1]);


            stats.put("totalOnlineBattles", globalStats[2]);


            stats.put("totalTrainingSessions", globalStats[3]);



            root.put("globalStats", stats);

            // Save the updated JSON
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


    //early functions
    public static List<BountyHunter> loadBountyHunters(Context context, String fileName) {
        List<BountyHunter> bountyHunters = new ArrayList<>();
        try {

            FileInputStream fis = context.openFileInput(fileName);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();


            JSONArray huntersArray = new JSONArray(new String(data));

            for (int i = 0; i < huntersArray.length(); i++) {
                JSONObject hunterJson = huntersArray.getJSONObject(i);

                BountyHunter hunter = new BountyHunter(
                        hunterJson.optString("name", ""),
                        hunterJson.optString("imagePath", ""),
                        hunterJson.optBoolean("preferedAttack", false),
                        hunterJson.optInt("meleAttack", 0),
                        hunterJson.optInt("meleDefense", 0),
                        hunterJson.optInt("rangedAttack", 0),
                        hunterJson.optInt("rangedDefense", 0),
                        hunterJson.optInt("maxHealth", 100),
                        hunterJson.optInt("experience", 0)
                );

                bountyHunters.add(hunter);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading bounty hunters: " + e.getMessage());
        }
        return bountyHunters;
    }




    public static boolean saveBountyHunters(Context context, List<BountyHunter> bountyHunters, String fileName) {
        try {
            JSONArray huntersArray = new JSONArray();

            for (BountyHunter hunter : bountyHunters) {
                JSONObject hunterJson = new JSONObject();
                hunterJson.put("name", hunter.getName());
                hunterJson.put("imagePath", hunter.getImagePath());
                hunterJson.put("preferedAttack", hunter.isPreferedAttack());
                hunterJson.put("meleAttack", hunter.getMeleAttack());
                hunterJson.put("meleDefense", hunter.getMeleDefense());
                hunterJson.put("rangedAttack", hunter.getRangedAttack());
                hunterJson.put("rangedDefense", hunter.getRangedDefense());
                hunterJson.put("maxHealth", hunter.getMaxHealth());
                hunterJson.put("experience", hunter.getExperience());

                huntersArray.put(hunterJson);
            }

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(huntersArray.toString());
            writer.close();
            fos.close();

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving bounty hunters: " + e.getMessage());
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
        return null;
    }


}


