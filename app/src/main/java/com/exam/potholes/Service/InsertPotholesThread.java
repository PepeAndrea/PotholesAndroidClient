package com.exam.potholes.Service;

import android.content.Context;

import com.exam.potholes.DataAccess.Repository.PotholesRepository;
import com.exam.potholes.Model.Pothole;

public class InsertPotholesThread implements Runnable{

    Pothole pothole;

    public InsertPotholesThread(Pothole pothole) {
        this.pothole = pothole;
    }

    @Override
    public void run() {
        PotholesRepository repo = new PotholesRepository();
        repo.insertPothole(this.pothole);
    }
}
