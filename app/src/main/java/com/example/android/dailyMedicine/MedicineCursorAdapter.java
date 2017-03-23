package com.example.android.dailyMedicine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.dailyMedicine.data.MedicineContract.MedicineEntry;

/**
 * Created by Abdel-Rhman on 3/7/2017.
 */

public class MedicineCursorAdapter extends CursorAdapter {



    public MedicineCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // If the view was just created, inflate list_medicine layout to it.
        return LayoutInflater.from(context).inflate(R.layout.list_medicine, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // If the view exists, bind it.

        TextView medicineNameTextView = (TextView) view.findViewById(R.id.medicine_name);
        TextView takenTimesTextView = (TextView) view.findViewById(R.id.taken_times);

        int medicineNameColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_NAME);
        int takeTimesColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_TAKE_TIMES);
        int takenTimesColumnIndex = cursor.getColumnIndex(MedicineEntry.COLUMN_MEDICINE_TAKEN_TIMES);

        String MedicineName = cursor.getString(medicineNameColumnIndex);
        int takeTimes = cursor.getInt(takeTimesColumnIndex);
        int takenTimes = cursor.getInt(takenTimesColumnIndex);

        medicineNameTextView.setText(MedicineName);
        takenTimesTextView.setText(String.valueOf(takenTimes) + context.getResources().getString(R.string.out_of) + String.valueOf(takeTimes) + context.getResources().getString(R.string.times_today));
    }


}
