package com.hfad.mems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class memesTape extends AppCompatActivity {
    String date;
    private ProgressDialog dialog;
    private ArrayList<Meme> memes;
    private static final String FIRST = "firstname";
    private static final String LAST = "lastname";
    RecyclerView recyclerView;
    List <String> urls = new ArrayList<>();
    private static ArrayList<HashMap<String, Object>> myBooks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memes_tape);
        myBooks = new ArrayList<HashMap<String, Object>>();
        memes = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerForTape);
        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        TextView textView = (TextView) findViewById(R.id.textForTape);
        textView.setText(date);
        Log.e("LIVFF", "fr0 " + urls.size());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        new MySecondAsyncTask().execute();

    }
    class MySecondAsyncTask extends AsyncTask<String,String,String>{
        String answerHTTP;
        String server = "http://memeswithyou.site/memes/server.php";
        @Override
        protected void onPreExecute(){
            dialog = new ProgressDialog(memesTape.this);
            dialog.setMessage("Загрузка...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HashMap<String, String> postDataParams = new HashMap<String, String>();
            postDataParams.put("request", "meme_screen");
            postDataParams.put("date", date);
            answerHTTP = performGetCall(server, postDataParams);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            super.onPostExecute(result);
            JSONURL(answerHTTP);
            for (int i=0;i<memes.size();i++){
                urls.add( memes.get(i).nameImg);
            }
            Log.e("LIVFF", "fr " + urls.size());
            ImageSwitcherAdapterForTape adapterForTape = new ImageSwitcherAdapterForTape(memesTape.this,urls);
            recyclerView.setAdapter(adapterForTape);
        }
    }
    public String performGetCall(String requestURL,
                                 HashMap<String, String> getDataParams) {
        String response = "";

        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(requestURL + "?" + getDataString(getDataParams));
            urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = urlConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                response = convertInputStreamToString(urlConnection.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }
    private String getDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    private String convertInputStreamToString(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return response.toString();
    }
    public void JSONURL(String result) {

        try {
            JSONObject json = new JSONObject(result);
            JSONArray urls = json.getJSONArray("response");
            for (int i = 0; i < urls.length(); i++) {
                HashMap<String, Object> hm;
                hm = new HashMap<String, Object>();
                hm.put(FIRST, urls.getJSONObject(i).getString("link_fullsize").toString());
                hm.put(LAST, urls.getJSONObject(i).getString("date").toString());
                myBooks.add(hm);
                memes.add(new Meme(hm.get(FIRST).toString(),hm.get(LAST).toString()));
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }
    public class Meme{
        String nameImg;
        String date;
        Meme(String nameImg,String date){
            this.nameImg = nameImg;
            this.date = date;
        }

    }
}