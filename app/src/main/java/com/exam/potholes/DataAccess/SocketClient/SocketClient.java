package com.exam.potholes.DataAccess.SocketClient;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.exam.potholes.DataAccess.Repository.AuthRepository;
import com.exam.potholes.Model.Pothole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketClient {

    private static String server_address = "192.168.1.127";
    private static int server_port = 8585;

    private static SocketClient socketClient;
    private MutableLiveData<List<Pothole>> potholesList = new MutableLiveData<>();
    private MutableLiveData<Float> threshold = new MutableLiveData<>(-1f);


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
                resultList.add(new Pothole(tokens[0],Double.valueOf(tokens[1]),Double.valueOf(tokens[2]),Double.valueOf(tokens[3])));
            }

            socket.close();
            potholesList.setValue(resultList);
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
                resultList.add(new Pothole(tokens[0],Double.valueOf(tokens[1]),Double.valueOf(tokens[2]),Double.valueOf(tokens[3])));
            }

            socket.close();
            potholesList.setValue(resultList);
        }catch (Exception e){
            Log.e("Errore comunicazione con il socket","Impossibile dialogare con il socket");
            e.printStackTrace();
        }

        return potholesList;
    }

    public LiveData<Float> getThreshold(Context context){
        String msg = "getThreshold";
        try{
            Socket socket = this.openSocketConnection();
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            socket.getOutputStream().write(msg.getBytes());
            Thread.sleep(2000);

            if(reader.ready()) {
                threshold.setValue(Float.valueOf(reader.readLine()));
            }

            socket.close();

        }catch (Exception e){
            Log.e("Errore comunicazione con il socket","Impossibile dialogare con il socket");
            e.printStackTrace();
        }

        return threshold;
    }

    private String formatRequest(String... args){
        return String.join(";",args);
    }


}