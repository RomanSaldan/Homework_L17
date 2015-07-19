package com.lynx.homework_l18;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by WORK on 19.07.2015.
 */
public final class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context mCtx;

    public MyInfoWindowAdapter(Context _context) {
        mCtx = _context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.custom_marker_layout, null, false);
        TextView tvTitle_CML        = (TextView) view.findViewById(R.id.tvTitle_CML);
        TextView tvDescription_CML  = (TextView) view.findViewById(R.id.tvDescription_CML);
        tvTitle_CML         .setText(mCtx.getString(R.string.marker_num) + marker.getId());
        tvDescription_CML   .setText(mCtx.getString(R.string.marker_description));
        return view;
    }
}
