package com.vaccinenotifier.helper;

import com.vaccinenotifier.bean.AvailableCenter;
import com.vaccinenotifier.bean.GetSlotsResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlotResponseHelper {

    public static List<AvailableCenter> filter(GetSlotsResponse slotsResponse, int ageLimit, String vaccine, String feeType, String dose) {

        List<String> feeTypeList = null;
        if (!feeType.equals("")) {
            feeTypeList = Arrays.asList(feeType.split(","));
        }
        List<AvailableCenter> availableCenters = new ArrayList<>();
        for (GetSlotsResponse.Center center : slotsResponse.getCenters()) {
            if (feeTypeList != null && !feeTypeList.contains(center.getFeeType().toLowerCase())) {
                continue;
            }
            List<AvailableCenter.AvailableSession> availableSessions = new ArrayList<>();
            for (GetSlotsResponse.Session session : center.getSessions()) {
                if (constraintChecks(session, ageLimit, vaccine)) {
                    AvailableCenter.AvailableSession availableSession = new AvailableCenter.AvailableSession();
                    availableSession.setVaccine(session.getVaccine());
                    availableSession.setAvailableCapacity(session.getAvailableCapacity());
                    availableSession.setDate(session.getDate());
                    availableSessions.add(availableSession);
                }
            }
            if (availableSessions.size() > 0) {
                AvailableCenter availableCenter = new AvailableCenter();
                availableCenter.setName(center.getName());
                availableCenter.setPincode(center.getPincode());
                availableCenter.setAvailableSessions(availableSessions);
                availableCenters.add(availableCenter);
            }
        }
        return availableCenters;
    }

    private static boolean constraintChecks(GetSlotsResponse.Session session, int ageLimit, String vaccine) {
        if (session.getMinAgeLimit() != ageLimit) {
            return false;
        }
        if (session.getAvailableCapacity() <= 0) {
            return false;
        }
        List<String> vaccineList = null;
        if (!vaccine.equals("")) {
            vaccineList = Arrays.asList(vaccine.split(","));
        }
        return vaccineList == null || vaccineList.contains(session.getVaccine().toLowerCase());
    }
}
