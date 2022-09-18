package com.exam.potholes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;

import com.exam.potholes.DataAccess.Repository.AuthRepository;
import com.exam.potholes.UI.Home.HomeFragment;
import com.exam.potholes.UI.Login.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportActionBar().hide();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (savedInstanceState == null) {
            if(!AuthRepository.getInstance().isNicknameSaved(this)){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, LoginFragment.newInstance())
                        .commitNow();
            }else{
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance())
                        .commitNow();
            }

        }
    }

    public void changeView(String view){
        switch (view){
            case "login": getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance())
                    .commitNow();
            case "home" : getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, HomeFragment.newInstance())
                    .commitNow();
        }


    }
}