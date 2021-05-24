package com.vaccinenotifier.bean;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailableCenter {
    private String name;
    private String pincode;
    private List<AvailableSession> availableSessions;

    @Getter
    @Setter
    public static class AvailableSession {
        private String vaccine;
        private Integer availableCapacity;
        private String date;
    }
}
