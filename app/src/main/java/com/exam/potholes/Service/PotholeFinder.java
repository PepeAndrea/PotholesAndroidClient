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
    private Context context;
    private double threshold;
    private String nickname;
    private long lastEventTime = System.currentTimeMillis();

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        this.showNotificationAndStartForegroundService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("PotholeFinder", "Service interrotto");
        sensorManager.unregisterListener(this, accelerometer);
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
        stopSelf();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        final float alpha = 0.8f;

        double[] gravity = new double[3];
        gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];
        /*
        double[] linear_acceleration = new double[3];
        linear_acceleration[0] = sensorEvent.values[0] - gravity[0];
        linear_acceleration[1] = sensorEvent.values[1] - gravity[1];
        linear_acceleration[2] = sensorEvent.values[2] - gravity[2];
        */
        double[] linear_acceleration = new double[3];
        linear_acceleration[0] = sensorEvent.values[0];
        linear_acceleration[1] = sensorEvent.values[1];
        linear_acceleration[2] = sensorEvent.values[2];

        double verticalAcc = Math.sqrt(linear_acceleration[0] * linear_acceleration[0] + linear_acceleration[1] * linear_acceleration[1] + linear_acceleration[2] * linear_acceleration[2]);
        //Log.e("VERTICAL", "onSensorChanged: "+verticalAcc);
        if (verticalAcc > this.threshold) {
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
                                Log.i("Accellerazione", String.valueOf(verticalAcc)+"\n\n\n\n");
                                Log.i("Latitudine", String.valueOf(location.getLatitude())+"\n\n\n\n");
                                Log.i("Longitudine", String.valueOf(location.getLongitude())+"\n\n\n\n");
                                Log.i("RIGA SEPARAZIONE", "------------------");

                                Pothole newPothole = new Pothole(nickname,location.getLatitude(),location.getLongitude(),verticalAcc);

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