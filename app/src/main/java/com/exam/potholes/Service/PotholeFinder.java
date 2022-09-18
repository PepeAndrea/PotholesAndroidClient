package com.exam.potholes.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.exam.potholes.BuildConfig;
import com.exam.potholes.R;
import com.google.android.gms.location.FusedLocationProviderClient;

public class PotholeFinder extends Service implements SensorEventListener{

    private FusedLocationProviderClient fusedLocationClient;
    private SensorManager sensorManager;
    private Sensor accellerometer;

    public PotholeFinder() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("PotholeFinder","Service partito");
        super.onStartCommand(intent, flags, startId);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accellerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accellerometer , SensorManager.SENSOR_DELAY_NORMAL);

        this.showNotificationAndStartForegroundService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("PotholeFinder","Service interrotto");
        sensorManager.unregisterListener(this,accellerometer);
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
        sensorManager.unregisterListener(this,accellerometer);
        stopSelf();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i("SENSOR1", String.valueOf(sensorEvent.values[0]));
        Log.i("SENSOR2", String.valueOf(sensorEvent.values[1]));
        Log.i("SENSOR3", String.valueOf(sensorEvent.values[2]));
        Log.i("SENSOR", "\n\n\n");

        final float alpha = 0.8f;

        //@AndroidDeveloper
        double[] gravity = new double[3];
        gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

        double[] linear_acceleration = new double[3];
        linear_acceleration[0] = sensorEvent.values[0] - gravity[0];
        linear_acceleration[1] = sensorEvent.values[1] - gravity[1];
        linear_acceleration[2] = sensorEvent.values[2] - gravity[2];

        Log.i("ACC", String.valueOf(Math.abs(linear_acceleration[1]))+"\n\n\n\n");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}