package com.example.ladvi.chernobylproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class LandmarkDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_details);

        Intent intent = getIntent();
        final Landmark landmark = (Landmark)intent.getSerializableExtra("landmark");

        TextView titleTextView = (TextView) findViewById(R.id.landmarkDetailsTitle);
        titleTextView.setText(landmark.getTitle());

        TextView titleDescriptionView = (TextView) findViewById(R.id.landmarkDetailsDescription);
        titleDescriptionView.setText(landmark.getDescription());

        LinearLayout imagesContainer = (LinearLayout) findViewById(R.id.landmarkDetailsImagesContainer);

        int[] imageArray = landmark.getImages();
        for (int i = 0; i < imageArray.length; i++)
        {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 10;

            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageArray[i]);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setLayoutParams(layoutParams);
            imagesContainer.addView(imageView);
        }

        Button goToMapButton = (Button) findViewById(R.id.landmarkDetailsGoToMapButton);
        goToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LandmarkDetailsActivity.this, OfflineMap.class);
                intent.putExtra("lat", landmark.getLatitude());
                intent.putExtra("lng", landmark.getLongitude());
                startActivity(intent);
            }
        });
    }
}
