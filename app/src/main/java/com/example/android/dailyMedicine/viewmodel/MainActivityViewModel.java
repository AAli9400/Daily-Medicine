package com.example.android.dailyMedicine.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.dailyMedicine.db.AppDatabase;
import com.example.android.dailyMedicine.db.MedicineDao;
import com.example.android.dailyMedicine.model.Medicine;
import com.example.android.dailyMedicine.repository.MedicineRepository;
import com.example.android.dailyMedicine.util.MedicineAlarmUtil;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    private LiveData<List<Medicine>> medicines;
    private MedicineRepository repository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        MedicineDao dao = AppDatabase.getInstance(application).medicineDao();
        MedicineAlarmUtil alarmUtil = MedicineAlarmUtil.getInstance(application);
        repository = MedicineRepository.getInstance(dao, alarmUtil);
        medicines = repository.loadAllMedicines();
    }

    public LiveData<List<Medicine>> getMedicines() {
        if (medicines == null) {
            medicines = repository.loadAllMedicines();
        }

        return medicines;
    }

    public void resetTakenTimes() {
        List<Medicine> medicines = this.medicines.getValue();
        if (medicines != null) {
            for (Medicine medicine : medicines) {
                medicine.setTakenTimes(0);
                repository.updateMedicine(medicine);
            }
        }
    }
}
