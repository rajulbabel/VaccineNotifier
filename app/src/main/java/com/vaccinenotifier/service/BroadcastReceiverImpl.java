package com.vaccinenotifier.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiverImpl extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startForegroundService(new Intent(context, CheckSlotsService.class));
    }
}
