package com.vaccinenotifier.api;

import com.vaccinenotifier.bean.GetSlotsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface CoWin {

    @GET("/api/v2/appointment/sessions/public/calendarByDistrict")
    Call<GetSlotsResponse> getSlots(@Query("district_id") Integer districtId, @Query("date") String date, @Header("User-Agent") String userAgent);
}
