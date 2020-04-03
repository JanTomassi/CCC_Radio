package com.jantomassi.newcccradioapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.jantomassi.newcccradioapp.ui.radio.RadioFragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.jantomassi.newcccradioapp.App.CHANNEL_1_ID;

public class MediaService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener {
    //Volume
    private static final float NORMAL_MEDIA_VOL = 1.0f;
    private static final float DUCK_MEDIA_VOL = 0.05f;
    //Audio and Media Player
    public static MediaPlayer mediaPlayer;
    public static AudioManager audioManager;
    public static AudioFocusRequest focusRequest;
    //Notification
    public static NotificationManagerCompat notificationManager;
    public static Notification notification;
    static AudioManager.OnAudioFocusChangeListener afChangeListener;
    //Instantiate file and Ambient Variable
    private static Context context;
    private static int audioFocusResult;
    //AudioFocus Request
    private static AudioAttributes playbackAttributes;
    Map<String, String> headers = new HashMap<>();
    Uri uri;
    private WifiManager.WifiLock wifiLock;
    private PowerManager.WakeLock wakeLock;

    public static void requestAudioFocus() {
        audioFocusResult = audioManager.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.setOnErrorListener(this);
        try {
            mediaPlayer.setDataSource(getString(R.string.URL));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(this);

        mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);

        mediaPlayer.prepareAsync();
        return flags;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        notificationManager = NotificationManagerCompat.from(this);

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = Objects.requireNonNull(powerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "CCCRadio::AudioStreaming");
        wakeLock.acquire();

        wifiLock = ((WifiManager) Objects.requireNonNull(getApplicationContext().getSystemService(Context.WIFI_SERVICE)))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");

        wifiLock.acquire();

        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        playbackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        afChangeListener =
                new AudioManager.OnAudioFocusChangeListener() {
                    public final void onAudioFocusChange(int focusChange) {
                        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                            // Pause playback immediately
                            mediaPause();
                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                            // Pause playback
                            mediaPause();
                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                            // Lower the volume, keep playing
                            mediaDuck();
                        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                            // Your app has been granted audio focus again
                            // Raise volume to normal, restart playback if necessary
                            mediaPlay();
                        }
                    }
                };
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        RadioFragment.audioStreamCtlBtnImg.setValue(R.drawable.pauseButton);
        requestAudioFocus();
        if (audioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) mp.start();
        context = this;
        notificationMediaPlayerCtl(context);
        Log.i("MediaPlayer", "MediaPlayer is ready");
        startForeground(1, notification);
    }


    public void notificationMediaPlayerCtl(Context context) {

        Intent playIntent = new Intent(context, NotificationReceiver.class);
        playIntent.putExtra("toastMessage", "Play");
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(R.drawable.ic_radio_black_24dp)
                .setContentTitle("Chris Cappell College Radio")
                .setColor(Color.rgb(255, 180, 0))
                .addAction(RadioFragment.audioStreamCtlBtnImg.getValue(), "PlayOrPause", playPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0))
                .build();

        notificationManager.notify(1, notification);
    }

    public void mediaPause() {
        RadioFragment.audioStreamCtlBtnImg.setValue(R.drawable.playButton);
        mediaPlayer.pause();
        notificationMediaPlayerCtl(context);
        Log.d("MediaPlaying", String.format("Is Playing %s", mediaPlayer.isPlaying()));
    }

    public void mediaPlay() {
        RadioFragment.audioStreamCtlBtnImg.setValue(R.drawable.pauseButton);
        requestAudioFocus();
        if (!mediaPlayer.isPlaying() &&
                audioFocusResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) mediaPlayer.start();
        Log.d("MediaPlaying", String.format("Is Playing %s", mediaPlayer.isPlaying()));
        mediaPlayer.setVolume(NORMAL_MEDIA_VOL, NORMAL_MEDIA_VOL);
        notificationMediaPlayerCtl(context);
    }

    private void mediaDuck() {
        RadioFragment.audioStreamCtlBtnImg.setValue(R.drawable.pauseButton);
        if (!mediaPlayer.isPlaying()) mediaPlayer.start();
        Log.d("MediaPlaying", String.format("Dock %s", mediaPlayer.isPlaying()));
        mediaPlayer.setVolume(DUCK_MEDIA_VOL, DUCK_MEDIA_VOL);
        notificationMediaPlayerCtl(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioManager.abandonAudioFocus(afChangeListener);
        mediaPlayer.release();
        mediaPlayer = null;
        notificationManager.cancelAll();
        wifiLock.release();
        wakeLock.release();
        stopForeground(true);
        stopSelf(1);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        mp.setOnErrorListener(this);
        try {
            mp.setDataSource(getString(R.string.URL));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mp.setOnPreparedListener(this);

        mp.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);

        mp.prepareAsync();
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
