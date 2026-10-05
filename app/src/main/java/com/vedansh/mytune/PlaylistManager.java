package com.vedansh.mytune;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlaylistManager {

    private static final String PREF_NAME = "MyTunePlaylistsPref";
    private static final String KEY_PLAYLISTS = "saved_playlists_json";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static Map<String, ArrayList<File>> getAllPlaylists(Context context) {
        Map<String, ArrayList<File>> playlists = new HashMap<>();
        String jsonStr = getPrefs(context).getString(KEY_PLAYLISTS, "{}");
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String name = keys.next();
                JSONArray arr = jsonObject.getJSONArray(name);
                ArrayList<File> fileList = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    String path = arr.getString(i);
                    File file = new File(path);
                    if (file.exists()) {
                        fileList.add(file);
                    }
                }
                playlists.put(name, fileList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public static ArrayList<File> getPlaylistSongs(Context context, String playlistName) {
        Map<String, ArrayList<File>> all = getAllPlaylists(context);
        if (all.containsKey(playlistName)) {
            return all.get(playlistName);
        }
        return new ArrayList<>();
    }

    public static void savePlaylist(Context context, String playlistName, ArrayList<File> files) {
        Map<String, ArrayList<File>> all = getAllPlaylists(context);
        all.put(playlistName, files);
        persistAll(context, all);
    }

    public static void deletePlaylist(Context context, String playlistName) {
        Map<String, ArrayList<File>> all = getAllPlaylists(context);
        all.remove(playlistName);
        persistAll(context, all);
    }

    private static void persistAll(Context context, Map<String, ArrayList<File>> playlists) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, ArrayList<File>> entry : playlists.entrySet()) {
                JSONArray arr = new JSONArray();
                for (File f : entry.getValue()) {
                    arr.put(f.getAbsolutePath());
                }
                jsonObject.put(entry.getKey(), arr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getPrefs(context).edit().putString(KEY_PLAYLISTS, jsonObject.toString()).apply();
    }
}
