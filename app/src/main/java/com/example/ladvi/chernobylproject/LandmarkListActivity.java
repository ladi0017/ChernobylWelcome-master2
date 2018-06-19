package com.example.ladvi.chernobylproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class LandmarkListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landmarkactivity);

        LandmarkController landmarkController = new LandmarkController();
        LandmarkListAdapter adapter = new LandmarkListAdapter(this, landmarkController.getLandmarks());

        final ListView listView = (ListView) findViewById(R.id.landmark_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object listViewObject = listView.getItemAtPosition(position);
                Landmark landmark = (Landmark)listViewObject;

                Intent i = new Intent(LandmarkListActivity.this, LandmarkDetailsActivity.class);
                i.putExtra("landmark", landmark);
                startActivity(i);
            }
        });
    }
}
