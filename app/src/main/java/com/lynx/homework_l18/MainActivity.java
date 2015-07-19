package com.lynx.homework_l18;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements OnMapReadyCallback, View.OnClickListener, DialogInterface.OnClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mGoogleMap;
    private ImageButton btnZoomIn_AM;
    private ImageButton btnZoomOut_AM;
    private ImageButton btnLocation_AM;
    private ImageButton btnClean_AM;

    private List<LatLng> markersList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markersList = new ArrayList<>();
        sharedPreferences = getPreferences(MODE_PRIVATE);

        initUI();
        initMap();

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveMarkers();
    }

    /*Save markers to shared preferences*/
    private void saveMarkers() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(markersList.size()>0) {
            editor.putInt("size", markersList.size());
            for(int i = 0; i < markersList.size(); i++) {
                editor.putFloat("lat"+i, (float) markersList.get(i).latitude);
                editor.putFloat("lng"+i, (float) markersList.get(i).longitude);
            }
            editor.commit();
        }
    }

    /*Load markers from shared preferences*/
    private void loadMarkers() {
        int size = sharedPreferences.getInt("size", 0);
        for (int i = 0; i < size; i++) {
            double lat = (double) sharedPreferences.getFloat("lat" + i, 0);
            double lng = (double) sharedPreferences.getFloat("lng" + i, 0);
            mGoogleMap.addMarker(prepareMarker(new LatLng(lat, lng)));
        }
    }

    /*Initialize UI (views, listeners, etc)*/
    private void initUI() {
        btnZoomIn_AM = (ImageButton) findViewById(R.id.btnZoomIn_AM);
        btnZoomOut_AM = (ImageButton) findViewById(R.id.btnZoomOut_AM);
        btnLocation_AM = (ImageButton) findViewById(R.id.btnLocation_AM);
        btnClean_AM = (ImageButton) findViewById(R.id.btnClean_AM);
        btnZoomIn_AM.setOnClickListener(this);
        btnZoomOut_AM.setOnClickListener(this);
        btnLocation_AM.setOnClickListener(this);
        btnClean_AM.setOnClickListener(this);
    }

    /*Initialize Google API map*/
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.googleMapFragment);
        mapFragment.getMapAsync(this);
        mapFragment.setRetainInstance(true);
    }

    /*Prepare simple marker*/
    private MarkerOptions prepareMarker(LatLng _position) {
        MarkerOptions marker = new MarkerOptions();
        marker  .title("Marker title")
                .position(_position)
                .snippet("My custom marker")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker));

        return marker;
    }

    /*Get current location info*/
    private String getCurrentLocInfo() throws IOException {
        LatLng current = mGoogleMap.getCameraPosition().target;
        double latitude = current.latitude;
        double longitude = current.longitude;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1);

        StringBuilder strReturnedAddress = new StringBuilder();
        if(addresses != null && addresses.size()>0) {
            Address returnedAddress = addresses.get(0);
            for(int i = 0; i<=returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
            }
        }
        else {
            strReturnedAddress.append("No Address returned!");
        }
        return strReturnedAddress.toString();
    }

    /*Show dialog with current location info*/
    private void showInfoDialog(String _info) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Current location information: ")
                .setMessage(_info)
                .setIcon(R.drawable.background_btn_location)
                .setPositiveButton("OK", this)
                .show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if(mGoogleMap == null) return;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));
        mGoogleMap.setOnInfoWindowClickListener(this);
        loadMarkers();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnZoomIn_AM:
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.btnZoomOut_AM:
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.btnLocation_AM:
                try {
                    showInfoDialog(getCurrentLocInfo());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnClean_AM:
                mGoogleMap.clear();
                markersList.clear();
                sharedPreferences.edit().clear().commit();
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mGoogleMap.addMarker(prepareMarker(latLng));
        markersList.add(latLng);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
    }
}
