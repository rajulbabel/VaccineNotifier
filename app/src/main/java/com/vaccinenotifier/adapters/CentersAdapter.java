package com.vaccinenotifier.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.vaccinenotifier.R;
import com.vaccinenotifier.bean.AvailableCenter;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CentersAdapter extends RecyclerView.Adapter<CentersAdapter.CentersViewHolder> {

    private List<AvailableCenter> availableCenters;

    @NonNull
    @Override
    public CentersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CentersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.center_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CentersViewHolder holder, int position) {
        holder.bind(availableCenters.get(position));
    }

    @Override
    public int getItemCount() {
        return availableCenters.size();
    }

    static class CentersViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView centerText;
        private final AppCompatTextView centerPincode;
        private final AppCompatTextView sessionInfo;

        public CentersViewHolder(@NonNull View itemView) {
            super(itemView);
            centerText = itemView.findViewById(R.id.centerName);
            centerPincode = itemView.findViewById(R.id.centerPincode);
            sessionInfo = itemView.findViewById(R.id.sessionInfo);
        }

        public void bind(AvailableCenter availableCenter) {
            centerText.setText("Center Name: " + availableCenter.getName());
            centerPincode.setText("Center Pincode: " + availableCenter.getPincode());
            StringBuilder sessionData = new StringBuilder();
            for (AvailableCenter.AvailableSession session : availableCenter.getAvailableSessions()) {
                sessionData.append(session.getDate()).append(": ").append(session.getVaccine()).append(" quantity: ").append(session.getAvailableCapacity()).append("\n");
            }
            sessionInfo.setText(sessionData.toString());
        }
    }
}
