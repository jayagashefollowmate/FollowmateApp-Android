package com.terracotta.followmateapp.Activity;

/**
 * Created by aspl37 on 8/8/16.
 */
public class HelloMultiDexApplication extends com.orm.SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();
        // register with Active Android

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private String someVariable;

    public String getSomeVariable() {
        return someVariable;
    }

    public void setSomeVariable(String someVariable) {
        this.someVariable = someVariable;
    }
}
