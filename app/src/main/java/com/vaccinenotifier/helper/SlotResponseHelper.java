package com.vaccinenotifier.helper;

import android.content.res.Resources;

import com.vaccinenotifier.R;
import com.vaccinenotifier.bean.AvailableCenter;
import com.vaccinenotifier.bean.GetSlotsResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlotResponseHelper {

    public static List<AvailableCenter> filter(GetSlotsResponse slotsResponse, int ageLimit, String vaccine, String feeType, String dose, Resources resources) {

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
                    AvailableCenter.AvailableSession availableSession = new AvailableCenter.AvailableSession();
                    availableSession.setVaccine(session.getVaccine());
                    availableSession.setAvailableCapacity(getAvailableCapacity(dose, session, resources));
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

    private static int getAvailableCapacity(String dose, GetSlotsResponse.Session session, Resources resources) {
        if (dose.equals(resources.getString(R.string.dose1))) {
            return session.getAvailableCapacityDose1();
        } else if (dose.equals(resources.getString(R.string.dose2))) {
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
}
