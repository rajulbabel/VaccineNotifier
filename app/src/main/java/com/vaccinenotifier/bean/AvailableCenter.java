package com.vaccinenotifier.bean;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AvailableCenter {
    private String name;
    private String pincode;
    private List<AvailableSession> availableSessions;

    @Getter
    @Setter
    @Builder
    public static class AvailableSession {
        private String vaccine;
        private Integer availableCapacity;
        private Integer availableCapacityDose1;
        private Integer availableCapacityDose2;
        private String date;
        private Integer minAgeLimit;
        private String dose;
    }
}
