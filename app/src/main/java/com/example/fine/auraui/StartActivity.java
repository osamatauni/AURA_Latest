package com.example.fine.auraui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ViewFlipper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "AURA";
    private ImageButton traffic;
    private LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Intent accessUsageSetting = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(accessUsageSetting, 1);

        final ViewFlipper flipper = (ViewFlipper) findViewById(R.id.Flipper1);
        final ListView listView= (ListView) findViewById(R.id.Apps_list);

        ImageButton appsPanel = (ImageButton) findViewById(R.id.imageButton_Apps);
        appsPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReadCSVUpload(listView);
                flipper.showNext();
            }

        });

        ImageButton BacktoIcons = (ImageButton) findViewById(R.id.imageButton_back2icons);
        BacktoIcons.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flipper.showPrevious();
            }

        });

        //Button to upload file on server
        ImageButton upload = (ImageButton) findViewById(R.id.imageButton_cloud);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String filename = "log.txt";
                new StartActivity.uploadTask().execute(filename);
            }
        });

        //Button to get traffic
        traffic = (ImageButton) findViewById(R.id.imageButton_Traffic);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                openlocdetails(location.getLatitude(),location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };//end location listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                }, 10);
                return;
            }//end if
        } else {
            openlocdetailsButton();
        }//end else

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openlocdetailsButton();
                return;
        }
    }

    private void openlocdetailsButton() {
        traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates("gps", 10000, 20, locationListener);
            }
        });
    }//end openlocdetailsButton

    private class uploadTask extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(StartActivity.this);
            pd.setTitle("Uploading Log");
            pd.setMessage("Please wait, upload in progress");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            upload(strings[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pd.dismiss();
        }
    }//end uploadTask
    //Function to upload file
    private void upload(String filename) {
        String url = "http://198.46.153.11/upload.php";
        File file = new File(Environment.getExternalStorageDirectory()+"/Logs/"+filename);
        if (!file.exists()) try {

            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { throw new RuntimeException(e); }


        Connection.Response response;
        try {
            response = Jsoup.connect(url)
                    .method(Connection.Method.POST)
                    .data("uploaded_file", file.getName(), new FileInputStream(file))
                    .execute();

            Log.i("UploadTest", "Status: " + response.statusCode() + " - " + response.statusMessage());
            Log.i("UploadTest", response.parse().text());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//end file upload function

    private void openlocdetails(double longitude,double latitude)
    {
        try
        {

                //GoogleMap.setTrafficEnabled(true);
                //String url = "https://www.waze.com/livemap";
                //String url="waze://?ll="+longitude+", "+latitude+"&z=20";//+"&navigate=yes";
                //String url="geo: 28.375144,-81.549033";
                // String url = "waze://?ll=40.761043,-73.980545&navigate=yes";
                //String url = "waze://?q=Hawaii";
                //Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
                //startActivity(intent);
                //Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                //startActivity( intent );
                //String url = "http://maps.google.com/maps?f=d&daddr="+ destinationLatitude+","+destinationLongitude+"&dirflg=d&layer=t";

            String url = "http://maps.google.com/maps?q=loc:" + longitude + "," + latitude + "&z=300&layer=t";
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        }
        catch ( ActivityNotFoundException ex  )
        {
            Log.d("Traffic","Error:"+ex);
        }
    }//end openlocdetails

    private void ReadCSVUpload(ListView lv)
    {
        File file = new File(getCacheDir() + "/FreeTimes.csv");
        if (!file.exists())
        {
            try
            {
                InputStream is = getAssets().open("Log_Dataset.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;
                ArrayList<String> lines = new ArrayList<String>();
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }//end while
               reader.close();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lines);
                lv.setAdapter(adapter);
            }//end try
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }//end catch
        }//end if
    }//end ReadCSVUpload


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("AURA","In onActivityResult");
        Log.d("AURA","requestcode: "+requestCode+"  resultCode:"+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),2);
        }//end if
        if (requestCode == 2)
        {
            startService(new Intent(StartActivity.this,ForegroundService.class));
        }//end if
    }//end onActivityResult

}
