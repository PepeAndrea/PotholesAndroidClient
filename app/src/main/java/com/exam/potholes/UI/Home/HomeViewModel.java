package com.exam.potholes.UI.Home;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.exam.potholes.DataAccess.Repository.PotholesRepository;
import com.exam.potholes.Model.Pothole;
import com.exam.potholes.R;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                potholesRepository.getThreshold(context);
            }
        }).start();
    }

    public void stopPotholesFinder(Context context){
        context.stopService(new Intent(context, PotholeFinder.class));
        ((TextView)((Activity) context).findViewById(R.id.startStopRecording)).setText("Avvia registrazione");
    }


    public LiveData<List<Pothole>> getObservablePotholes(Context context) {
        return this.potholesRepository.getObservablePotholes(context);
    }

    public void setupSessionRecording(Context context,LifecycleOwner viewLifecycleOwner){
        potholesRepository.getObservableThreshold(context).observe(viewLifecycleOwner, new Observer<Double>() {
            @Override
            public void onChanged(Double threshold) {
                if(threshold == -2.0){
                    new AlertDialog.Builder(context)
                            .setTitle("Server offline")
                            .setMessage("Impossibile ricevere i parametri di soglia dal server")
                            .setNeutralButton("Chiudi",(dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .setOnDismissListener(dialogInterface -> ((Activity)context).findViewById(R.id.startStopRecording).setEnabled(true))
                            .show();
                }else if(threshold != -1.0){
                    Intent service = new Intent(context, PotholeFinder.class);
                    service.putExtra("threshold",threshold);
                    Log.i("Threshold ricevuto dal server", String.valueOf(threshold));
                    context.startService(service);
                    ((TextView)((Activity) context).findViewById(R.id.startStopRecording)).setText("Ferma registrazione");
                    ((Activity)context).findViewById(R.id.startStopRecording).setEnabled(true);
                }
            }
        });
    }
}