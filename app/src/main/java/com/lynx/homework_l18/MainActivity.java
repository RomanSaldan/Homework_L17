package com.lynx.homework_l18;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends Activity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mGoogleMap;
    private ImageButton btnZoomIn;
    private ImageButton btnZoomOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMap();

        btnZoomIn = (ImageButton) findViewById(R.id.btnZoomIn_AM);
        btnZoomOut = (ImageButton) findViewById(R.id.btnZoomOut_AM);
        btnZoomIn.setOnClickListener(this);
        btnZoomOut.setOnClickListener(this);

    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.googleMapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if(mGoogleMap == null) return;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
        }
    }
}
