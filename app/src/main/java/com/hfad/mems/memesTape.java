package com.hfad.mems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class memesTape extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memes_tape);
        textView = (TextView)findViewById(R.id.test);
        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        textView.setText(date);
    }
}
