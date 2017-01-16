package com.example.fine.auraui;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class HomeActivity extends AppCompatActivity {
    Context context;
    int SELECT_PICTURE = 1;

    TextView messageText;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    String upLoadServerUri = null;
    final String uploadFilePath = "path";
    final String uploadFileName = "name";
    double longi=31.601144;
    double lat=73.036364;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this.getApplication();

        upLoadServerUri = "http://198.46.153.11/upload.php";

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
                LinearLayout layout2 = (LinearLayout) findViewById(R.id.linearLayout3);
                layout1.animate().alpha(0.0f);
                layout2.setVisibility(layout2.VISIBLE);
                layout1.setVisibility(layout1.INVISIBLE);

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

                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

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

        ImageView next4 = (ImageView) findViewById(R.id.image_next4);
        next4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                LinearLayout layout1 = (LinearLayout) findViewById(R.id.linearLayout5);
                LinearLayout layout2 = (LinearLayout) findViewById(R.id.linearLayout6);
                layout1.animate().alpha(0.0f);
                layout2.setVisibility(layout2.VISIBLE);
                layout1.setVisibility(layout1.INVISIBLE);
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

        //Button to get traffic
        Button traffic = (Button) findViewById(R.id.button_traffic);
        traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  openlocdetails(longi,lat);
            }
        });

        //Button to upload file on server
        Button upload = (Button) findViewById(R.id.button_server);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String filename = "test.txt";
                new uploadTask().execute(filename);

            }
        });
    }

    private class uploadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            upload(strings[0]);
            return null;
        }
    }//end uploadTask

    //Function to upload file
    private void upload(String filename) {
        String url = "http://198.46.153.11/upload.php";


        File file = new File(getCacheDir()+"/"+filename);
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

        }
    }
        private Bitmap getBitmapFromUri(Uri uri) throws IOException {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        }

    //Function to get traffic details and navigation
    private void openlocdetails(double longitude, double latitude)
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
            String url="http://maps.google.com/maps?q=loc:"+longitude+","+latitude+"&z=300&layer=t";
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        }
        catch ( ActivityNotFoundException ex  )
        {
            Intent intent =
                    new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
            startActivity(intent);
        }
    }//end openlocdetails

    //Method2: upload file on server
    public int uploadFile(String sourceFileUri)
    {
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        /*if (!sourceFile.isFile())
        {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    messageText.setText("Source File not exist :" +uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        }//end if
        else
        {*/
            try
            {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200)
                {

                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"+" serverpath"
                                    +uploadFileName;

                            messageText.setText(msg);
                            Toast.makeText(HomeActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            }
            catch (MalformedURLException ex)
            {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(HomeActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            }
            catch (Exception e)
            {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(HomeActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                //Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

       // }//end else
    }


}
