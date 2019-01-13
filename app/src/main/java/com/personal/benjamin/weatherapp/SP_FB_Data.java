package com.personal.benjamin.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SP_FB_Data extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp__fb__data);
        Intent intent = getIntent();
        String summ = intent.getStringExtra("summary");
        String lati = intent.getStringExtra("lati");
        String longi = intent.getStringExtra("longi");
        TextView summary = findViewById(R.id.summaryView);
        TextView latiView = findViewById(R.id.latiView);
        TextView longiView = findViewById(R.id.longiView);
        summary.setText(summ);
        latiView.setText(lati);
        longiView.setText(longi);
    }
}
