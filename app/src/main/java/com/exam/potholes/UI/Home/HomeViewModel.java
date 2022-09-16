package com.exam.potholes.UI.Home;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    public void Hello(Context context){
        Toast.makeText(context,"Ciao",Toast.LENGTH_LONG).show();
    }


}