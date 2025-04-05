package com.example.oop_project.hunter;

import android.content.Context;
import android.os.Build;

import com.example.oop_project.hunter.BountyHunter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
// ... other imports
public class BountyHunterLoader {
    public static List<BountyHunter> loadBountyHunters(String path) {
        try {
            String json = null; // Assuming the file is in the project root
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                json = new String(Files.readAllBytes(Paths.get(path)));
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<BountyHunter>>(){}.getType();
            return gson.fromJson(json, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}