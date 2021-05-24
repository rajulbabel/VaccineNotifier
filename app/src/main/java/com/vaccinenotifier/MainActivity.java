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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vaccinenotifier.bean.SlotConstraints;
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
        alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), getResources().getInteger(R.integer.cronIntervalMs), pIntent);
    }

    private void refreshAlertDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() == 0) {
            return;
        }
        SlotConstraints slotConstraints = SlotConstraints.buildBySharedPref(sharedPreferences, getResources());

        final String newLine = getString(R.string.newLine);
        TextView textView = findViewById(R.id.alertDetails);
        textView.setText("District Name: " + slotConstraints.getDistrictName() + newLine + "Age: " + slotConstraints.getAge() + newLine + "Vaccine: " + slotConstraints.getVaccine() + newLine + "Fee Type: " + slotConstraints.getFeeType() + newLine + "Dose: " + slotConstraints.getDose() + newLine);
        populateSharedPrefDataToUi(slotConstraints.getDistrictSpinnerPosition(), slotConstraints.getAge(), slotConstraints.getVaccine(), slotConstraints.getFeeType(), slotConstraints.getDose());
    }

    private void populateSharedPrefDataToUi(Integer districtSpinnerPosition, int age, String vaccine, String feeType, String dose) {
        Spinner spinner = findViewById(R.id.districtNames);
        spinner.setSelection(districtSpinnerPosition);

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
        Spinner spinner = findViewById(R.id.districtNames);
        int spinnerPosition = spinner.getSelectedItemPosition();
        if (spinnerPosition == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.districtEmpty), Toast.LENGTH_SHORT).show();
            return;
        }
        SlotConstraints slotConstraints = SlotConstraints.builder()
                .districtName(spinner.getSelectedItem().toString())
                .districtId(getResources().getIntArray(R.array.districtIds)[spinnerPosition])
                .districtSpinnerPosition(spinnerPosition)
                .age(Integer.parseInt(getRadioBoxValueFromGroup(R.id.age)))
                .vaccine(getCheckBoxValues(Arrays.asList(R.id.COVISHIELD, R.id.COVAXIN, R.id.SPUTNIK)))
                .feeType(getCheckBoxValues(Arrays.asList(R.id.feeTypeFree, R.id.feeTypePaid)))
                .dose(getRadioBoxValueFromGroup(R.id.dose)).build();

        getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE).edit()
                .putString(SlotConstraints.Fields.districtName, slotConstraints.getDistrictName())
                .putInt(SlotConstraints.Fields.districtId, slotConstraints.getDistrictId())
                .putInt(SlotConstraints.Fields.districtSpinnerPosition, slotConstraints.getDistrictSpinnerPosition())
                .putInt(SlotConstraints.Fields.age, slotConstraints.getAge())
                .putString(SlotConstraints.Fields.vaccine, slotConstraints.getVaccine())
                .putString(SlotConstraints.Fields.feeType, slotConstraints.getFeeType())
                .putString(SlotConstraints.Fields.dose, slotConstraints.getDose())
                .apply();

        Toast.makeText(MainActivity.this, getString(R.string.alertCreated), Toast.LENGTH_SHORT).show();
        refreshAlertDetails();
    }

    public void removeAlert(View view) {
        getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE).edit().clear().apply();
        TextView textView = findViewById(R.id.alertDetails);
        textView.setText(getString(R.string.alertDetails));
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
