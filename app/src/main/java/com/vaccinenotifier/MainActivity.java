package com.vaccinenotifier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.vaccinenotifier.api.CoWinAsyncTask;
import com.vaccinenotifier.bean.AvailableCenter;
import com.vaccinenotifier.bean.GetSlotsResponse;
import com.vaccinenotifier.bean.SlotConstraints;
import com.vaccinenotifier.helper.SlotResponseHelper;
import com.vaccinenotifier.service.BroadcastReceiverImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SearchableSpinner searchableSpinner = findViewById(R.id.districtNames);
        searchableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE).edit()
                        .putString(SlotConstraints.Fields.districtName, searchableSpinner.getSelectedItem().toString())
                        .putInt(SlotConstraints.Fields.districtId, getResources().getIntArray(R.array.districtIds)[position])
                        .putInt(SlotConstraints.Fields.districtSpinnerPosition, position)
                        .apply();
                refreshAlertDetails();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        refreshAlertDetails();
    }

    private void scheduleAlarm(boolean enable) {
        Intent intent = new Intent(getApplicationContext(), BroadcastReceiverImpl.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, BroadcastReceiverImpl.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (enable) {
            alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), getResources().getInteger(R.integer.cronIntervalMs), pIntent);
        } else {
            alarm.cancel(pIntent);
        }
    }

    private void refreshAlertDetails() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() == 0) {
            return;
        }
        SlotConstraints slotConstraints = SlotConstraints.buildBySharedPref(sharedPreferences, getResources());

        TextView textView = findViewById(R.id.alertDetails);
        String notificationStatus = slotConstraints.isEnabled() ? getString(R.string.notificationOn) : getString(R.string.notificationOff);
        textView.setText(getString(R.string.alertConfigDisplayString, notificationStatus) + getString(R.string.alertsDescription, slotConstraints.getDistrictName(), slotConstraints.getAge(), slotConstraints.getVaccine(), slotConstraints.getFeeType(), slotConstraints.getDose()));
        populateSharedPrefDataToUi(slotConstraints);
    }

    private void populateSharedPrefDataToUi(SlotConstraints slotConstraints) {
        Integer districtSpinnerPosition = slotConstraints.getDistrictSpinnerPosition();
        String age = slotConstraints.getAge();
        String vaccine = slotConstraints.getVaccine();
        String feeType = slotConstraints.getFeeType();
        String dose = slotConstraints.getDose();
        Spinner spinner = findViewById(R.id.districtNames);
        spinner.setSelection(districtSpinnerPosition);

        if (getString(R.string.age18).equals(age)) {
            checkRadioBox(R.id.age18);
        } else if (getString(R.string.age45).equals(age)) {
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
        enableSwitch(slotConstraints.isEnabled());
    }

    private void enableSwitch(boolean isEnable) {
        SwitchCompat switchCompat = findViewById(R.id.alertSwitch);
        switchCompat.setChecked(isEnable);
        scheduleAlarm(isEnable);
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

    private boolean createAlert() {
        Spinner spinner = findViewById(R.id.districtNames);
        int spinnerPosition = spinner.getSelectedItemPosition();
        if (spinnerPosition == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.districtEmpty), Toast.LENGTH_SHORT).show();
            return false;
        }

        getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE).edit()
                .putBoolean(SlotConstraints.Fields.isEnabled, true)
                .apply();

        Toast.makeText(MainActivity.this, getString(R.string.alertCreated), Toast.LENGTH_SHORT).show();
        return true;
    }

    private void disableAlert() {
        getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE).edit()
                .putBoolean(SlotConstraints.Fields.isEnabled, false)
                .apply();
        Toast.makeText(MainActivity.this, getString(R.string.alertDisabled), Toast.LENGTH_SHORT).show();
    }

    public void switchListener(View view) {
        SwitchCompat switchCompat = (SwitchCompat) view;
        if (switchCompat.isChecked()) {
            switchCompat.setChecked(createAlert());
        } else {
            disableAlert();
        }
        refreshAlertDetails();
    }

    public void radioBoxListener(View view) {
        RadioButton radioButton = (RadioButton) view;
        getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE).edit()
                .putString(SlotConstraints.getViewIdToType().get(radioButton.getId()), radioButton.getText().toString())
                .apply();
        refreshAlertDetails();
    }

    public void checkBoxClickListener(View view) {
        CheckBox checkBox = (CheckBox) view;
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.alertsSharedPreferencesName), Context.MODE_PRIVATE);
        String existingValue = sharedPreferences.getString(SlotConstraints.getViewIdToType().get(checkBox.getId()), null);
        if (checkBox.isChecked()) {
            if (!TextUtils.isEmpty(existingValue)) {
                ArrayList<String> newValues = new ArrayList<>(Arrays.asList(existingValue.split(getString(R.string.comma))));
                newValues.add(checkBox.getText().toString());
                sharedPreferences.edit().putString(SlotConstraints.getViewIdToType().get(checkBox.getId()), String.join(getString(R.string.comma), newValues)).apply();
            } else {
                sharedPreferences.edit().putString(SlotConstraints.getViewIdToType().get(checkBox.getId()), checkBox.getText().toString()).apply();
            }
        } else {
            if (!TextUtils.isEmpty(existingValue)) {
                ArrayList<String> newValues = new ArrayList<>(Arrays.asList(existingValue.split(getString(R.string.comma))));
                newValues.remove(checkBox.getText().toString());
                sharedPreferences.edit().putString(SlotConstraints.getViewIdToType().get(checkBox.getId()), String.join(getString(R.string.comma), newValues)).apply();
            } else {
                sharedPreferences.edit().putString(SlotConstraints.getViewIdToType().get(checkBox.getId()), checkBox.getText().toString()).apply();
            }
        }
        refreshAlertDetails();
    }

    public void checkAllSlots(View view) {
        Button checkAllSlotsButton = (Button) view;
        Spinner spinner = findViewById(R.id.districtNames);
        int spinnerPosition = spinner.getSelectedItemPosition();
        if (spinnerPosition == 0) {
            Toast.makeText(MainActivity.this, getString(R.string.districtEmpty), Toast.LENGTH_SHORT).show();
            return;
        }
        checkAllSlotsButton.setEnabled(false);
        checkAllSlotsButton.setText(R.string.checking);
        CoWinAsyncTask coWinAsyncTask = new CoWinAsyncTask(getResources());
        coWinAsyncTask.doTask(new CoWinAsyncTask.ResultListener() {
            @Override
            public void onTaskSuccess(GetSlotsResponse result) {
                List<AvailableCenter> availableCenters = SlotResponseHelper.filterByCapacity(result, getResources());
                if (availableCenters.size() == 0) {
                    Log.i("onTaskSuccess", getString(R.string.noCentersAvailable));
                    createPopup(getString(R.string.noCentersAvailable), getString(R.string.tryAgainLater));
                    checkAllSlotsButton.setEnabled(true);
                    checkAllSlotsButton.setText(R.string.checkAllSlots);
                    return;
                }
                Gson gson = new Gson();
                String availableCentersJson = gson.toJson(availableCenters);
                Intent centersActivityIntent = new Intent(getApplicationContext(), CentersActivity.class);
                centersActivityIntent.putExtra(getString(R.string.availableCentersJson), availableCentersJson);
                centersActivityIntent.putExtra(getString(R.string.centerViewTypeCheckAllSlots), true);
                centersActivityIntent.putExtra(SlotConstraints.Fields.districtId, getResources().getIntArray(R.array.districtIds)[spinnerPosition]);
                centersActivityIntent.putExtra(SlotConstraints.Fields.districtName, spinner.getSelectedItem().toString());
                startActivity(centersActivityIntent);
                checkAllSlotsButton.setEnabled(true);
                checkAllSlotsButton.setText(R.string.checkAllSlots);
            }

            @Override
            public void onTaskFailure() {
                createPopup(getString(R.string.someErrorOccurred), getString(R.string.tryAgainLater));
                checkAllSlotsButton.setEnabled(true);
                checkAllSlotsButton.setText(R.string.checkAllSlots);
            }
        }, getResources().getIntArray(R.array.districtIds)[spinnerPosition]);
    }

    private void createPopup(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }
}
