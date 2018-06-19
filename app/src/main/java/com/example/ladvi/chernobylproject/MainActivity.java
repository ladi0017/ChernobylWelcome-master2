package com.example.ladvi.chernobylproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton onButton, offButton, lButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onButton =(ImageButton)findViewById(R.id.olinemapButton);
        offButton =(ImageButton)findViewById(R.id.offlinemapButton);
        lButton =(ImageButton)findViewById(R.id.listButton);

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this,OnlineMap.class);
                startActivity(i);
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this,OfflineMap.class);
                startActivity(i);
            }
        });

        lButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this,LandmarkListActivity.class);
                startActivity(i);
            }
        });

    }
}
