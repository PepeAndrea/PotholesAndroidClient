package com.exam.potholes.DataAccess.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.exam.potholes.DataAccess.SocketClient.SocketClient;
import com.exam.potholes.Model.Pothole;

import java.util.List;

public class HomeRepository {
    private static HomeRepository homeRepository;
    private SocketClient socketClient;


    public HomeRepository() {
        this.socketClient = SocketClient.getInstance();
    }

    public static HomeRepository getInstance(){
        if(homeRepository == null){
            homeRepository = new HomeRepository();
        }
        return homeRepository;
    }

    public LiveData<List<Pothole>> getPotholes(Context context) {
       return this.socketClient.getAll();
    }

    public LiveData<List<Pothole>> getFilterPotholes(Context context, String radius) {
        return this.socketClient.getFilter(radius);
    }
}
