package com.exam.potholes.UI.Home;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.exam.potholes.DataAccess.Repository.PotholesRepository;
import com.exam.potholes.Model.Pothole;
import com.exam.potholes.Service.PotholeFinder;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private PotholesRepository potholesRepository;

    public HomeViewModel() {
        this.potholesRepository = PotholesRepository.getInstance();
    }

    public LiveData<List<Pothole>> getPotholes(Context context) {
       return this.potholesRepository.getPotholes(context);
    }

    public LiveData<List<Pothole>> getFilterPotholes(Context context, String radius, Double latitude,Double longitude) {
        return this.potholesRepository.getFilterPotholes(context,radius,latitude,longitude);
    }

    public boolean isServiceRunning(Context context){
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals("com.exam.potholes.Service.PotholeFinder")){
                Log.i("Service PotholeFinder","Il service è attivo");
                return true;
            }
        }
        Log.i("PotholeFinder","Il service è disattivato");

        return false;
    }

    public void startPotholesFinder(Context context){
        context.startService(new Intent(context, PotholeFinder.class));
    }

    public void stopPotholesFinder(Context context){
        context.stopService(new Intent(context, PotholeFinder.class));
    }


}