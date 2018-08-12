package com.example.android.dailyMedicine;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.dailyMedicine.db.Medicine;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private List<Medicine> mMedicinesList;
    private Resources mResourcesCallback;
    private Listener mListenerCallback;

    public MedicineAdapter(List<Medicine> medicinesList, Resources resourcesCallback, Listener listener) {
        this.mMedicinesList = medicinesList;
        this.mResourcesCallback = resourcesCallback;
        this.mListenerCallback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.medicine_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Medicine medicine;
        try {
            medicine = mMedicinesList.get(i);

            viewHolder.numberOfDailyTakeTimesTextView
                    .setText(String.valueOf(medicine.getMedicineTotalNumberOfTakeTimesPerDay()));

            viewHolder.medicineNameTextView.setText(medicine.getMedicineName());

            StringBuilder stringHelper = new StringBuilder(
                    String.valueOf(medicine.getMedicineTotalNumberOfTakenTimesPerDay()) +
                            mResourcesCallback.getStringResources(R.string.out_of) +
                            String.valueOf(medicine.getMedicineTotalNumberOfTakeTimesPerDay()) +
                            mResourcesCallback.getStringResources(R.string.times_today)
            );

            viewHolder.numberOfTakenTimesTodayTextView.setText(stringHelper.toString());

            int medicineTotalNumberOfTakeTimesPerDay =
                    medicine.getMedicineTotalNumberOfTakeTimesPerDay();

            int hour = medicine.getFirstHour();
            int minute = medicine.getFirstMin();
            stringHelper =
                    new StringBuilder(mResourcesCallback.getStringResources(R.string.you_take_this_medicine_at));

            for (int j = 0; j < medicineTotalNumberOfTakeTimesPerDay; j++) {
                hour = (hour +  (24 / medicineTotalNumberOfTakeTimesPerDay)) % 24;
                String ampm = mResourcesCallback.getStringResources(
                        (hour >= 12) ? R.string.pm : R.string.am
                );
                int hourHelper = (hour % 12 == 0) ? 12 : hour % 12;
                String hourString =
                        (String.valueOf(hourHelper).equals("0")) ? "00" : String.valueOf(hourHelper);
                hourString = (hourString.length() == 1) ? "0" + hourString : hourString;

                String minuteString = String.valueOf(minute);
                minuteString = (minuteString.length() == 1) ? "0" + minuteString : minuteString;

                stringHelper.append("\n")
                        .append(hourString)
                        .append(":")
                        .append(minuteString)
                        .append(ampm);
            }
            viewHolder.medicineDetailsTextView.setText(stringHelper.toString());

            viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListenerCallback.onClick(view, medicine.get_ID());
                }
            });
        } catch (IndexOutOfBoundsException e) {
            Log.v("Adapter", e.getMessage());
        }
    }

    public void swapData(List<Medicine> medicines) {
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
        private TextView medicineDetailsTextView;
        private Button editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            numberOfDailyTakeTimesTextView = itemView.findViewById(R.id.tv_number_of_daily_times);
            medicineNameTextView = itemView.findViewById(R.id.tv_medicine_name);
            numberOfTakenTimesTodayTextView =
                    itemView.findViewById(R.id.tv_number_of_taken_times_today);
            medicineDetailsTextView = itemView.findViewById(R.id.tv_medicine_details);
            editButton = itemView.findViewById(R.id.btn_arrow_down);
        }
    }

    public interface Resources {
        String getStringResources(int resId);
    }

    public interface Listener {
        void onClick(View view, int medicineId);
    }
}
