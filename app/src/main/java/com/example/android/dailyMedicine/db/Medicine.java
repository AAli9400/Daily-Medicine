package com.example.android.dailyMedicine.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.example.android.dailyMedicine.util.Constants;

import java.util.Date;

@Entity(tableName = Constants.MEDICINE_TABLE_NAME)
public class Medicine {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_ID")
    private int _ID;

    @ColumnInfo(name = "name")
    private String medicineName;

    @ColumnInfo(name = "total_number_of_take_times_per_day")
    private int medicineTotalNumberOfTakeTimesPerDay;

    @ColumnInfo(name = "total_number_of_taken_times_today")
    private int medicineTotalNumberOfTakenTimesToday;

    @ColumnInfo(name = "insert_date")
    private Date date;

    public Medicine(int _ID, String medicineName, int medicineTotalNumberOfTakeTimesPerDay, int medicineTotalNumberOfTakenTimesToday, Date date) {
        this._ID = _ID;
        this.medicineName = medicineName;
        this.medicineTotalNumberOfTakeTimesPerDay = medicineTotalNumberOfTakeTimesPerDay;
        this.medicineTotalNumberOfTakenTimesToday = medicineTotalNumberOfTakenTimesToday;
        this.date = date;
    }

    @Ignore
    public Medicine(int _ID, String medicineName, int medicineTotalNumberOfTakeTimesPerDay) {
        this._ID = _ID;
        this.medicineName = medicineName;
        this.medicineTotalNumberOfTakeTimesPerDay = medicineTotalNumberOfTakeTimesPerDay;
        medicineTotalNumberOfTakenTimesToday = 0;
        this.date = new Date();
    }

    @Ignore
    public Medicine(String medicineName, int medicineTotalNumberOfTakeTimesPerDay) {
        this.medicineName = medicineName;
        this.medicineTotalNumberOfTakeTimesPerDay = medicineTotalNumberOfTakeTimesPerDay;
        medicineTotalNumberOfTakenTimesToday = 0;
        this.date = new Date();
    }

    @Ignore
    public Medicine() {
        this.date = new Date();
    }

    public int get_ID() {
        return _ID;
    }

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getMedicineTotalNumberOfTakeTimesPerDay() {
        return medicineTotalNumberOfTakeTimesPerDay;
    }

    public void setMedicineTotalNumberOfTakeTimesPerDay(int medicineTotalNumberOfTakeTimesPerDay) {
        this.medicineTotalNumberOfTakeTimesPerDay = medicineTotalNumberOfTakeTimesPerDay;
    }

    public int getMedicineTotalNumberOfTakenTimesToday() {
        return medicineTotalNumberOfTakenTimesToday;
    }

    public void setMedicineTotalNumberOfTakenTimesToday(int medicineTotalNumberOfTakenTimesToday) {
        this.medicineTotalNumberOfTakenTimesToday = medicineTotalNumberOfTakenTimesToday;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getMedicineIdForPendingIntent() {
        return this._ID + 3;
    }
}
