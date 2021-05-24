package com.vaccinenotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vaccinenotifier.bean.VaccineConstraints;
import com.vaccinenotifier.service.BroadcastReceiverImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshAlertDetails();
        scheduleAlarm();
    }

    private void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), BroadcastReceiverImpl.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, BroadcastReceiverImpl.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 2000, pIntent);
    }

    private void refreshAlertDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences("Alerts", Context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() == 0) {
            return;
        }
        int age = sharedPreferences.getInt(VaccineConstraints.age.name(), 18);
        String vaccine = sharedPreferences.getString(VaccineConstraints.vaccine.name(), "");
        String feeType = sharedPreferences.getString(VaccineConstraints.feeType.name(), "");
        String dose = sharedPreferences.getString(VaccineConstraints.dose.name(), "");

        final String newLine = "\n";
        TextView textView = findViewById(R.id.alertDetails);
        textView.setText("age: " + age + newLine +
                "vaccine: " + vaccine + newLine +
                "feeType: " + feeType + newLine +
                "dose: " + dose + newLine);

        if (age == 18) {
            checkRadioBox(R.id.age18);
        } else if (age == 45) {
            checkRadioBox(R.id.age45);
        }

        if (dose.equals("does1")) {
            checkRadioBox(R.id.dose1);
        } else if (dose.equals("dose2")) {
            checkRadioBox(R.id.dose2);
        }

        List<String> vaccineList = Arrays.asList(vaccine.split(","));
        List<Integer> checkBoxList = new ArrayList<>();
        if (vaccineList.contains("covishield")) {
            checkBoxList.add(R.id.covishield);
        }
        if (vaccineList.contains("covaxin")) {
            checkBoxList.add(R.id.covaxin);
        }
        if (vaccineList.contains("sputnik")) {
            checkBoxList.add(R.id.sputnik);
        }
        if (vaccineList.size() == 0) {
            checkBoxList = Arrays.asList(R.id.covishield, R.id.covaxin, R.id.sputnik);
        }

        List<String> feeTypeList = Arrays.asList(feeType.split(","));
        if (feeTypeList.contains("free")) {
            checkBoxList.add(R.id.free);
        }
        if (feeTypeList.contains("paid")) {
            checkBoxList.add(R.id.paid);
        }
        if (feeTypeList.size() == 0) {
            checkBoxList.add(R.id.free);
            checkBoxList.add(R.id.paid);
        }
        checkCheckBox(checkBoxList);
    }

    private void checkCheckBox(List<Integer> checkBoxIds) {
        for (Integer checkBoxId : checkBoxIds) {
            CheckBox checkBox = findViewById(checkBoxId);
            checkBox.setChecked(true);
        }
    }

    private void checkRadioBox(Integer radioBoxId) {
        RadioButton radioButton = findViewById(radioBoxId);
        radioButton.setChecked(true);
    }

    public void createAlert(View view) {
        String age = getRadioBoxValueFromGroup(R.id.age);
        String vaccine = getCheckBoxValues(Arrays.asList(R.id.covishield, R.id.covaxin, R.id.sputnik));
        String feeType = getCheckBoxValues(Arrays.asList(R.id.free, R.id.paid));
        String dose = getRadioBoxValueFromGroup(R.id.dose);

        SharedPreferences sharedpreferences = getSharedPreferences("Alerts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(VaccineConstraints.age.name(), Integer.parseInt(age));
        editor.putString(VaccineConstraints.vaccine.name(), vaccine);
        editor.putString(VaccineConstraints.feeType.name(), feeType);
        editor.putString(VaccineConstraints.dose.name(), dose);
        editor.apply();

        Toast.makeText(MainActivity.this, "Alert created", Toast.LENGTH_SHORT).show();
        refreshAlertDetails();
    }

    private String getCheckBoxValues(List<Integer> checkBoxIds) {
        ArrayList<String> checkBoxValues = new ArrayList<>();
        for (Integer checkBoxId : checkBoxIds) {
            CheckBox checkBox = findViewById(checkBoxId);
            if (checkBox.isChecked()) {
                checkBoxValues.add(checkBox.getText().toString());
            }
        }
        return String.join(",", checkBoxValues);
    }

    private String getRadioBoxValueFromGroup(Integer radioBoxGroupId) {
        RadioGroup radioGroup = findViewById(radioBoxGroupId);
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedRadioButtonId);
        return radioButton.getText().toString();
    }
}
