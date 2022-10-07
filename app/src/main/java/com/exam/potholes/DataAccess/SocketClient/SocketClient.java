package com.exam.potholes.DataAccess.SocketClient;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.exam.potholes.DataAccess.Repository.AuthRepository;
import com.exam.potholes.Model.Pothole;
import com.exam.potholes.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketClient {

    private static String server_address = "4.231.56.238";
    private static int server_port = 8585;

    private static SocketClient socketClient;
    private MutableLiveData<List<Pothole>> potholesList = new MutableLiveData<>();
    private MutableLiveData<Double> threshold = new MutableLiveData<>(-1d);


    public static SocketClient getInstance(){
        if(socketClient == null){
            socketClient = new SocketClient();
        }
        return socketClient;
    }

    private Socket openSocketConnection(){
        Socket socket = null;
        try {
            socket = new Socket(server_address,server_port);
            socket.setSoTimeout(6000);
            Log.i("Socket creata","Socket connessa correttamente");
            return socket;
        } catch (IOException e) {
            Log.e("Errore Socket","Non è stato possibile stabilire una connessione con la socket");
            e.printStackTrace();
        }
        return socket;
    }

    public LiveData<List<Pothole>> getAll(){
        String msg = "getAllPotholes",result;
        List<Pothole> resultList = new ArrayList<>();
        try{
            Socket socket = this.openSocketConnection();
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            socket.getOutputStream().write(msg.getBytes());
            Thread.sleep(2000);

            while(reader.ready()) {
                result = reader.readLine();
                String[] tokens = result.split(";");
                resultList.add(0,new Pothole(tokens[0],Double.valueOf(tokens[1]),Double.valueOf(tokens[2]),Double.valueOf(tokens[3])));
            }

            socket.close();
            potholesList.postValue(resultList);
        }catch (Exception e){
            Log.e("Errore comunicazione con il socket","Impossibile dialogare con il socket");
            e.printStackTrace();
        }

        return potholesList;
    }

    public LiveData<List<Pothole>> getFilter(Context context, String radius, Double latitude, Double longitude) {
        String msg = "getNearPotholes",result;
        List<Pothole> resultList = new ArrayList<>();
        try{
            Socket socket = this.openSocketConnection();
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            socket.getOutputStream().write(msg.getBytes());
            Thread.sleep(2000);

            String request = this.formatRequest(AuthRepository.getInstance().getSavedNickname(context),
                                                String.valueOf(latitude),String.valueOf(longitude),radius);
            socket.getOutputStream().write(request.getBytes());
            Thread.sleep(2000);

            while(reader.ready()) {
                result = reader.readLine();
                String[] tokens = result.split(";");
                resultList.add(0,new Pothole(tokens[0],Double.valueOf(tokens[1]),Double.valueOf(tokens[2]),Double.valueOf(tokens[3])));
            }

            socket.close();
            potholesList.postValue(resultList);
        }catch (Exception e){
            Log.e("Errore comunicazione con il socket","Impossibile dialogare con il socket");
            e.printStackTrace();
        }

        return potholesList;
    }

    public LiveData<Double> getThreshold(Context context){
        String msg = "getThreshold";
        try{
            Socket socket = this.openSocketConnection();
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            socket.getOutputStream().write(msg.getBytes());
            Thread.sleep(2000);

            if(reader.ready()) {
                threshold.postValue(Double.valueOf(reader.readLine()));
            }

            socket.close();

        }catch (Exception e){
            Log.e("Errore comunicazione con il socket","Impossibile dialogare con il socket");
            e.printStackTrace();
            threshold.postValue(-2d);
        }

        return threshold;
    }

    public void insertPothole(Pothole pothole){
        String msg = "insertPotholes",result;
        List<Pothole> resultList = new ArrayList<>();
        try{
            Socket socket = this.openSocketConnection();
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            socket.getOutputStream().write(msg.getBytes());
            Thread.sleep(2000);

            String request = this.formatRequest(pothole.getUsername(),String.valueOf(pothole.getLatitude()),String.valueOf(pothole.getLongitude()),String.valueOf(pothole.getVariation()));
            socket.getOutputStream().write(request.getBytes());
            Thread.sleep(2000);

            if(reader.ready()) {
                String res = reader.readLine();
                if(res == "ERROR\n")
                    Log.e("Errore inserimento", "Non è stato possibile inserire il record nel database");
            }

            socket.close();
        }catch (Exception e){
            Log.e("Errore comunicazione con il socket","Impossibile dialogare con il socket");
            e.printStackTrace();
        }
    }

    private String formatRequest(String... args){
        return String.join(";",args);
    }


    public LiveData<List<Pothole>> getObservablePotholes() {
        return potholesList;
    }

    public LiveData<Double> getObservableThreshold() {
        return this.threshold;
    }
}