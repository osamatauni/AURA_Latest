package com.example.fine.auraui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.sql.Time;

public class SplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 4000;
    private Time top_free = null;
   //WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent homeIntent=new Intent(SplashScreen.this, SettingsActivity.class);
                    startActivity(homeIntent);
                    //Intent StartIntent = new Intent(SplashScreen.this, StartActivity.class);
                   // startActivity(StartIntent);
                    finish();
                }

            }, SPLASH_TIME_OUT);
    }


}

