package com.vaccinenotifier.helper;

import android.content.res.Resources;

import com.vaccinenotifier.R;
import com.vaccinenotifier.bean.AvailableCenter;
import com.vaccinenotifier.bean.GetSlotsResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlotResponseHelper {

    public static List<AvailableCenter> filter(GetSlotsResponse slotsResponse, String age, String vaccine, String feeType, String dose, Resources resources) {

        int ageLimit = Integer.parseInt(age);
        List<String> feeTypeList = null;
        if (!feeType.equals("")) {
            feeTypeList = Arrays.asList(feeType.split(","));
        }
        List<AvailableCenter> availableCenters = new ArrayList<>();
        for (GetSlotsResponse.Center center : slotsResponse.getCenters()) {
            if (feeTypeList != null && !feeTypeList.contains(center.getFeeType())) {
                continue;
            }
            List<AvailableCenter.AvailableSession> availableSessions = new ArrayList<>();
            for (GetSlotsResponse.Session session : center.getSessions()) {
                if (constraintChecks(session, ageLimit, vaccine, dose, resources)) {
                    availableSessions.add(AvailableCenter.AvailableSession.builder()
                            .vaccine(session.getVaccine())
                            .availableCapacity(getAvailableCapacity(dose, session, resources))
                            .date(session.getDate())
                            .minAgeLimit(session.getMinAgeLimit())
                            .build());
                }
            }
            if (availableSessions.size() > 0) {
                availableCenters.add(AvailableCenter.builder()
                        .name(center.getName())
                        .pincode(center.getPincode())
                        .availableSessions(availableSessions)
                        .build());
            }
        }
        return availableCenters;
    }

    private static int getAvailableCapacity(String dose, GetSlotsResponse.Session session, Resources resources) {
        if (resources.getString(R.string.dose1).equals(dose)) {
            return session.getAvailableCapacityDose1();
        } else if (resources.getString(R.string.dose2).equals(dose)) {
            return session.getAvailableCapacityDose2();
        }
        return session.getAvailableCapacity();
    }

    private static boolean constraintChecks(GetSlotsResponse.Session session, int ageLimit, String vaccine, String dose, Resources resources) {
        if (session.getMinAgeLimit() != ageLimit) {
            return false;
        }
        if (getAvailableCapacity(dose, session, resources) <= 0) {
            return false;
        }
        List<String> vaccineList = null;
        if (!vaccine.equals("")) {
            vaccineList = Arrays.asList(vaccine.split(","));
        }
        return vaccineList == null || vaccineList.contains(session.getVaccine().toUpperCase());
    }

    public static List<AvailableCenter> filterByCapacity(GetSlotsResponse slotsResponse, Resources resources) {
        List<AvailableCenter> availableCenters = new ArrayList<>();
        for (GetSlotsResponse.Center center : slotsResponse.getCenters()) {
            List<AvailableCenter.AvailableSession> availableSessions = new ArrayList<>();
            for (GetSlotsResponse.Session session : center.getSessions()) {
                int availableCapacity = getAvailableCapacity(null, session, resources);
                if (availableCapacity > 0) {
                    availableSessions.add(AvailableCenter.AvailableSession.builder()
                            .vaccine(session.getVaccine())
                            .availableCapacityDose1(session.getAvailableCapacityDose1())
                            .availableCapacityDose2(session.getAvailableCapacityDose2())
                            .date(session.getDate())
                            .minAgeLimit(session.getMinAgeLimit())
                            .build());
                }
            }
            if (availableSessions.size() > 0) {
                availableCenters.add(AvailableCenter.builder()
                        .name(center.getName())
                        .pincode(center.getPincode())
                        .availableSessions(availableSessions)
                        .build());
            }
        }
        return availableCenters;
    }
}
