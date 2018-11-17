package com.hfad.mems;
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
import org.w3c.dom.Text;

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

public class MainActivity extends AppCompatActivity {

    private String date;
    private static ArrayList<HashMap<String, Object>> myBooks;
    private static final String FIRST = "firstname";
    private static final String LAST = "lastname";
    private ArrayList<MemePreview> memesPreview;
    private ProgressDialog dialog;
    RecyclerView recyclerView;
    List <String> urls = new ArrayList<>();
    List <String> dates = new ArrayList<>();
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        myBooks = new ArrayList<HashMap<String, Object>>();
        memesPreview = new ArrayList<>();
       LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        new MyAsyncTask().execute();
    }

    class MyAsyncTask extends AsyncTask<String, String, String> {

    String answerHTTP;
    String server = "http://memeswithyou.site/memes/server.php";

    @Override
    protected void onPreExecute() {
        date=(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Загрузка...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        HashMap<String, String> postDataParams = new HashMap<String, String>();
        postDataParams.put("request", "main_screen");
        postDataParams.put("date", date);
        answerHTTP = performGetCall(server, postDataParams);

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        dialog.dismiss();
        super.onPostExecute(result);
        JSONURL(answerHTTP);
        for (int i=0;i<memesPreview.size();i++){
            dates.add(memesPreview.get(i).datePreview);
            urls.add( memesPreview.get(i).nameImgPreview);
        }
        ImageSwitcherAdapter adapter = new ImageSwitcherAdapter(MainActivity.this,urls,dates);
        recyclerView.setAdapter(adapter);
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
                if (urls.getString(i).toString()!="false"){
                HashMap<String, Object> hm;
                hm = new HashMap<String, Object>();
                hm.put(FIRST, urls.getJSONObject(i).getString("link_preview").toString());
                hm.put(LAST, urls.getJSONObject(i).getString("date").toString());
                myBooks.add(hm);
                memesPreview.add(new MemePreview(hm.get(FIRST).toString(),hm.get(LAST).toString()));
            }else {            Log.e("log_tag", "false grabber");}}
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

public class MemePreview {
    String nameImgPreview;
    String datePreview;
    MemePreview(String nameImgPreview,String datePreview){
        this.nameImgPreview = nameImgPreview;
        this.datePreview = datePreview;
    }

}
}