package com.example.navigo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


public class MapFragment extends Fragment {


    public MapFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue("MyUserAgentVal");
        // Inflate the layout for this fragment
        LinearLayout l1 = (LinearLayout) inflater.inflate(R.layout.fragment_map, container, false);
        MapView map = (MapView) l1.findViewById(R.id.map);
        map.setMultiTouchControls(true);

        GeoPoint startPoint = new GeoPoint(-1.2832533, 36.8219);
        IMapController mapController = map.getController();
        mapController.setZoom(20.0);
        mapController.setCenter(startPoint);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        startMarker.setTitle("Start point");
        map.invalidate();
        return l1;
    }
}