package com.exam.potholes.DataAccess.Repository;

public class HomeRepository {
    private static HomeRepository homeRepository;


    public HomeRepository() {
    }

    public static HomeRepository getInstance(){
        if(homeRepository == null){
            homeRepository = new HomeRepository();
        }
        return homeRepository;
    }


}
