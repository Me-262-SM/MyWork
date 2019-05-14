package com.edu.sicnu.cs.zzy.mywork;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicReceiver extends BroadcastReceiver {
    PlayerActivity activity;

    public MusicReceiver() {
    }

    public MusicReceiver(PlayerActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String musicName = intent.getStringExtra("name");
        activity.music_name.setText(musicName);
    }
}
