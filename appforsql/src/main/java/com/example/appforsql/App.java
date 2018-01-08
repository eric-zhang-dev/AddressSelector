package com.example.appforsql;

import android.app.Application;

import com.example.appforsql.db.DBManager;


public class App extends Application {
    private static App app;

    public static App getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
//        new Thread(init).start();
    }

    private Runnable init = new Runnable() {

        @Override
        public void run() {
            DBManager.copyDataBase();
        }
    };
}
