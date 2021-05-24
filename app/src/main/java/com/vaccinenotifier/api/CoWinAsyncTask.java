package com.vaccinenotifier.api;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.vaccinenotifier.R;
import com.vaccinenotifier.bean.GetSlotsResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Response;

public class CoWinAsyncTask extends AsyncTask<Integer, Void, Object> {

    private final Resources resources;
    private ResultListener listener;

    public CoWinAsyncTask(Resources resources) {
        this.resources = resources;
    }

    public void doTask(ResultListener listener, Integer districtId) {
        this.listener = listener;
        this.execute(districtId);
    }

    @Override
    protected Object doInBackground(Integer... params) {
        Integer districtId = params[0];
        GetSlotsResponse slotResponse = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(resources.getString(R.string.coWinDateFormat));
        String todayDate = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        try {
            Response<GetSlotsResponse> getSlotsResponse = RetrofitClientInstance.getRetrofitInstance(resources.getString(R.string.coWinBaseUrl))
                    .create(CoWin.class).getSlots(districtId, todayDate, System.getProperty(resources.getString(R.string.httpAgent)))
                    .execute();
            int responseCode = getSlotsResponse.code();
            if (responseCode == 200) {
                slotResponse = getSlotsResponse.body();
            } else if (getSlotsResponse.errorBody() != null) {
                return getSlotsResponse.errorBody().string();
            }
        } catch (IOException e) {
            return e;
        }
        return slotResponse;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if (result == null) {
            Log.e("onPostExecute", "empty response");
        } else if (result instanceof String) {
            Log.e("onPostExecute", (String) result);
        } else if (result instanceof IOException) {
            Log.e("onPostExecute", ((IOException) result).getMessage());
            listener.onTaskFailure((IOException) result);
        } else if (result instanceof GetSlotsResponse) {
            listener.onTaskSuccess((GetSlotsResponse) result);
        }
    }

    public interface ResultListener {
        void onTaskSuccess(GetSlotsResponse result);

        default void onTaskFailure(IOException e) {
        }
    }
}
