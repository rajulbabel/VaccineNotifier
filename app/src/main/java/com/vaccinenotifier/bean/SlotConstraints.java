package com.vaccinenotifier.bean;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.vaccinenotifier.R;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@Builder
@FieldNameConstants
public class SlotConstraints {

    private Integer age;
    private String vaccine;
    private String feeType;
    private String dose;
    private Integer districtId;
    private String districtName;
    private Integer districtSpinnerPosition;

    public static SlotConstraints buildBySharedPref(SharedPreferences sharedPreferences, Resources resources) {
        return SlotConstraints.builder()
                .districtId(sharedPreferences.getInt(SlotConstraints.Fields.districtId, resources.getInteger(R.integer.defaultDistrictId)))
                .districtName(sharedPreferences.getString(SlotConstraints.Fields.districtName, resources.getString(R.string.defaultDistrictName)))
                .districtSpinnerPosition(sharedPreferences.getInt(SlotConstraints.Fields.districtSpinnerPosition, resources.getInteger(R.integer.districtSpinnerPosition)))
                .age(sharedPreferences.getInt(SlotConstraints.Fields.age, resources.getInteger(R.integer.defaultAge)))
                .vaccine(sharedPreferences.getString(SlotConstraints.Fields.vaccine, resources.getString(R.string.defaultVaccine)))
                .feeType(sharedPreferences.getString(SlotConstraints.Fields.feeType, resources.getString(R.string.defaultFeeType)))
                .dose(sharedPreferences.getString(SlotConstraints.Fields.dose, resources.getString(R.string.defaultDose))).build();
    }
}
