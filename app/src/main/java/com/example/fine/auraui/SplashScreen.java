package com.example.fine.auraui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.Calendar;

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
                    //Intent homeIntent=new Intent(SplashScreen.this, SettingsActivity.class);
                    //startActivity(homeIntent);
                    Intent StartIntent = new Intent(SplashScreen.this, StartActivity.class);
                    startActivity(StartIntent);
                    finish();
                }

            }, SPLASH_TIME_OUT);
    }

    //Upload CSV &Upload FIle
    private void ReadCSVUpload()
    {
                File file = new File(getCacheDir() + "/FreeTimes.csv");
                if (!file.exists())
                {
                    try
                    {
                        InputStream is = getAssets().open("FreeTimes.csv");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        String line = reader.readLine();
                        String str;
                        Calendar c = Calendar.getInstance();

                        while (line != null) {
                            Log.d("Free Times from Server:", "Top Time:" + top_free);
                            top_free = Time.valueOf(line);
                            while (top_free != c.getTime()) {
                                if (top_free == c.getTime()) //&& wifi.isWifiEnabled() == true)
                                {
                                    //upload(log.txt)
                                }//end if
                                else
                                {
                                    line = reader.readLine();
                                    top_free = Time.valueOf(line);
                                }//end else
                            }//end while2
                        }//end while
                    }//end try
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }//end catch
                }//end if
            }//end ReadCSVUpload

}

