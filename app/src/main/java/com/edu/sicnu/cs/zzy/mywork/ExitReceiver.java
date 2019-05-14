package com.edu.sicnu.cs.zzy.mywork;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ExitReceiver extends BroadcastReceiver {
    private Activity activity;

    public ExitReceiver(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        activity.finish();
    }
}
