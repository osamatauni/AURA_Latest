package com.example.fine.auraui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;


public class SettingsActivity extends AppCompatActivity {
    Context context;
    int SELECT_PICTURE = 1;

    TextView messageText;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    String upLoadServerUri = null;
    final String uploadFilePath = "path";
    final String uploadFileName = "name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this.getApplication();

        //Giving Preferences for one time view
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }//end preferences

        ImageView next = (ImageView) findViewById(R.id.image_next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                LinearLayout layout1 = (LinearLayout) findViewById(R.id.linearLayout1);
                LinearLayout layout2 = (LinearLayout) findViewById(R.id.linearLayout2);
                layout1.animate().alpha(0.0f);
                layout2.setVisibility(layout2.VISIBLE);
                layout1.setVisibility(layout1.INVISIBLE);
            }

        });

        ImageView next1 = (ImageView) findViewById(R.id.image_next1);
        next1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                LinearLayout layout1 = (LinearLayout) findViewById(R.id.linearLayout2);
                LinearLayout layout2 = (LinearLayout) findViewById(R.id.linearLayout5);
                layout1.animate().alpha(0.0f);
                layout2.setVisibility(layout2.VISIBLE);
                layout1.setVisibility(layout1.INVISIBLE);
                UserToFile();

                WifiManager wifi = (WifiManager) getSystemService(context.WIFI_SERVICE);

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                if (wifi.isWifiEnabled() == false || (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false))
                {

                    if(wifi.isWifiEnabled() == false && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false))
                    {
                        Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gps);
                        wifi.setWifiEnabled(true);
                    }//end if
                    else
                    {
                        if (wifi.isWifiEnabled() == false)
                        {
                            wifi.setWifiEnabled(true);
                        }//end if
                        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                            Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gps);
                        }//end else if
                    }//end else
                }//end if

            }

        });

        ImageView next2 = (ImageView) findViewById(R.id.image_next2);
        next2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                LinearLayout layout1 = (LinearLayout) findViewById(R.id.linearLayout3);
                LinearLayout layout2 = (LinearLayout) findViewById(R.id.linearLayout4);
                layout1.animate().alpha(0.0f);
                layout2.setVisibility(layout2.VISIBLE);
                layout1.setVisibility(layout1.INVISIBLE);

            }

        });


        ImageView next3 = (ImageView) findViewById(R.id.image_next3);
        next3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                LinearLayout layout1 = (LinearLayout) findViewById(R.id.linearLayout4);
                LinearLayout layout2 = (LinearLayout) findViewById(R.id.linearLayout5);
                layout1.animate().alpha(0.0f);
                layout2.setVisibility(layout2.VISIBLE);
                layout1.setVisibility(layout1.INVISIBLE);

                /*WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                if (wifi.isWifiEnabled() == false || (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false))
                {

                    if(wifi.isWifiEnabled() == false && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false))
                    {
                        Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(gps);
                        wifi.setWifiEnabled(true);
                    }//end if
                    else
                    {
                        if (wifi.isWifiEnabled() == false)
                        {
                            wifi.setWifiEnabled(true);
                        }//end if
                        else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                            Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gps);
                        }//end else if
                    }//end else
                }//end if*/

            }

        });

        ImageView next4 = (ImageView) findViewById(R.id.image_next4);
        next4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent= new Intent(SettingsActivity.this,StartActivity.class);
                startActivity(intent);
                finish();
            }

        });

        //image chooser button
        Button chse_img = (Button) findViewById(R.id.button_img);
        chse_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                Intent camintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                camintent.putExtra("return-data", true);
                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, (new Intent[]{camintent, pickIntent}));
                startActivityForResult(chooserIntent, SELECT_PICTURE);
            }
        });


    }//end oncreate


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imageView9);

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            imageView.setImageBitmap(bmp);
            SaveImage(bmp);
        }
    }
        private Bitmap getBitmapFromUri(Uri uri) throws IOException
        {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        }

    private void SaveImage(Bitmap finalBitmap)
    {

        File root = new File(Environment.getExternalStorageDirectory(),"User_Image");
        if (!root.exists())
        {
            root.mkdirs();
        }//end if
        Random generator = new Random();
        String fname = "User.jpg";
        File file = new File (root, fname);
        if (file.exists ()) file.delete ();
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }//end try
        catch (Exception e)
        {
            e.printStackTrace();
        }//end catch
    }//end SaveImage

    private void UserToFile()
    {
       EditText name = (EditText) findViewById(R.id.TextBox_Name);
        EditText password = (EditText) findViewById(R.id.editText_Password);
        RadioButton male=(RadioButton) findViewById(R.id.radioButton_Male);
        RadioButton female=(RadioButton) findViewById(R.id.radioButton_Female);

        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "User_Data");
            if (!root.exists())
            {
                root.mkdirs();
            }//end if
            File file = new File(root,"user.txt");
            FileWriter writer = new FileWriter(file);
            writer.append(name.getText().toString().toUpperCase());
            writer.append(System.getProperty("line.separator"));
            writer.append(password.getText().toString());
            writer.append(System.getProperty("line.separator"));
            if(male.isChecked())
            {
                writer.append("Male");
            }//end if
            else if (female.isChecked())
            {
                writer.append("Female");
            }//end else
            writer.flush();
            writer.close();
        }//end try
        catch (IOException e)
        {
            e.printStackTrace();
        }//end catch
    }//end UserToFile
}
