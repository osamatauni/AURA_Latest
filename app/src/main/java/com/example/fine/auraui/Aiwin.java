package com.example.fine.auraui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class Aiwin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aiwin);
        download();
    }

   private  void  download(){}
    {
        final String url = "http://198.46.153.11/file.php";

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String response = null;
                try {
                    response = Jsoup.connect(url).execute().body();
                    JSONObject obj = new JSONObject(response);
                    Log.i("Download", obj.getString("status"));
                    if(obj.getBoolean("status")){
                        String file = obj.getString("file");
                        String file_content = Jsoup.connect(file).ignoreContentType(true).execute().parse().text();
                        Log.i("FIle Content", file_content);
                        String res = Jsoup.connect(url)
                                 .data("update", "downloaded")
                                .method(Connection.Method.POST)
                                .post().body().text();
                        Log.i("Response", res);

                    } else {

                        Log.i("Message", obj.getString("message"));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Dowload", response);
                return null;
            }
        }.execute();

    }

}

