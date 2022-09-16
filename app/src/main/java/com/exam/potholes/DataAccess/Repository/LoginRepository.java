package com.exam.potholes.DataAccess.Repository;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginRepository {
    private static LoginRepository loginRepository;


    public LoginRepository() {
    }

    public static LoginRepository getInstance(){
        if(loginRepository == null){
            loginRepository = new LoginRepository();
        }
        return loginRepository;
    }

    public void saveNickname(Context context,String nickname){
        SharedPreferences sharedPreferences = context.getSharedPreferences("app",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nickname",nickname);
        editor.apply();
    }

    public String getSavedNickname(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        return sharedPreferences.getString("nickname","NULL");
    }
    public boolean isNicknameSaved(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        return sharedPreferences.contains("nickname");
    }


}
