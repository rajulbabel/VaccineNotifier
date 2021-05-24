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
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() == 0) {
            return;
        }
        int age = sharedPreferences.getInt(VaccineConstraints.age.name(), 18);
        String vaccine = sharedPreferences.getString(VaccineConstraints.vaccine.name(), "");
        String feeType = sharedPreferences.getString(VaccineConstraints.feeType.name(), "");
        String dose = sharedPreferences.getString(VaccineConstraints.dose.name(), "");

        final String newLine = getString(R.string.newLine);
        TextView textView = findViewById(R.id.alertDetails);
        textView.setText("age: " + age + newLine + "vaccine: " + vaccine + newLine + "feeType: " + feeType + newLine + "dose: " + dose + newLine);
        populateSharedPrefDataToUi(age, vaccine, feeType, dose);
    }

    private void populateSharedPrefDataToUi(int age, String vaccine, String feeType, String dose) {
        if (age == getResources().getInteger(R.integer.age18)) {
            checkRadioBox(R.id.age18);
        } else if (age == getResources().getInteger(R.integer.age45)) {
            checkRadioBox(R.id.age45);
        }

        if (dose.equals(getString(R.string.dose1))) {
            checkRadioBox(R.id.dose1);
        } else if (dose.equals(getString(R.string.dose2))) {
            checkRadioBox(R.id.dose2);
        }

        List<String> vaccineList = Arrays.asList(vaccine.split(getString(R.string.comma)));
        List<Integer> checkBoxList = new ArrayList<>();
        if (vaccineList.contains(getString(R.string.COVISHIELD))) {
            checkBoxList.add(R.id.COVISHIELD);
        }
        if (vaccineList.contains(getString(R.string.COVAXIN))) {
            checkBoxList.add(R.id.COVAXIN);
        }
        if (vaccineList.contains(getString(R.string.SPUTNIK))) {
            checkBoxList.add(R.id.SPUTNIK);
        }
        if (vaccineList.size() == 0) {
            checkBoxList = Arrays.asList(R.id.COVISHIELD, R.id.COVAXIN, R.id.SPUTNIK);
        }

        List<String> feeTypeList = Arrays.asList(feeType.split(getString(R.string.comma)));
        if (feeTypeList.contains(getString(R.string.feeTypeFree))) {
            checkBoxList.add(R.id.feeTypeFree);
        }
        if (feeTypeList.contains(getString(R.string.feeTypePaid))) {
            checkBoxList.add(R.id.feeTypePaid);
        }
        if (feeTypeList.size() == 0) {
            checkBoxList.add(R.id.feeTypeFree);
            checkBoxList.add(R.id.feeTypePaid);
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
        String vaccine = getCheckBoxValues(Arrays.asList(R.id.COVISHIELD, R.id.COVAXIN, R.id.SPUTNIK));
        String feeType = getCheckBoxValues(Arrays.asList(R.id.feeTypeFree, R.id.feeTypePaid));
        String dose = getRadioBoxValueFromGroup(R.id.dose);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(VaccineConstraints.age.name(), Integer.parseInt(age));
        editor.putString(VaccineConstraints.vaccine.name(), vaccine);
        editor.putString(VaccineConstraints.feeType.name(), feeType);
        editor.putString(VaccineConstraints.dose.name(), dose);
        editor.apply();

        Toast.makeText(MainActivity.this, getString(R.string.alertCreated), Toast.LENGTH_SHORT).show();
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
        return String.join(getString(R.string.comma), checkBoxValues);
    }

    private String getRadioBoxValueFromGroup(Integer radioBoxGroupId) {
        RadioGroup radioGroup = findViewById(radioBoxGroupId);
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedRadioButtonId);
        return radioButton.getText().toString();
    }
}
