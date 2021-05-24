package com.vaccinenotifier.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSlotsResponse {

    private List<Center> centers;

    @Getter
    @Setter
    public static class Center {
        @SerializedName("center_id")
        private String centerId;
        private String name;
        private String address;
        @SerializedName("state_name")
        private String stateName;
        @SerializedName("district_name")
        private String districtName;
        @SerializedName("block_name")
        private String blockName;
        private String pincode;
        private String lat;
        @SerializedName("long")
        private String lng;
        private String from;
        private String to;
        @SerializedName("fee_type")
        private String feeType;
        private List<Session> sessions;
    }

    @Getter
    @Setter
    public static class Session {
        @SerializedName("session_id")
        private String sessionId;
        private String date;
        private List<String> slots;
        @SerializedName("available_capacity")
        private Integer availableCapacity;
        @SerializedName("min_age_limit")
        private Integer minAgeLimit;
        private String vaccine;
        @SerializedName("available_capacity_dose1")
        private Integer availableCapacityDose1;
        @SerializedName("available_capacity_dose2")
        private Integer availableCapacityDose2;

    }
}
