package com.lynx.homework_l18;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lynx.homework_l18.global.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MainActivity extends Activity implements OnMapReadyCallback, View.OnClickListener, DialogInterface.OnClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap       mGoogleMap;
    private ImageButton     btnZoomIn_AM;
    private ImageButton     btnZoomOut_AM;
    private ImageButton     btnLocation_AM;
    private ImageButton     btnClean_AM;

    private List<LatLng>        mMarkersList;
    private SharedPreferences   mSharedPreferences;

    @Override
    protected void onCreate(Bundle _savedInstanceState) {
        super.onCreate(_savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNetworkAvailable()) { // check internet connection
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setCancelable(false)
                    .setTitle(getString(R.string.dialog_conn_err_title))
                    .setMessage(getString(R.string.dialog_conn_err_msg))
                    .setNegativeButton(getString(R.string.dialog_conn_err_btn), this)
                    .show();
        }

        if(!isPlayServicesAvailable()) { // check GooglePlayServices
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setCancelable(false)
                    .setTitle(getString(R.string.dialog_conn_err_title))
                    .setMessage(getString(R.string.dialog_gps_err_msg))
                    .setNegativeButton(getString(R.string.dialog_conn_err_btn), this)
                    .show();
        }

        mMarkersList        = new ArrayList<>();
        mSharedPreferences  = getPreferences(MODE_PRIVATE);

        initUI();
        initMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveMarkers();
    }

    /*Check if internet connection is available*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*Check if GooglePlayServices is available*/
    private boolean isPlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        return status == ConnectionResult.SUCCESS;
    }

    /*Save markers to shared preferences*/
    private void saveMarkers() {
        mSharedPreferences                  = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor     = mSharedPreferences.edit();
        if(mMarkersList.size()>0) {
            editor.putInt(Constants.KEY_MARKERS_SIZE, mMarkersList.size());
            for(int i = 0; i < mMarkersList.size(); i++) {
                editor.putFloat(Constants.KEY_LATITUDE +i, (float) mMarkersList.get(i).latitude);
                editor.putFloat(Constants.KEY_LONGITUDE +i, (float) mMarkersList.get(i).longitude);
            }
            editor.commit();
        }
    }

    /*Load markers from shared preferences*/
    private void loadMarkers() {
        int size = mSharedPreferences.getInt(Constants.KEY_MARKERS_SIZE, 0);
        for (int i = 0; i < size; i++) {
            double lat = (double) mSharedPreferences.getFloat(Constants.KEY_LATITUDE + i, 0);
            double lng = (double) mSharedPreferences.getFloat(Constants.KEY_LONGITUDE + i, 0);
            mGoogleMap.addMarker(prepareMarker(new LatLng(lat, lng)));
        }
    }

    /*Initialize UI (views, listeners, etc)*/
    private void initUI() {
        btnZoomIn_AM        = (ImageButton) findViewById(R.id.btnZoomIn_AM);
        btnZoomOut_AM       = (ImageButton) findViewById(R.id.btnZoomOut_AM);
        btnLocation_AM = (ImageButton) findViewById(R.id.btnLocation_AM);
        btnClean_AM         = (ImageButton) findViewById(R.id.btnClean_AM);
        btnZoomIn_AM    .setOnClickListener(this);
        btnZoomOut_AM   .setOnClickListener(this);
        btnLocation_AM  .setOnClickListener(this);
        btnClean_AM     .setOnClickListener(this);
    }

    /*Initialize Google API map*/
    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.googleMapFragment);
        mapFragment             .getMapAsync(this);
        mapFragment             .setRetainInstance(true);
    }

    /*Prepare simple marker*/
    private MarkerOptions prepareMarker(LatLng _position) {
        MarkerOptions marker = new MarkerOptions();
        marker  .position(_position)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker));
        return marker;
    }

    /*Get current location info*/
    private String getCurrentLocInfo() throws IOException {
        LatLng current      = mGoogleMap.getCameraPosition().target;
        double latitude     = current.latitude;
        double longitude    = current.longitude;
        Geocoder geocoder;
        List<Address> listOfAddresses;
        geocoder            = new Geocoder(this, Locale.getDefault());
        listOfAddresses     = geocoder.getFromLocation(latitude, longitude, 1);

        StringBuilder strReturnedAddress = new StringBuilder();
        if(listOfAddresses != null && listOfAddresses.size()>0) {
            Address returnedAddress = listOfAddresses.get(0);
            for(int i = 0; i<=returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
            }
        }
        else {
            strReturnedAddress.append(getString(R.string.dialog_msg_noaddress));
        }
        return strReturnedAddress.toString();
    }

    /*Show dialog with current location info*/
    private void showInfoDialog(String _info) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getString(R.string.dialog_msg_title))
                .setMessage(_info)
                .setIcon(R.drawable.background_btn_location)
                .setPositiveButton(getString(R.string.dialog_msg_btn_ok), this)
                .show();
    }

    @Override
    public void onMapReady(GoogleMap _googleMap) {
        mGoogleMap = _googleMap;
        if(mGoogleMap == null) return;
        mGoogleMap  .setMyLocationEnabled(true);
        mGoogleMap  .setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap  .setOnMapLongClickListener(this);
        mGoogleMap  .setInfoWindowAdapter(new MyInfoWindowAdapter(this));
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
                mGoogleMap          .clear();
                mMarkersList        .clear();
                mSharedPreferences  .edit().clear().commit();
                break;
        }
    }

    @Override
    public void onClick(DialogInterface _dialog, int _which) {
        switch (_which) {
            case DialogInterface.BUTTON_POSITIVE:
                _dialog.dismiss();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                finish();
                break;
        }
    }

    @Override
    public void onMapLongClick(LatLng _latLng) {
        mGoogleMap      .addMarker(prepareMarker(_latLng));
        mMarkersList    .add(_latLng);
    }
}
