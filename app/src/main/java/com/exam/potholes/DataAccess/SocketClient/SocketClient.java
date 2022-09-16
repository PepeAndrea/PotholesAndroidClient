package com.exam.potholes.DataAccess.SocketClient;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.exam.potholes.Model.Pothole;
import com.exam.potholes.R;

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


    public LiveData<List<Pothole>> getFilter(String radius) {
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
}

/*

public static void main(String[] args) {

        String msg = "insertPotholes",result = "1";
        ArrayList<Pot> pots = new ArrayList<>();

        try{
            Socket socket = new Socket("127.0.0.1",8585);

            socket.setSoTimeout(6000);
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

            socket.getOutputStream().write(msg.getBytes());

            Thread.sleep(2000);
            socket.getOutputStream().write("Marcello;sds;dfdf;dfdf".getBytes());

            System.out.println("Attendo lettura");

            while(reader.ready()) {
                result = reader.readLine();
                System.out.println(result);
                //String[] tokens = result.split(";");
                //pots.add(new Pot(tokens[0],tokens[1],tokens[2],tokens[3]));
            }

            socket.close();


            for (Pot pot : pots) {
                System.out.println(pot.toString());
            }


            }catch (Exception e){
                e.printStackTrace();
                }

                }
                }
*/