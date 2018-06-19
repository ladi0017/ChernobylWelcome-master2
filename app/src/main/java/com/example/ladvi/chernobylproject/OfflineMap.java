package com.example.ladvi.chernobylproject;


import android.graphics.Color;
import android.os.AsyncTask;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.text.TextUtils;


import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapzen.android.lost.api.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;





public class OfflineMap extends AppCompatActivity {

    private static final String TAG = "OfflineMap";

    private boolean isEndNotified;
    private ProgressBar progBar;
    private MapView mapView;
    private OfflineManager offlineManager;

    private MapboxMap map;
    private FloatingActionButton floatingActionButton;
    private LocationServices locationServices;
    private LocationEngine locationEngine;

    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    private static final int PERMISSIONS_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.offlinemap);

        locationEngine = LocationSource.getLocationEngine(this);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                offlineManager = OfflineManager.getInstance(OfflineMap.this);

                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(new LatLng(51.422407, 30.261891)) // Northeast
                        .include(new LatLng(51.242220, 29.987233)) // Southwest
                        .build();

                OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                        mapboxMap.getStyleUrl(),
                        latLngBounds,
                        10,
                        20,
                        OfflineMap.this.getResources().getDisplayMetrics().density);

                byte[] metadata;
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JSON_FIELD_REGION_NAME, "Chernobyl");
                    String json = jsonObject.toString();
                    metadata = json.getBytes(JSON_CHARSET);
                } catch (Exception exception) {
                    Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
                    metadata = null;
                }


                offlineManager.createOfflineRegion(
                        definition, metadata,
                        new OfflineManager.CreateOfflineRegionCallback() {
                            @Override
                            public void onCreate(OfflineRegion offlineRegion) {
                                offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
                                progBar = (ProgressBar) findViewById(R.id.progress_bar);
                                startProgress();

                                offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                                    @Override
                                    public void onStatusChanged(OfflineRegionStatus status) {

                                        double percentage = status.getRequiredResourceCount() >= 0
                                                ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                                0.0;

                                        if (status.isComplete()) {
                                            endProgress("Chernobyl zone was dowloaded");
                                        } else if (status.isRequiredResourceCountPrecise()) {
                                            setPercentage((int) Math.round(percentage));
                                        }
                                    }

                                    @Override
                                    public void onError(OfflineRegionError error) {
                                        Log.e(TAG, "onError reason: " + error.getReason());
                                        Log.e(TAG, "onError message: " + error.getMessage());
                                    }

                                    @Override
                                    public void mapboxTileCountLimitExceeded(long limit) {
                                        Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
                                    }
                                });
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG, "Error: " + error);
                            }
                        });
                map = mapboxMap;
                IconFactory iconFactory = IconFactory.getInstance(OfflineMap.this);
                Icon icon1 = iconFactory.fromResource(R.drawable.white);
                Icon icon2 = iconFactory.fromResource(R.drawable.green);
                Icon icon3 = iconFactory.fromResource(R.drawable.yel);
                Icon icon4 = iconFactory.fromResource(R.drawable.red);

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(51.406145, 30.057325))
                        .title("Pripyat main square")
                        .snippet("The central square of city of Pripyat. Hotel Polesia is the dominant landmark in this area."))
                        .setIcon(icon1);

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(51.408340, 30.055810))
                        .title("Ferris wheel")
                        .snippet("Not very contaminated, readings 0,5 microsievert/h"))
                        .setIcon(icon2);

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(51.386696, 30.088431))
                        .title("Red Forest")
                        .snippet("Severely contaminated, avoid. Readings over 500microsievert/h"))
                        .setIcon(icon4);

                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(51.394344, 30.057784))
                        .title("Yanov train station")
                        .snippet("Contaminated place,dont stay long. Readigs up to 400 microsiev/h"))
                        .setIcon(icon3);

                Intent intent = getIntent();
                double intentLat = -1;
                intentLat = intent.getDoubleExtra("lat", intentLat);
                double intentLng = -1;
                intentLng = intent.getDoubleExtra("lng", intentLng);
                if (intentLat != -1)
                {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(intentLat, intentLng), 12);
                    mapboxMap.setCameraPosition(cameraUpdate.getCameraPosition(mapboxMap));
                }
                new DrawGeoJson().execute();
            }
        });



        floatingActionButton = (FloatingActionButton) findViewById(R.id.location_toggle_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
                }
            }
        });
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
            }
            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 16));
            }
            locationEngine.addLocationEngineListener(new LocationEngineListener() {
                @Override
                public void onConnected() {

                }

                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {

                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 16));
                        locationEngine.removeLocationEngineListener(this);
                    }
                }
            });
            floatingActionButton.setImageResource(R.drawable.ic_location_disabled_24dp);
        } else {
            floatingActionButton.setImageResource(R.drawable.ic_my_location_24dp);
        }
        map.setMyLocationEnabled(enabled);
    }

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation(true);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

  //  @Override
  //  public void onPause() {
  //      super.onPause();
  //      mapView.onPause();
   //     offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
   //         @Override
   //         public void onList(OfflineRegion[] offlineRegions) {
   //             if (offlineRegions.length > 0) {
   //                 // delete the last item in the offlineRegions list which will be yosemite offline map
   //                 offlineRegions[(offlineRegions.length - 1)].delete(new OfflineRegion.OfflineRegionDeleteCallback() {
   //                     @Override
   //                     public void onDelete() {
   //                         Toast.makeText(OfflineMap.this, "Chernobyl offline map deleted", Toast.LENGTH_LONG).show();
   //                     }
//
   //                     @Override
   //                     public void onError(String error) {
   //                         Log.e(TAG, "On Delete error: " + error);
   //                     }
   //                 });
    //            }
    //        }
//
    //        @Override
    //        public void onError(String error) {
    //            Log.e(TAG, "onListError: " + error);
    //        }
    //    });
  //  }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    private void startProgress() {

        isEndNotified = false;
        progBar.setIndeterminate(true);
        progBar.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        progBar.setIndeterminate(false);
        progBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
        if (isEndNotified) {
            return;
        }

        isEndNotified = true;
        progBar.setIndeterminate(false);
        progBar.setVisibility(View.GONE);

        Toast.makeText(OfflineMap.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    private class DrawGeoJson extends AsyncTask<Void, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(Void... voids) {

            ArrayList<LatLng> points = new ArrayList<>();

            try {
                // Load GeoJSON file
                InputStream inputStream = getAssets().open("quest.geojson");
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }

                inputStream.close();

                // Parse JSON
                JSONObject json = new JSONObject(sb.toString());
                JSONArray features = json.getJSONArray("features");
                JSONObject feature = features.getJSONObject(0);
                JSONObject geometry = feature.getJSONObject("geometry");
                if (geometry != null) {
                    String type = geometry.getString("type");

                    //GeoJSON only has one feature: a line string
                    if (!TextUtils.isEmpty(type) && type.equalsIgnoreCase("LineString")) {

                        // Get the Coordinates
                        JSONArray coords = geometry.getJSONArray("coordinates");
                        for (int lc = 0; lc < coords.length(); lc++) {
                            JSONArray coord = coords.getJSONArray(lc);
                            LatLng latLng = new LatLng(coord.getDouble(1), coord.getDouble(0));
                            points.add(latLng);
                        }
                    }
                }
            } catch (Exception exception) {
                Log.e(TAG, "Exception Loading GeoJSON: " + exception.toString());
            }
            Log.e(TAG, "Points: " + points);
            return points;
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            super.onPostExecute(points);

            if (points.size() > 0) {

                // Draw polyline on map
                map.addPolyline(new PolylineOptions()
                        .addAll(points)
                        .color(Color.parseColor("#3bb2d0"))
                        .width(3));
            }
        }
        }
}

