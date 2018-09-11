package com.example.android.dailyMedicine.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.dailyMedicine.R;
import com.example.android.dailyMedicine.db.Medicine;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private List<Medicine> mMedicinesList;
    private Resources mResourcesCallback;
    private ActionListener mActionListenerCallback;

    MedicineAdapter(List<Medicine> medicinesList, Resources resourcesCallback, ActionListener actionListener) {
        this.mMedicinesList = medicinesList;
        this.mResourcesCallback = resourcesCallback;
        this.mActionListenerCallback = actionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.medicine_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final Medicine medicine;

        medicine = mMedicinesList.get(i);

        holder.numberOfDailyTakeTimesTextView
                .setText(String.valueOf(medicine.getMedicineTotalNumberOfTakeTimesPerDay()));

        holder.medicineNameTextView.setText(medicine.getMedicineName());

        String stringHelper = String.valueOf(medicine.getMedicineTotalNumberOfTakenTimesToday()) +
                mResourcesCallback.getStringResources(R.string.out_of) +
                String.valueOf(medicine.getMedicineTotalNumberOfTakeTimesPerDay()) +
                mResourcesCallback.getStringResources(R.string.times_today);

        holder.numberOfTakenTimesTodayTextView.setText(stringHelper);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActionListenerCallback.onListItemClickListener(
                        medicine.get_ID(),
                        medicine.getMedicineName(),
                        medicine.getMedicineTotalNumberOfTakeTimesPerDay()
                );
            }
        });
    }

    public void updateData(List<Medicine> medicines) {
        mMedicinesList = medicines;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mMedicinesList != null) {
            return mMedicinesList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView numberOfDailyTakeTimesTextView;
        private TextView medicineNameTextView;
        private TextView numberOfTakenTimesTodayTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            numberOfDailyTakeTimesTextView = itemView.findViewById(R.id.tv_number_of_daily_times);
            medicineNameTextView = itemView.findViewById(R.id.tv_medicine_name);
            numberOfTakenTimesTodayTextView =
                    itemView.findViewById(R.id.tv_number_of_taken_times_today);
        }
    }

    public interface Resources {
        String getStringResources(int resId);
    }

    public interface ActionListener {
        void onListItemClickListener(int medicineId, String medicineName, int medicineTakeTimes);
    }
}
