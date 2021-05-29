package com.vaccinenotifier.bean;

import android.content.SharedPreferences;
import android.content.res.Resources;

import com.vaccinenotifier.R;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@Builder
@FieldNameConstants
public class SlotConstraints {

    @Getter
    private static Map<Integer, String> viewIdToType = new HashMap<Integer, String>() {{
        put(R.id.age18, Fields.age);
        put(R.id.age45, Fields.age);
        put(R.id.COVISHIELD, Fields.vaccine);
        put(R.id.COVAXIN, Fields.vaccine);
        put(R.id.SPUTNIK, Fields.vaccine);
        put(R.id.feeTypeFree, Fields.feeType);
        put(R.id.feeTypePaid, Fields.feeType);
        put(R.id.dose1, Fields.dose);
        put(R.id.dose2, Fields.dose);
    }};
    private boolean isEnabled;
    private String vaccine;
    private String feeType;
    private String dose;
    private Integer districtId;
    private String districtName;
    private Integer districtSpinnerPosition;
    private String age;

    public static SlotConstraints buildBySharedPref(SharedPreferences sharedPreferences, Resources resources) {
        return SlotConstraints.builder()
                .isEnabled(sharedPreferences.getBoolean(Fields.isEnabled, resources.getBoolean(R.bool.isAlertEnabled)))
                .districtId(sharedPreferences.getInt(SlotConstraints.Fields.districtId, resources.getInteger(R.integer.defaultDistrictId)))
                .districtName(sharedPreferences.getString(SlotConstraints.Fields.districtName, resources.getString(R.string.defaultDistrictName)))
                .districtSpinnerPosition(sharedPreferences.getInt(SlotConstraints.Fields.districtSpinnerPosition, resources.getInteger(R.integer.districtSpinnerPosition)))
                .age(sharedPreferences.getString(SlotConstraints.Fields.age, resources.getString(R.string.defaultAge)))
                .vaccine(sharedPreferences.getString(SlotConstraints.Fields.vaccine, resources.getString(R.string.defaultVaccine)))
                .feeType(sharedPreferences.getString(SlotConstraints.Fields.feeType, resources.getString(R.string.defaultFeeType)))
                .dose(sharedPreferences.getString(SlotConstraints.Fields.dose, resources.getString(R.string.defaultDose))).build();
    }
}
