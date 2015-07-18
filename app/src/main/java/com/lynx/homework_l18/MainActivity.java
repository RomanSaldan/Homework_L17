package com.lynx.homework_l18;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mGoogleMap;
    private ImageButton btnZoomIn_AM;
    private ImageButton btnZoomOut_AM;
    private ImageButton btnLocation_AM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initMap();
    }

    /*Initialize UI (views, listeners, etc)*/
    private void initUI() {
        btnZoomIn_AM = (ImageButton) findViewById(R.id.btnZoomIn_AM);
        btnZoomOut_AM = (ImageButton) findViewById(R.id.btnZoomOut_AM);
        btnLocation_AM = (ImageButton) findViewById(R.id.btnLocation_AM);
        btnZoomIn_AM.setOnClickListener(this);
        btnZoomOut_AM.setOnClickListener(this);
        btnLocation_AM.setOnClickListener(this);
    }

    /*Initialize Google API map*/
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.googleMapFragment);
        mapFragment.getMapAsync(this);
    }

    /*Prepare simple marker*/
    private MarkerOptions prepareMarker(LatLng _position) {
        MarkerOptions marker = new MarkerOptions();
        marker  .title("Marker title")
                .position(_position)
                .snippet("My custom marker")
                .icon(BitmapDescriptorFactory.defaultMarker());
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
                .show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if(mGoogleMap == null) return;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMapClickListener(this);
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
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mGoogleMap.addMarker(prepareMarker(latLng));
    }
}
