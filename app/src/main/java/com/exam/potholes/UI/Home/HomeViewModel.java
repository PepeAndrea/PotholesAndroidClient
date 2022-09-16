package com.exam.potholes.UI.Home;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.exam.potholes.DataAccess.Repository.HomeRepository;
import com.exam.potholes.Model.Pothole;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private HomeRepository homeRepository;

    public HomeViewModel() {
        this.homeRepository = HomeRepository.getInstance();
    }

    public LiveData<List<Pothole>> getPotholes(Context context) {
       return this.homeRepository.getPotholes(context);
    }

    public LiveData<List<Pothole>> getFilterPotholes(Context context, String radius) {
        return this.homeRepository.getFilterPotholes(context,radius);
    }
}