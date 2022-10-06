package com.exam.potholes.Service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.exam.potholes.BuildConfig;
import com.exam.potholes.DataAccess.Repository.AuthRepository;
import com.exam.potholes.DataAccess.Repository.PotholesRepository;
import com.exam.potholes.Model.Pothole;
import com.exam.potholes.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

public class PotholeFinder extends Service implements SensorEventListener {

    private FusedLocationProviderClient fusedLocationClient;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private Context context;
    private double threshold;
    private String nickname;
    private long lastEventTime = System.currentTimeMillis();

    double Yaccel = 0;
    double Xaccel = 0;
    double Zaccel = 0;
    double gravityY = 0;
    double gravityX = 0;
    double gravityZ = 0;

    public PotholeFinder() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("PotholeFinder", "Service partito");
        super.onStartCommand(intent, flags, startId);
        this.context = this;
        this.threshold = intent.getDoubleExtra("threshold",-1);
        this.nickname = AuthRepository.getInstance().getSavedNickname(context);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //TODO Da valutare
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        this.showNotificationAndStartForegroundService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("PotholeFinder", "Service interrotto");
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, gravity);
    }

    private void showNotificationAndStartForegroundService() {

        final String CHANNEL_ID = BuildConfig.APPLICATION_ID.concat("_notification_id");
        final String CHANNEL_NAME = BuildConfig.APPLICATION_ID.concat("_notification_name");
        final int NOTIFICATION_ID = 100;

        NotificationCompat.Builder builder;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_NONE;
            assert notificationManager != null;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
        }
        builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name));
        startForeground(NOTIFICATION_ID, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopForeground(true);
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, gravity);
        stopSelf();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;
        double verticalAccel;
        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            Xaccel = (double) sensorEvent.values[0];
            Yaccel = (double) sensorEvent.values[1];
            Zaccel = (double) sensorEvent.values[2];
        }

        if (sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravityX = (double) sensorEvent.values[0];
            gravityY = (double) sensorEvent.values[1];
            gravityZ = (double) sensorEvent.values[2];
        }

        /*
        double scalarProduct = gravityX * Xaccel + gravityY * Yaccel + gravityZ * Zaccel;
        Double gravityVectorLength = Math.sqrt(gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ);
        Double linearAccVectorLength = Math.sqrt(Xaccel * Xaccel + Yaccel * Yaccel + Zaccel * Zaccel);
        double cosVectorAngle = scalarProduct / (gravityVectorLength * linearAccVectorLength);
        */

        verticalAccel = (Xaccel * gravityX / 9.8) + (Yaccel * gravityY / 9.8) +  (Zaccel *gravityZ /9.8);

        if (Math.abs(verticalAccel) > this.threshold) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            long newEvent = System.currentTimeMillis();
            long passedSecond = (newEvent - lastEventTime)/1000;
            //Log.e("TEMPO",lastEventTime+"--"+newEvent+"--"+passedSecond);
            if (passedSecond < 2){
                return;
            }

            lastEventTime = newEvent;

            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,null)
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.e("LOCATION", "LOCATION OTTENUTA");
                            if (location != null) {
                                Log.i("Accellerazione", String.valueOf(Math.abs(verticalAccel))+"\n\n\n\n");
                                Log.i("Latitudine", String.valueOf(location.getLatitude())+"\n\n\n\n");
                                Log.i("Longitudine", String.valueOf(location.getLongitude())+"\n\n\n\n");
                                Log.i("RIGA SEPARAZIONE", "------------------");

                                Pothole newPothole = new Pothole(nickname,location.getLatitude(),location.getLongitude(),Math.abs(verticalAccel));

                                Thread newThread = new Thread(new InsertPotholesThread(newPothole));
                                newThread.start();
                            }
                        }
                    });

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}