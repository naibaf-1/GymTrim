package com.naibaf.GymTrim.OtherClasses;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.naibaf.GymTrim.R;

public class AudioServiceForBackgroundProcess extends Service {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private AudioManager.OnAudioFocusChangeListener focusChangeListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotification(); // Foreground-Service
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Listener for the audio focus
        focusChangeListener = focusChange -> {
            if (mediaPlayer == null) return;

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mediaPlayer.pause();
                    break;

                case AudioManager.AUDIOFOCUS_GAIN:
                    mediaPlayer.start();
                    break;
                // Android reduces the audio automatically
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    break;
            }
        };

        int focusResult;

        // Use a modern API for 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .setOnAudioFocusChangeListener(focusChangeListener)
                .build();
            focusResult = audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            // Legacy API
            focusResult = audioManager.requestAudioFocus(
                focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            );
        }

        // Play the preferred sound
        if (focusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            int preferredSound = sharedPreferences.getInt("SoundForReminder", R.raw.message);
            mediaPlayer = MediaPlayer.create(this, preferredSound);

            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());

            mediaPlayer.setOnCompletionListener(mp -> {
                releaseAudioFocus();
                mp.release();
            });

            mediaPlayer.start();
        }
        return START_STICKY;
    }

    private void releaseAudioFocus() {
        if (audioManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest);
        } else if (focusChangeListener != null) {
            audioManager.abandonAudioFocus(focusChangeListener);
        }
    }

    // Cancel everything
    @Override
    public void onDestroy() {
        releaseAudioFocus();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotification() {
        NotificationManager manager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "AudioServiceChannel",
                    "Audio Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            manager.createNotificationChannel(channel);
        }

        String notificationText = getString(R.string.notification_reminder_for_training);
        Notification notification = new NotificationCompat.Builder(this, "AudioServiceChannel")
            .setContentTitle(notificationText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build();

        startForeground(1, notification);
    }
}

// Changes: More modern API, Fixed bug of volume to loud => Test this first!