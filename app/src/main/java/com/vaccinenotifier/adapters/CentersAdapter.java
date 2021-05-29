package com.vaccinenotifier.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.vaccinenotifier.R;
import com.vaccinenotifier.bean.AvailableCenter;
import com.vaccinenotifier.bean.SlotConstraints;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CentersAdapter extends RecyclerView.Adapter<CentersAdapter.CentersViewHolder> {

    private Resources resources;
    private List<AvailableCenter> availableCenters;
    private boolean displayAllSlots;

    public CentersAdapter(Resources resources, boolean displayAllSlots) {
        this.resources = resources;
        this.displayAllSlots = displayAllSlots;
    }

    @NonNull
    @Override
    public CentersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CentersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.center_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CentersViewHolder holder, int position) {
        if (displayAllSlots) {
            holder.displayAllSlotsBind(availableCenters.get(position));
        } else {
            holder.bind(availableCenters.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return availableCenters.size();
    }

    class CentersViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView centerName;
        private final AppCompatTextView centerPincode;
        private final AppCompatTextView sessionInfo;

        public CentersViewHolder(@NonNull View itemView) {
            super(itemView);
            centerName = itemView.findViewById(R.id.centerName);
            centerPincode = itemView.findViewById(R.id.centerPincode);
            sessionInfo = itemView.findViewById(R.id.sessionInfo);
        }

        public void bind(AvailableCenter availableCenter) {
            centerName.setText(resources.getString(R.string.centerNameDisplay, availableCenter.getName()));
            centerPincode.setText(resources.getString(R.string.centerPincodeDisplay, availableCenter.getPincode()));
            StringBuilder sessionData = new StringBuilder();
            for (AvailableCenter.AvailableSession session : availableCenter.getAvailableSessions()) {
                sessionData.append(session.getDate())
                        .append(": ")
                        .append(session.getVaccine())
                        .append(" quantity: ")
                        .append(session.getAvailableCapacity())
                        .append(resources.getString(R.string.newLine));
            }
            sessionInfo.setText(sessionData.toString());
        }

        public void displayAllSlotsBind(AvailableCenter availableCenter) {
            centerName.setText(resources.getString(R.string.centerNameDisplay, availableCenter.getName()));
            centerPincode.setText(resources.getString(R.string.centerPincodeDisplay, availableCenter.getPincode()));
            StringBuilder sessionData = new StringBuilder();
            for (AvailableCenter.AvailableSession session : availableCenter.getAvailableSessions()) {
                sessionData.append(resources.getString(R.string.smallTab)).append(session.getDate()).append(":")
                        .append(resources.getString(R.string.newLine))
                        .append(resources.getString(R.string.largeTab))
                        .append(SlotConstraints.Fields.vaccine).append(": ").append(session.getVaccine())
                        .append(resources.getString(R.string.newLine))
                        .append(resources.getString(R.string.largeTab))
                        .append(SlotConstraints.Fields.age).append(": ").append(session.getMinAgeLimit())
                        .append(resources.getString(R.string.newLine))
                        .append(resources.getString(R.string.largeTab))
                        .append(resources.getString(R.string.dose1)).append(": ").append(session.getAvailableCapacityDose1())
                        .append(resources.getString(R.string.newLine))
                        .append(resources.getString(R.string.largeTab))
                        .append(resources.getString(R.string.dose2)).append(": ").append(session.getAvailableCapacityDose2())
                        .append(resources.getString(R.string.newLine));
            }
            sessionInfo.setText(sessionData.toString());
        }
    }
}
