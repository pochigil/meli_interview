package com.meli;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import model.DaoMaster;
import model.DaoSession;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    static DaoSession daoSession;
    private static AppController mInstance;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static DaoSession getSession() {
        return daoSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "meli-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

}