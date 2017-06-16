package com.example.fine.auraui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "AURA";
    private ImageButton traffic;
    private LocationManager locationManager;
    LocationListener locationListener;
    private Time top_free = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //registerReceiver(rec,new IntentFilter("SMS_RECEIVED"));

        Intent accessUsageSetting = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(accessUsageSetting, 1);

        final ViewFlipper flipper = (ViewFlipper) findViewById(R.id.Flipper1);
        final ListView listView_Apps= (ListView) findViewById(R.id.Apps_list);
        final ListView listView_Interests= (ListView) findViewById(R.id.Interests_list);

        ImageButton appsPanel = (ImageButton) findViewById(R.id.imageButton_Apps);
        appsPanel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReadCSV("Log_Dataset.csv",listView_Apps);
                flipper.setDisplayedChild(flipper.indexOfChild(findViewById(R.id.Layout_App_Suggestions)));
            }

        });

        ImageButton BacktoIcons = (ImageButton) findViewById(R.id.imageButton_back2icons);
        BacktoIcons.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flipper.setDisplayedChild(flipper.indexOfChild(findViewById(R.id.Layout_Icon_container)));
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

        //Button for safePlace check
        ImageButton chkSafePlace = (ImageButton) findViewById(R.id.imageButton_Places);
        chkSafePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        ImageButton Back2Icons = (ImageButton) findViewById(R.id.imageButton1_back2icons);
        Back2Icons.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flipper.setDisplayedChild(flipper.indexOfChild(findViewById(R.id.Layout_Icon_container)));
            }

        });

        //Button for Interests
        ImageButton interests = (ImageButton) findViewById(R.id.imageButton_Interests);
        interests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ReadCSV("Interests.csv",listView_Interests);
                 //flipper.setDisplayedChild(flipper.indexOfChild(findViewById(R.id.Layout_Interests_Crawled)));

            }
        });

        //Button for Profile
        ImageButton profile = (ImageButton) findViewById(R.id.imageButton_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReadUserData();
                flipper.setDisplayedChild(flipper.indexOfChild(findViewById(R.id.Layout_User_Profile)));
            }
        });

        ImageButton Back3Icons = (ImageButton) findViewById(R.id.imageButton_back3icons);
        Back3Icons.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flipper.setDisplayedChild(flipper.indexOfChild(findViewById(R.id.Layout_Icon_container)));
            }

        });

        //Button for Expenses

        ImageButton expenses = (ImageButton) findViewById(R.id.imageButton_Places);
        expenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPieChart(ReadExpenseData());
                flipper.setDisplayedChild(flipper.indexOfChild(findViewById(R.id.Layout_Expenses)));
            }
        });

        ImageButton Back4Icons = (ImageButton) findViewById(R.id.imageButton2_back4icons);
        Back4Icons.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                flipper.setDisplayedChild(flipper.indexOfChild(findViewById(R.id.Layout_Icon_container)));
            }

        });
    }//end onCreate


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

    private void ReadCSV(String filename, ListView lv)
    {
        File file = new File(getCacheDir() + "/"+filename);
        if (!file.exists())
        {
            try
            {
                InputStream is = getAssets().open(filename);
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

    }//end ReadCSV

    private class SafePlaces extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(StartActivity.this);
            pd.setTitle("Determining Safety");
            pd.setMessage("Please wait...");
            pd.setCancelable(true);
            pd.setIndeterminate(true);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... args){

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            pd.dismiss();
        }
    }//end FindSafePlaces

    //Upload at FreeTimes
    private void UploadWhenFree()
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
                        if (top_free == c.getTime() )
                        {
                            new StartActivity.uploadTask().execute("log.txt");
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
    }//end UploadWhenFree

    //ReadTextFile
    private void ReadUserData()
    {
        File root = new File(Environment.getExternalStorageDirectory(), "User_Data");
        File file = new File(root + "/user.txt");
        File root1 = new File(Environment.getExternalStorageDirectory(), "User_Image");
        File file1 = new File(root1 + "/user.jpg");
        TextView name=(TextView) findViewById(R.id.textView_Name_from_file);
        TextView gender=(TextView) findViewById(R.id.textView_Gender_from_file);
        ImageView user= (ImageView) findViewById(R.id.imageView_user_profile);

        Bitmap myBitmap = BitmapFactory.decodeFile(file1.getAbsolutePath());

        if (file.exists())
        {
            try
            {
                FileReader in=new FileReader(file);
                BufferedReader reader = new BufferedReader(in);
                String line;
                ArrayList<String> userdata = new ArrayList<String>();
                while ((line = reader.readLine()) != null) {
                    userdata.add(line);
                }//end while
                reader.close();
                name.setText(userdata.get(0).toString());
                gender.setText(userdata.get(2).toString());
                user.setImageBitmap(myBitmap);

            }//end try
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }//end catch
        }//end if
    }//end ReadUserData

    private int [] ReadExpenseData()
    {
        //File root = new File(Environment.getExternalStorageDirectory(), "Expenses");
        //File file = new File(root + "/expenses.txt");
        File file = new File(getCacheDir() + "/expenses.txt");
        ArrayList<String> date = new ArrayList<String>();
        ArrayList<String> expense=new ArrayList<String>();
        int exps[]=new int[12];

        TextView expRes=(TextView) findViewById(R.id.textViewFinalExpRes);

        if (!file.exists())
        {
            try
            {
                //FileReader in=new FileReader(file);
                InputStream is = getAssets().open("expenses.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;

                while ((line = reader.readLine()) != null) {
                    String [] arr=line.split(",");
                    date.add(arr[0]);
                    expense.add(arr[1]);
                }//end while
                reader.close();

            }//end try
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }//end catch


            int sum=0;
            for(int i=1;i<=12;i++)
            {
                sum=0;
                for(int j=0; j<date.size();j++)
                {
                    String [] arr1=date.get(j).toString().split("-");
                    if(Integer.parseInt(arr1[1])==i)
                    {
                        sum=sum+Integer.parseInt(expense.get(j).toString());

                    }

                }
                exps[i-1]=sum;
                Log.i("Expense Array:",String.valueOf(exps[i-1]));
            }//end for

        }//end if

        String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        String dateArr[]=currentDate.split("-");
        int past_sum=0;
        int current_month=Integer.parseInt(dateArr[1]);
        Log.i("Current Month:",String.valueOf(current_month));
        current_month=current_month-1;

        for(int k=current_month-1; k>=0;k-- )
        {
            past_sum=past_sum+exps[k];
        }//end for
        Log.i("Final Past_sum: ",String.valueOf(past_sum));

        if(past_sum>exps[current_month])
        {
            expRes.setText("Your expenses have decreased this month");
        }//end if
        else if(past_sum<exps[current_month])
        {
            expRes.setText("Your expenses have increased this month");
        }//end else
        return exps;
    }//end ReadExpenseData

    private void setupPieChart(int arr[])
    {
        String monthNames[]={"Jan","Feb","March","April","May","June","July","August","September","October","November","December"};
        List<PieEntry> pieEntries=new ArrayList<>();
        for (int i=0; i<arr.length;i++)
        {
            pieEntries.add(new PieEntry(arr[i], monthNames[i]));
        }//end for

        PieDataSet dataSet=new PieDataSet(pieEntries, "Monthly Expense Comparison");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data=new PieData(dataSet);

        PieChart chart=(PieChart) findViewById(R.id.expChart);
        chart.setData(data);
        chart.animateY(5000);
        chart.invalidate();
    }//end setupPieChart()

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
