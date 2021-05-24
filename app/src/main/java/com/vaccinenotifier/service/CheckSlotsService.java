package com.vaccinenotifier.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.vaccinenotifier.CentersActivity;
import com.vaccinenotifier.R;
import com.vaccinenotifier.api.CoWinAsyncTask;
import com.vaccinenotifier.bean.AvailableCenter;
import com.vaccinenotifier.bean.GetSlotsResponse;
import com.vaccinenotifier.bean.SlotConstraints;
import com.vaccinenotifier.helper.SlotResponseHelper;

import java.util.List;

public class CheckSlotsService extends IntentService implements CoWinAsyncTask.ResultListener {

    private SlotConstraints slotConstraints;
    public CheckSlotsService() {
        super(CheckSlotsService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String channelId = getString(R.string.backgroundNotifyChannelId);
        NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_NONE);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, channelId).build();
        startForeground(1, notification);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        slotConstraints = getSlotConstraints();
        if (slotConstraints == null) {
            return;
        }
        CoWinAsyncTask coWinAsyncTask = new CoWinAsyncTask(getResources());
        coWinAsyncTask.doTask(this, slotConstraints.getDistrictId());
        Log.i("onHandleIntent", "Service is running");
    }

    private SlotConstraints getSlotConstraints() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() == 0) {
            return null;
        }
        return SlotConstraints.buildBySharedPref(sharedPreferences, getResources());
    }

    @Override
    public void onTaskSuccess(GetSlotsResponse result) {
        List<AvailableCenter> availableCenters = SlotResponseHelper.filter(result, slotConstraints.getAge(), slotConstraints.getVaccine(), slotConstraints.getFeeType(), slotConstraints.getDose());
        if (availableCenters.size() == 0) {
            Log.i("onTaskSuccess", getString(R.string.noCentersAvailable));
            return;
        }
        Log.i("onTaskSuccess", availableCenters.size() + " centers available");

        StringBuilder notificationText = new StringBuilder();
        for (AvailableCenter availableCenter : availableCenters) {
            notificationText.append(availableCenter.getName()).append(" @ ").append(availableCenter.getAvailableSessions().get(0).getDate()).append("\n");
        }

        Intent centersIntent = new Intent(this, CentersActivity.class);
        Gson gson = new Gson();
        String availableCentersJson = gson.toJson(availableCenters);
        centersIntent.putExtra(getString(R.string.availableCentersJson), availableCentersJson);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, getResources().getInteger(R.integer.notifyId), centersIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = getString(R.string.notifyChannelId);
        NotificationChannel channel = new NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(availableCenters.size() + getString(R.string.notificationTitle))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText.toString()))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(getResources().getInteger(R.integer.notifyId), mBuilder.build());
    }
}
