package com.exam.potholes.UI.Login;


import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.exam.potholes.DataAccess.Repository.LoginRepository;

public class LoginViewModel extends ViewModel {

    public void login(Context context,String nickname){
        LoginRepository.getInstance().saveNickname(context,nickname);
    }



}