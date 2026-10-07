package com.vedansh.mytune;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MusicService.OnTrackChangeListener {

    private static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
            ".mp3", ".m4a", ".wav", ".flac", ".aac", ".ogg", ".opus", ".wma"
    );

    private static final List<String> CALL_RECORDING_KEYWORDS = Arrays.asList(
            "call", "recording", "call_rec", "soundrecorder", "voice_rec", "callrecord", "voicerecorder"
    );

    private ListView listView;
    private Button btnTabAllSongs, btnTabPlaylists;
    private ImageButton btnCreatePlaylist, btnMiniPlay, btnMiniClose, btnBackFromPlaylist;
    private ImageView imgMiniArtwork;
    private LinearLayout miniPlayerBar, tabLayout;
    private TextView txtHeaderTitle, txtMiniSongName;

    private ArrayList<File> allSongs = new ArrayList<>();
    private ActivityResultLauncher<String[]> permissionLauncher;

    private boolean isPlaylistsTab = false;
    private String selectedPlaylistName = null;

    private MusicService musicService;
    private boolean isBound = false;
    private GestureDetector gestureDetector;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;
            musicService.setTrackChangeListener(MainActivity.this);
            updateMiniPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            musicService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        btnTabAllSongs = findViewById(R.id.btnTabAllSongs);
        btnTabPlaylists = findViewById(R.id.btnTabPlaylists);
        btnCreatePlaylist = findViewById(R.id.btnCreatePlaylist);
        btnBackFromPlaylist = findViewById(R.id.btnBackFromPlaylist);
        tabLayout = findViewById(R.id.tabLayout);
        txtHeaderTitle = findViewById(R.id.txtHeaderTitle);
        miniPlayerBar = findViewById(R.id.miniPlayerBar);
        imgMiniArtwork = findViewById(R.id.imgMiniArtwork);
        txtMiniSongName = findViewById(R.id.txtMiniSongName);
        btnMiniPlay = findViewById(R.id.btnMiniPlay);
        btnMiniClose = findViewById(R.id.btnMiniClose);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (selectedPlaylistName != null) {
                    closePlaylistView();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean audioGranted = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Boolean mediaAudio = result.get(Manifest.permission.READ_MEDIA_AUDIO);
                        audioGranted = mediaAudio != null && mediaAudio;
                    } else {
                        Boolean readStorage = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
                        audioGranted = readStorage != null && readStorage;
                    }

                    if (audioGranted) {
                        loadSongs();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission required to access audio files", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestAudioPermissions();

        btnTabAllSongs.setOnClickListener(v -> switchTab(false));
        btnTabPlaylists.setOnClickListener(v -> switchTab(true));
        btnCreatePlaylist.setOnClickListener(v -> showCreatePlaylistDialog());
        btnBackFromPlaylist.setOnClickListener(v -> closePlaylistView());

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            if (isPlaylistsTab && selectedPlaylistName == null) {
                                switchTab(false);
                                return true;
                            }
                        } else {
                            if (!isPlaylistsTab && selectedPlaylistName == null) {
                                switchTab(true);
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });

        listView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });

        miniPlayerBar.setOnClickListener(v -> {
            if (isBound && musicService != null && musicService.isPrepared() && musicService.getSongList() != null) {
                Intent intent = new Intent(MainActivity.this, PlaySong.class);
                startActivity(intent);
            }
        });

        btnMiniPlay.setOnClickListener(v -> {
            if (isBound && musicService != null) {
                musicService.togglePlayPause();
                updateMiniPlayer();
            }
        });

        btnMiniClose.setOnClickListener(v -> {
            if (isBound && musicService != null) {
                musicService.stopPlayback();
                updateMiniPlayer();
            }
        });

        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBound && musicService != null) {
            musicService.setTrackChangeListener(this);
            updateMiniPlayer();
        }
    }

    private void requestAudioPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
            });
        } else {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
        }
    }

    private void loadSongs() {
        allSongs = fetchSongsFromMediaStore();

        if (allSongs == null || allSongs.isEmpty()) {
            allSongs = fetchSongsFromStorage(Environment.getExternalStorageDirectory());
        }

        if (allSongs != null) {
            Collections.sort(allSongs, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        }

        renderListView();
    }

    private void switchTab(boolean showPlaylists) {
        this.isPlaylistsTab = showPlaylists;
        this.selectedPlaylistName = null;
        btnBackFromPlaylist.setVisibility(View.GONE);
        btnCreatePlaylist.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        txtHeaderTitle.setText("MyTune");

        if (showPlaylists) {
            btnTabAllSongs.setBackgroundColor(Color.parseColor("#1A1953"));
            btnTabAllSongs.setTextColor(Color.parseColor("#A5A5D0"));
            btnTabPlaylists.setBackgroundColor(Color.parseColor("#2F2FE4"));
            btnTabPlaylists.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            btnTabAllSongs.setBackgroundColor(Color.parseColor("#2F2FE4"));
            btnTabAllSongs.setTextColor(Color.parseColor("#FFFFFF"));
            btnTabPlaylists.setBackgroundColor(Color.parseColor("#1A1953"));
            btnTabPlaylists.setTextColor(Color.parseColor("#A5A5D0"));
        }
        renderListView();
    }

    private void openPlaylistView(String playlistName) {
        this.selectedPlaylistName = playlistName;
        btnBackFromPlaylist.setVisibility(View.VISIBLE);
        btnCreatePlaylist.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        txtHeaderTitle.setText(playlistName);
        renderListView();
    }

    private void closePlaylistView() {
        this.selectedPlaylistName = null;
        btnBackFromPlaylist.setVisibility(View.GONE);
        btnCreatePlaylist.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        txtHeaderTitle.setText("MyTune");
        renderListView();
    }

    private void renderListView() {
        if (selectedPlaylistName != null) {
            ArrayList<File> playlistSongs = PlaylistManager.getPlaylistSongs(this, selectedPlaylistName);
            if (playlistSongs.isEmpty()) {
                String[] items = new String[]{"Playlist is empty. Long press any song in 'All Songs' to add it here."};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item, items);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(null);
                listView.setOnItemLongClickListener(null);
                return;
            }

            String[] items = new String[playlistSongs.size()];
            for (int i = 0; i < playlistSongs.size(); i++) {
                items[i] = formatSongName(playlistSongs.get(i).getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item, items);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((adapterView, view, position, id) -> {
                Intent intent = new Intent(MainActivity.this, PlaySong.class);
                intent.putExtra("songList", playlistSongs);
                intent.putExtra("position", position);
                startActivity(intent);
            });

            listView.setOnItemLongClickListener((parent, view, position, id) -> {
                File targetSong = playlistSongs.get(position);
                String songTitle = formatSongName(targetSong.getName());

                List<String> options = new ArrayList<>();
                if (position > 0) {
                    options.add("⬆️ Move Up");
                }
                if (position < playlistSongs.size() - 1) {
                    options.add("⬇️ Move Down");
                }
                options.add("❌ Remove from Playlist");

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(songTitle)
                        .setItems(options.toArray(new String[0]), (dialog, which) -> {
                            String choice = options.get(which);
                            if (choice.contains("Move Up")) {
                                Collections.swap(playlistSongs, position, position - 1);
                                PlaylistManager.savePlaylist(MainActivity.this, selectedPlaylistName, playlistSongs);
                                renderListView();
                            } else if (choice.contains("Move Down")) {
                                Collections.swap(playlistSongs, position, position + 1);
                                PlaylistManager.savePlaylist(MainActivity.this, selectedPlaylistName, playlistSongs);
                                renderListView();
                            } else if (choice.contains("Remove")) {
                                playlistSongs.remove(position);
                                PlaylistManager.savePlaylist(MainActivity.this, selectedPlaylistName, playlistSongs);
                                renderListView();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });
            return;
        }

        if (!isPlaylistsTab) {
            String[] items = new String[allSongs.size()];
            for (int i = 0; i < allSongs.size(); i++) {
                items[i] = formatSongName(allSongs.get(i).getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item, items);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((adapterView, view, position, id) -> {
                Intent intent = new Intent(MainActivity.this, PlaySong.class);
                intent.putExtra("songList", allSongs);
                intent.putExtra("position", position);
                startActivity(intent);
            });

            listView.setOnItemLongClickListener((parent, view, position, id) -> {
                showAddToPlaylistDialog(allSongs.get(position));
                return true;
            });
        } else {
            Map<String, ArrayList<File>> playlists = PlaylistManager.getAllPlaylists(MainActivity.this);
            List<String> playlistNames = new ArrayList<>(playlists.keySet());
            Collections.sort(playlistNames, String.CASE_INSENSITIVE_ORDER);

            if (playlistNames.isEmpty()) {
                String[] items = new String[]{"No playlists created. Tap '+' at top right to create a playlist!"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item, items);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(null);
                listView.setOnItemLongClickListener(null);
                return;
            }

            String[] items = new String[playlistNames.size()];
            for (int i = 0; i < playlistNames.size(); i++) {
                String pName = playlistNames.get(i);
                int count = playlists.get(pName).size();
                items[i] = pName + " (" + count + " tracks)";
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item, items);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((adapterView, view, position, id) -> {
                String clickedPlaylist = playlistNames.get(position);
                openPlaylistView(clickedPlaylist);
            });

            listView.setOnItemLongClickListener((parent, view, position, id) -> {
                String playlistToDelete = playlistNames.get(position);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Playlist")
                        .setMessage("Are you sure you want to delete '" + playlistToDelete + "'?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            PlaylistManager.deletePlaylist(MainActivity.this, playlistToDelete);
                            renderListView();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });
        }
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Playlist");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Playlist Name");
        builder.setView(input);

        builder.setPositiveButton("Next", (dialog, which) -> {
            String playlistName = input.getText().toString().trim();
            if (!playlistName.isEmpty()) {
                showSelectSongsForPlaylistDialog(playlistName);
            } else {
                Toast.makeText(MainActivity.this, "Playlist name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showSelectSongsForPlaylistDialog(String playlistName) {
        if (allSongs == null || allSongs.isEmpty()) {
            Toast.makeText(this, "No songs available", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] songNames = new String[allSongs.size()];
        boolean[] checkedItems = new boolean[allSongs.size()];
        for (int i = 0; i < allSongs.size(); i++) {
            songNames[i] = formatSongName(allSongs.get(i).getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Songs for '" + playlistName + "'");
        builder.setMultiChoiceItems(songNames, checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked);

        builder.setPositiveButton("Save Playlist", (dialog, which) -> {
            ArrayList<File> selectedFiles = new ArrayList<>();
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    selectedFiles.add(allSongs.get(i));
                }
            }
            PlaylistManager.savePlaylist(MainActivity.this, playlistName, selectedFiles);
            Toast.makeText(MainActivity.this, "Playlist '" + playlistName + "' saved!", Toast.LENGTH_SHORT).show();
            if (isPlaylistsTab) {
                renderListView();
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void showAddToPlaylistDialog(File songFile) {
        Map<String, ArrayList<File>> playlists = PlaylistManager.getAllPlaylists(this);
        if (playlists.isEmpty()) {
            Toast.makeText(this, "No playlists created yet. Create a playlist first!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> playlistNames = new ArrayList<>(playlists.keySet());
        Collections.sort(playlistNames, String.CASE_INSENSITIVE_ORDER);
        String[] items = playlistNames.toArray(new String[0]);

        new AlertDialog.Builder(this)
                .setTitle("Add to Playlist")
                .setItems(items, (dialog, which) -> {
                    String targetPlaylist = playlistNames.get(which);
                    ArrayList<File> currentFiles = playlists.get(targetPlaylist);
                    if (currentFiles != null && !currentFiles.contains(songFile)) {
                        currentFiles.add(songFile);
                        PlaylistManager.savePlaylist(MainActivity.this, targetPlaylist, currentFiles);
                        Toast.makeText(MainActivity.this, "Added to '" + targetPlaylist + "'", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Song already in playlist", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public ArrayList<File> fetchSongsFromMediaStore() {
        ArrayList<File> songList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE NOCASE ASC";

        Cursor cursor = contentResolver.query(songUri, null, null, null, sortOrder);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int nameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                do {
                    if (dataColumn != -1) {
                        String filePath = cursor.getString(dataColumn);
                        String fileName = nameColumn != -1 ? cursor.getString(nameColumn) : "";
                        if (filePath != null && isSupportedAudio(filePath) && !isCallRecording(fileName, filePath)) {
                            File file = new File(filePath);
                            if (file.exists()) {
                                songList.add(file);
                            }
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return songList;
    }

    public ArrayList<File> fetchSongsFromStorage(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] songs = file.listFiles();
        if (songs != null) {
            for (File myFile : songs) {
                if (myFile.isDirectory() && !myFile.isHidden()) {
                    if (!isCallRecording(myFile.getName(), myFile.getAbsolutePath())) {
                        arrayList.addAll(fetchSongsFromStorage(myFile));
                    }
                } else {
                    if (isSupportedAudio(myFile.getName()) && !isCallRecording(myFile.getName(), myFile.getAbsolutePath())) {
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }

    private boolean isSupportedAudio(String fileName) {
        if (fileName == null) return false;
        String lower = fileName.toLowerCase(Locale.getDefault());
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (lower.endsWith(ext) && !lower.startsWith(".")) {
                return true;
            }
        }
        return false;
    }

    private boolean isCallRecording(String name, String path) {
        String combined = ((name != null ? name : "") + " " + (path != null ? path : "")).toLowerCase(Locale.getDefault());
        for (String keyword : CALL_RECORDING_KEYWORDS) {
            if (combined.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String formatSongName(String fileName) {
        String result = fileName;
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (result.toLowerCase().endsWith(ext)) {
                result = result.substring(0, result.length() - ext.length());
                break;
            }
        }
        return result;
    }

    private void updateMiniPlayer() {
        if (isBound && musicService != null && musicService.isPrepared() && !musicService.getSongTitle().isEmpty()) {
            miniPlayerBar.setVisibility(View.VISIBLE);
            txtMiniSongName.setText(musicService.getSongTitle());

            ArrayList<File> songs = musicService.getSongList();
            int pos = musicService.getSongPosition();
            if (songs != null && pos >= 0 && pos < songs.size()) {
                Bitmap artwork = AlbumArtUtils.getAlbumArt(songs.get(pos).getAbsolutePath());
                if (artwork != null) {
                    imgMiniArtwork.setImageBitmap(artwork);
                    imgMiniArtwork.setImageTintList(null);
                } else {
                    imgMiniArtwork.setImageResource(R.drawable.mytune_logo);
                    imgMiniArtwork.setImageTintList(null);
                }
            } else {
                imgMiniArtwork.setImageResource(R.drawable.mytune_logo);
                imgMiniArtwork.setImageTintList(null);
            }

            if (musicService.isPlaying()) {
                btnMiniPlay.setImageResource(R.drawable.ic_pause);
                txtMiniSongName.setSelected(true);
            } else {
                btnMiniPlay.setImageResource(R.drawable.ic_play);
                txtMiniSongName.setSelected(false);
            }
        } else {
            miniPlayerBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTrackChanged(int position, String songName) {
        runOnUiThread(this::updateMiniPlayer);
    }

    @Override
    public void onPlaybackStateChanged(boolean isPlaying) {
        runOnUiThread(this::updateMiniPlayer);
    }

    @Override
    protected void onDestroy() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        super.onDestroy();
    }
}