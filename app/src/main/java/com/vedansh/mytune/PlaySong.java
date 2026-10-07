package com.vedansh.mytune;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class PlaySong extends AppCompatActivity implements MusicService.OnTrackChangeListener {

    private TextView txtSongName, txtCurrentTime, txtTotalTime;
    private SeekBar seekBar;
    private ImageButton btnPrevious, btnPlay, btnNext, btnAutoplay;
    private ImageView imageView4;

    private MusicService musicService;
    private boolean isBound = false;

    private ArrayList<File> songList;
    private int position;

    private final Handler updateHandler = new Handler(Looper.getMainLooper());
    private final Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (isBound && musicService != null && musicService.isPlaying()) {
                int currentPos = musicService.getCurrentPosition();
                int duration = musicService.getDuration();

                seekBar.setMax(duration);
                seekBar.setProgress(currentPos);

                txtCurrentTime.setText(formatTime(currentPos));
                txtTotalTime.setText(formatTime(duration));
            }
            updateHandler.postDelayed(this, 500);
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            isBound = true;

            musicService.setTrackChangeListener(PlaySong.this);

            if (songList != null) {
                musicService.setSongList(songList);
                if (getIntent().hasExtra("position")) {
                    int requestedPos = getIntent().getIntExtra("position", 0);
                    getIntent().removeExtra("position");
                    musicService.playSong(requestedPos);
                }
            }

            updateUI();
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
        setContentView(R.layout.activity_play_song);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imageView4 = findViewById(R.id.imageView4);
        txtSongName = findViewById(R.id.txtSongName);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);
        txtTotalTime = findViewById(R.id.txtTotalTime);
        seekBar = findViewById(R.id.seekBar);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnAutoplay = findViewById(R.id.btnAutoplay);

        Intent intent = getIntent();
        if (intent.hasExtra("songList")) {
            songList = (ArrayList<File>) intent.getSerializableExtra("songList");
            position = intent.getIntExtra("position", 0);
        }

        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        btnPlay.setOnClickListener(v -> {
            if (isBound && musicService != null) {
                musicService.togglePlayPause();
                updatePlayPauseButton();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (isBound && musicService != null) {
                musicService.playPrevious();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (isBound && musicService != null) {
                musicService.playNext();
            }
        });

        btnAutoplay.setOnClickListener(v -> {
            if (isBound && musicService != null) {
                boolean newState = !musicService.isAutoplayEnabled();
                musicService.setAutoplayEnabled(newState);
                updateAutoplayButton();
                String msg = newState ? "Autoplay ON: Plays next track automatically" : "Autoplay OFF: Stops after current track";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && isBound && musicService != null) {
                    musicService.seekTo(progress);
                    txtCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        updateHandler.post(updateProgressRunnable);
    }

    private void updateUI() {
        if (isBound && musicService != null) {
            txtSongName.setText(musicService.getSongTitle());
            updatePlayPauseButton();
            updateAutoplayButton();
            updateArtwork();
            int duration = musicService.getDuration();
            int currentPos = musicService.getCurrentPosition();
            seekBar.setMax(duration);
            seekBar.setProgress(currentPos);
            txtCurrentTime.setText(formatTime(currentPos));
            txtTotalTime.setText(formatTime(duration));
        }
    }

    private void updateArtwork() {
        if (isBound && musicService != null) {
            ArrayList<File> songs = musicService.getSongList();
            int pos = musicService.getSongPosition();
            if (songs != null && pos >= 0 && pos < songs.size()) {
                File currentFile = songs.get(pos);
                Bitmap artwork = AlbumArtUtils.getAlbumArt(currentFile.getAbsolutePath());
                if (artwork != null) {
                    imageView4.setImageBitmap(artwork);
                } else {
                    imageView4.setImageResource(R.drawable.mytune_logo);
                }
            } else {
                imageView4.setImageResource(R.drawable.mytune_logo);
            }
        }
    }

    private void updatePlayPauseButton() {
        if (isBound && musicService != null) {
            if (musicService.isPlaying()) {
                btnPlay.setImageResource(R.drawable.ic_pause);
                txtSongName.setSelected(true);
            } else {
                btnPlay.setImageResource(R.drawable.ic_play);
                txtSongName.setSelected(false);
            }
        }
    }

    private void updateAutoplayButton() {
        if (isBound && musicService != null) {
            if (musicService.isAutoplayEnabled()) {
                btnAutoplay.setImageResource(R.drawable.ic_autoplay_on);
            } else {
                btnAutoplay.setImageResource(R.drawable.ic_autoplay_off);
            }
        }
    }

    private String formatTime(int msec) {
        int seconds = (msec / 1000) % 60;
        int minutes = (msec / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    public void onTrackChanged(int position, String songName) {
        runOnUiThread(() -> {
            txtSongName.setText(songName);
            updatePlayPauseButton();
            updateArtwork();
        });
    }

    @Override
    public void onPlaybackStateChanged(boolean isPlaying) {
        runOnUiThread(this::updatePlayPauseButton);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        updateHandler.removeCallbacks(updateProgressRunnable);
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        super.onDestroy();
    }
}