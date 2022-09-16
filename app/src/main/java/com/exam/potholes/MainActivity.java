package com.exam.potholes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.exam.potholes.DataAccess.Repository.LoginRepository;
import com.exam.potholes.UI.Home.HomeFragment;
import com.exam.potholes.UI.Login.LoginFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportActionBar().hide();
        if (savedInstanceState == null) {
            if(!LoginRepository.getInstance().isNicknameSaved(this)){
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