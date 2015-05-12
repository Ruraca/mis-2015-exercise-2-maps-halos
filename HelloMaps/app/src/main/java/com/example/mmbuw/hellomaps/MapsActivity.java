package com.example.mmbuw.hellomaps;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.GpsStatus;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

//import com.google.android.gms.maps.SupportMapFragment;

public  class MapsActivity extends FragmentActivity  implements OnMapClickListener, OnMapLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private EditText editText;
    private Button buttonClear;
    private RadioButton radioMarkers,radioHalos;
    SharedPreferences sharedPreferences;
    int locationCount = 0;
     String tittle=null;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onCreate(Bundle savedInstanceState) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            editText = (EditText) findViewById(R.id.editText);
            radioMarkers=(RadioButton) findViewById(R.id.radioMarkers);
            radioHalos=(RadioButton) findViewById(R.id.radioHalos);
            buttonClear=(Button) findViewById(R.id.buttonClear);

            setUpMapIfNeeded();

            try {
                mMap.setOnMapClickListener(this);
                mMap.setOnMapLongClickListener(this);
            } catch (Exception e) {
                Log.e("My Tag", "LAAAAAAAAAAAAAA");
            }
            // Opening the sharedPreferences object
            sharedPreferences = getSharedPreferences("location", 0);
            buttonClear.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            mMap.clear();
                            sharedPreferences.edit().clear().commit();

                        }
                    }
            );

            // Getting number of locations already stored
            locationCount = sharedPreferences.getInt("locationCount", 0);

            // Getting stored zoom level if exists else return 0
            String zoom = sharedPreferences.getString("zoom", "0");

            // If locations are already saved
            if (locationCount != 0) {

                String lat = "";
                String lng = "";

                // Iterating through all the locations stored
                for (int i = 0; i < locationCount; i++) {

                    // Getting the latitude of the i-th location
                    lat = sharedPreferences.getString("lat" + i, "0");

                    // Getting the longitude of the i-th location
                    lng = sharedPreferences.getString("lng" + i, "0");
                    tittle = sharedPreferences.getString("tittle"+i, "vacio");
                    // Drawing marker on the map

                    drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), tittle);
                }

            }
        }
    }

    /*@Override
    public void onMapLongClick(LatLng point) {
        editText.setText("New marker added@" + point.toString());
        mMap.addMarker(new MarkerOptions().position(point).title(point.toString()));
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);// add current position (blue point)
       // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(radioMarkers.isChecked()) {
            locationCount++;
            MarkerOptions marcador = new MarkerOptions();
            marcador.position(latLng);
            marcador.title(editText.getText().toString());
            mMap.addMarker(marcador);
            // Drawing marker on the map
            //drawMarker(latLng);

            /** Opening the editor object to write data to sharedPreferences */
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Storing the latitude for the i-th location
            editor.putString("lat" + Integer.toString((locationCount - 1)), Double.toString(latLng.latitude));

            // Storing the longitude for the i-th location
            editor.putString("lng" + Integer.toString((locationCount - 1)), Double.toString(latLng.longitude));
            editor.putString("tittle" + Integer.toString(locationCount - 1), editText.getText().toString());
            // Storing the count of locations or marker count
            editor.putInt("locationCount", locationCount);

            /** Storing the zoom level to the shared preferences */
            editor.putString("zoom", Float.toString(mMap.getCameraPosition().zoom));

            /** Saving the values stored in the shared preferences */
            editor.commit();
            //editText.setText("New marker added@" + latLng.toString());
        }
        if(radioHalos.isChecked()){
            Log.d("My taga","ENTRAaaaaaaaaaaaaaaaaa");
            // halos code
            // Instantiates a new CircleOptions object and defines the center and radius
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.radius(1000000);
            circleOptions.fillColor(Color.TRANSPARENT);
            circleOptions.strokeColor(Color.RED);
            circleOptions.strokeWidth(5);
            circleOptions.visible(true);

            Circle circle = mMap.addCircle(circleOptions);
        }
    }

    private void drawMarker(LatLng point,String cad){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);
        markerOptions.title(cad);
        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
    }
}
