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
import com.vaccinenotifier.bean.VaccineConstraints;
import com.vaccinenotifier.helper.SlotResponseHelper;

import java.util.List;

public class CheckSlotsService extends IntentService implements CoWinAsyncTask.ResultListener {


    public CheckSlotsService() {
        super("CheckSlotsService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String CHANNEL_ID = "vaccine_notifier";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Vaccine notifier", NotificationManager.IMPORTANCE_NONE);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).build();
        startForeground(1, notification);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        CoWinAsyncTask coWinAsyncTask = new CoWinAsyncTask();
        coWinAsyncTask.doTask(this);
        Log.i("onHandleIntent", "Service is running");
    }

    @Override
    public void onTaskSuccess(GetSlotsResponse result) {
        SharedPreferences sharedPreferences = getSharedPreferences("Alerts", Context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() == 0) {
            return;
        }
        int age = sharedPreferences.getInt(VaccineConstraints.age.name(), 18);
        String vaccine = sharedPreferences.getString(VaccineConstraints.vaccine.name(), "");
        String feeType = sharedPreferences.getString(VaccineConstraints.feeType.name(), "");
        String dose = sharedPreferences.getString(VaccineConstraints.dose.name(), "");
        List<AvailableCenter> availableCenters = SlotResponseHelper.filter(result, age, vaccine, feeType, dose);
        if (availableCenters.size() == 0) {
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
        centersIntent.putExtra("availableCentersJson", availableCentersJson);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 123, centersIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String CHANNEL_ID = "vaccine_notifier";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Vaccine Notifier", NotificationManager.IMPORTANCE_HIGH);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(availableCenters.size() + " Centers Available!!, Click/Expand to check centers")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText.toString()))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(123, mBuilder.build());
    }
}
