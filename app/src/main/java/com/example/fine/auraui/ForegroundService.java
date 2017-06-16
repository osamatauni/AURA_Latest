package com.example.fine.auraui;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ForegroundService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    public static int FOREGROUND_SERVICE = 101;
    File file;
    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private Boolean flag = false;

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(rec,new IntentFilter("SMS_RECEIVED"));
    }

    BroadcastReceiver rec=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Main Activity:","Beta Pattern Matched");
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, StartActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        RemoteViews notificationView = new RemoteViews(this.getPackageName(),R.layout.suggestions);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.widget);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("AURA")
                .setTicker("AURA")
                .setContentText("AURA")
                .setSmallIcon(R.drawable.widget)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setOngoing(true).setCustomBigContentView(notificationView).setPriority(Notification.PRIORITY_MAX).build();

        startForeground(FOREGROUND_SERVICE, notification);
        //Start-Coding here
        createTextFile();
        locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location_log();

        Thread applog = new Thread(){
            public void run(){
                app_log();
            }
        };

        applog.start();


        return START_STICKY;
    }

    void app_log(){

        BufferedWriter fOut = null;
        try {
            fOut = new BufferedWriter(new FileWriter(file.getAbsolutePath(),true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("AURA","File is created");

        //Retrieving App Information

        while(true) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());


            String topPackageName = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                // We get usage stats for the last 10 seconds
                List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000, time);

                // Sort the stats by the last time used
                if (stats != null) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : stats) {

                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);

                    }
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {

                        topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                        try {
                            if (!topPackageName.isEmpty()) {
                                fOut.write(currentDateTime + " " + topPackageName);
                                fOut.newLine();
                                fOut.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void Location_log(){

        Log.d("AURA","Into the Location Log");
        locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(locationMangaer.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationMangaer.requestLocationUpdates(LocationManager
                    .GPS_PROVIDER, 300000,20, locationListener);
            Log.d("AURA","Location by GPS Provider");
        }
        else if(locationMangaer.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationMangaer.requestLocationUpdates(LocationManager
                    .NETWORK_PROVIDER,300000,20,locationListener);
            Log.d("AURA","Location by Network Provider");
        }
        else{
            Log.d("AURA","Location not available!");
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    void createTextFile(){

        File root = new File(Environment.getExternalStorageDirectory(), "Logs");
        // if external memory exists and folder with name Notes
        if (!root.exists()) {
            root.mkdirs(); // this will create folder.
        }
        file = new File(root, "log.txt");  // file path to save

    }


    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {

            BufferedWriter bufferedWriter = null;

            try {
                bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath(),true));
            } catch (IOException e) {
                e.printStackTrace();
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date());


            String longitude = "Longitude: " +loc.getLongitude();
            Log.v("AURA", longitude);
            String latitude = "Latitude: " +loc.getLatitude();
            Log.v("AURA", latitude);

            try {
                bufferedWriter.write(currentDateTime+" Longitude: "+loc.getLongitude()+" Latitude: "+loc.getLatitude());
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub


        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}