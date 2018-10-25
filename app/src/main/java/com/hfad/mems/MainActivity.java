package com.hfad.mems;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private String date;
    private ListView listView;
    private TextView textView3;
    public static String JsonURL;
    private static ArrayList<HashMap<String, Object>> myBooks;
    private static final String FIRST = "firstname";
    private static final String LAST = "lastname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.button);
        listView = (ListView)findViewById(R.id.list);
        textView3 = (TextView)findViewById(R.id.textView3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAsyncTask().execute();
            }
        });
    }

    class MyAsyncTask extends AsyncTask<String, String, String> {

        String a, b, answerHTTP;

        String server = "http://memeswithyou.site/memes/server.php";

        @Override
        protected void onPreExecute() {
            date=(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
           // date=LocalDate.now();
            a = "1";
            b = "1";
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
            super.onPostExecute(result);
            try {
                textView3.setText(answerHTTP);
                JSONObject json = new JSONObject(answerHTTP);
                JSONArray urls = json.getJSONArray("response");
                for (int i = 0; i<urls.length();i++){
                    HashMap<String,Object> hm;
                    hm = new HashMap<String, Object>();
                    hm.put(FIRST,urls.getJSONObject(i).getString("link_fullsize").toString());
                    hm.put(LAST, urls.getJSONObject(i).getString("date").toString());
                    myBooks.add(hm);
                    SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, myBooks, R.layout.list,
                            new String[] { FIRST, LAST, }, new int[] { R.id.text1, R.id.text2 });
                    listView.setAdapter(adapter);
                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                }



            } catch (JSONException e) {
                e.printStackTrace();
            }



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
}