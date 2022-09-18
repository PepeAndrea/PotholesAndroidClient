package com.exam.potholes.UI.Login;


import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.exam.potholes.DataAccess.Repository.AuthRepository;

public class LoginViewModel extends ViewModel {

    public void login(Context context,String nickname){
        AuthRepository.getInstance().saveNickname(context,nickname);
    }



}