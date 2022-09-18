package com.exam.potholes.DataAccess.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.exam.potholes.DataAccess.SocketClient.SocketClient;
import com.exam.potholes.Model.Pothole;

import java.util.List;

public class PotholesRepository {
    private static PotholesRepository potholesRepository;
    private SocketClient socketClient;


    public PotholesRepository() {
        this.socketClient = SocketClient.getInstance();
    }

    public static PotholesRepository getInstance(){
        if(potholesRepository == null){
            potholesRepository = new PotholesRepository();
        }
        return potholesRepository;
    }

    public LiveData<List<Pothole>> getPotholes(Context context) {
       return this.socketClient.getAll();
    }

    public LiveData<List<Pothole>> getFilterPotholes(Context context, String radius, Double latitude,Double longitude) {
        return this.socketClient.getFilter(context,radius,latitude,longitude);
    }
}
