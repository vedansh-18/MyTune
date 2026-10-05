package com.vedansh.mytune;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    public static final String CHANNEL_ID = "MyTune_Playback_Channel";
    public static final int NOTIFICATION_ID = 101;

    public static final String ACTION_TOGGLE_PLAY = "com.vedansh.mytune.ACTION_TOGGLE_PLAY";
    public static final String ACTION_NEXT = "com.vedansh.mytune.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "com.vedansh.mytune.ACTION_PREVIOUS";
    public static final String ACTION_STOP = "com.vedansh.mytune.ACTION_STOP";

    private final IBinder musicBind = new MusicBinder();
    private MediaPlayer mediaPlayer;

    private ArrayList<File> songList;
    private int songPosition = 0;
    private boolean isPrepared = false;
    private boolean isAutoplayEnabled = true;

    public interface OnTrackChangeListener {
        void onTrackChanged(int position, String songName);
        void onPlaybackStateChanged(boolean isPlaying);
    }

    private OnTrackChangeListener trackChangeListener;

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
        createNotificationChannel();
    }

    private void initMediaPlayer() {
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public void setSongList(ArrayList<File> songs) {
        this.songList = songs;
    }

    public ArrayList<File> getSongList() {
        return songList;
    }

    public void setTrackChangeListener(OnTrackChangeListener listener) {
        this.trackChangeListener = listener;
    }

    public boolean isAutoplayEnabled() {
        return isAutoplayEnabled;
    }

    public void setAutoplayEnabled(boolean enabled) {
        this.isAutoplayEnabled = enabled;
    }

    public void playSong(int position) {
        if (songList == null || position < 0 || position >= songList.size()) return;

        songPosition = position;
        isPrepared = false;
        mediaPlayer.reset();

        File fileToPlay = songList.get(songPosition);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepared = true;
        mp.start();
        updateNotification();
        if (trackChangeListener != null) {
            trackChangeListener.onTrackChanged(songPosition, getSongTitle());
            trackChangeListener.onPlaybackStateChanged(true);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isAutoplayEnabled) {
            playNext();
        } else {
            if (isPrepared) {
                mediaPlayer.pause();
                updateNotification();
                if (trackChangeListener != null) {
                    trackChangeListener.onPlaybackStateChanged(false);
                }
            }
        }
    }

    public void playNext() {
        if (songList == null || songList.isEmpty()) return;
        songPosition = (songPosition + 1) % songList.size();
        playSong(songPosition);
    }

    public void playPrevious() {
        if (songList == null || songList.isEmpty()) return;
        songPosition = (songPosition - 1 + songList.size()) % songList.size();
        playSong(songPosition);
    }

    public void togglePlayPause() {
        if (!isPrepared) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
        updateNotification();
        if (trackChangeListener != null) {
            trackChangeListener.onPlaybackStateChanged(mediaPlayer.isPlaying());
        }
    }

    public void stopPlayback() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
        }
        isPrepared = false;
        stopForeground(true);
        if (trackChangeListener != null) {
            trackChangeListener.onPlaybackStateChanged(false);
            trackChangeListener.onTrackChanged(-1, "");
        }
    }

    public void pause() {
        if (isPrepared && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updateNotification();
            if (trackChangeListener != null) {
                trackChangeListener.onPlaybackStateChanged(false);
            }
        }
    }

    public void resume() {
        if (isPrepared && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            updateNotification();
            if (trackChangeListener != null) {
                trackChangeListener.onPlaybackStateChanged(true);
            }
        }
    }

    public void seekTo(int positionMs) {
        if (isPrepared) {
            mediaPlayer.seekTo(positionMs);
        }
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public boolean isPlaying() {
        return isPrepared && mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        if (isPrepared) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (isPrepared) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public int getSongPosition() {
        return songPosition;
    }

    public String getSongTitle() {
        if (isPrepared && songList != null && songPosition >= 0 && songPosition < songList.size()) {
            return songList.get(songPosition).getName().replace(".mp3", "").replace(".m4a", "").replace(".wav", "").replace(".flac", "").replace(".aac", "").replace(".ogg", "");
        }
        return "";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_TOGGLE_PLAY:
                    togglePlayPause();
                    break;
                case ACTION_NEXT:
                    playNext();
                    break;
                case ACTION_PREVIOUS:
                    playPrevious();
                    break;
                case ACTION_STOP:
                    stopPlayback();
                    break;
            }
        }
        return START_STICKY;
    }

    private void updateNotification() {
        if (!isPrepared) return;
        String title = getSongTitle();

        Intent openAppIntent = new Intent(this, PlaySong.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        PendingIntent prevIntent = PendingIntent.getService(
                this, 1, new Intent(this, MusicService.class).setAction(ACTION_PREVIOUS),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        PendingIntent toggleIntent = PendingIntent.getService(
                this, 2, new Intent(this, MusicService.class).setAction(ACTION_TOGGLE_PLAY),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        PendingIntent nextIntent = PendingIntent.getService(
                this, 3, new Intent(this, MusicService.class).setAction(ACTION_NEXT),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        int playPauseIcon = isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play;
        String playPauseTitle = isPlaying() ? "Pause" : "Play";

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_audiotrack_24)
                .setContentTitle(title)
                .setContentText("MyTune Music Player")
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(R.drawable.ic_previous, "Previous", prevIntent)
                .addAction(playPauseIcon, playPauseTitle, toggleIntent)
                .addAction(R.drawable.ic_next, "Next", nextIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Background music player controls");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopPlayback();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        stopPlayback();
        super.onDestroy();
    }
}
