package com.jantomassi.newcccradioapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    String message = "Null";
    private MediaService mediaService = new MediaService();
    private MainActivity mainActivity = new MainActivity();


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra("toastMessage") != null) {
            message = intent.getStringExtra("toastMessage");
        }
        assert message != null;
        if (message.equals("Play")) {
            if (MediaService.mediaPlayer.isPlaying()) {
                //Media player control action
                mediaService.mediaPause();
                //MediaService.requestAudioFocus();

                //Notification media player control
                mediaService.notificationMediaPlayerCtl(context);
            } else {
                //Media player control action
                mediaService.mediaPlay();
                MediaService.requestAudioFocus();

                //Notification media player control
                mediaService.notificationMediaPlayerCtl(context);
            }
        }
    }
}

