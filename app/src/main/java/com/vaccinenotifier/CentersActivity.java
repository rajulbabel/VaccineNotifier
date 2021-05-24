package com.vaccinenotifier;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.vaccinenotifier.adapters.CentersAdapter;
import com.vaccinenotifier.bean.AvailableCenter;

import java.util.ArrayList;
import java.util.List;

public class CentersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centers);
        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        List<AvailableCenter> availableCenterList = new ArrayList<>();
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(getIntent().getExtras().get("availableCentersJson").toString()).getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            availableCenterList.add(gson.fromJson(jsonElement, AvailableCenter.class));
        }

        CentersAdapter centersAdapter = new CentersAdapter();
        centersAdapter.setAvailableCenters(availableCenterList);
        recyclerView.setAdapter(centersAdapter);
    }
}