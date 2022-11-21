package com.example.navigo;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private GeoPoint startLocation;
    private GeoPoint destinationA;
    private GeoPoint destinationB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //onEdit listener
        MapView map = (MapView) findViewById(R.id.map);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(10.0);
        EditText from = (EditText) findViewById(R.id.from_edittext);
        EditText to = (EditText) findViewById(R.id.to_edittext);
        from.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    from.clearFocus();
                    String query = from.getText().toString();
                    if (query != "") {
                        startLocation = getLocationName(query);
                        System.out.println(startLocation);
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter some Text", Toast.LENGTH_SHORT).show();

                    }

                }
                return true;
            }

        });

        to.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND) {
                    to.clearFocus();
                    String query = to.getText().toString();
                    if(query != ""){
                        destinationA= getLocationName(query);
                        System.out.println(destinationA);
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter destination", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        EditText dest2 = (EditText) findViewById(R.id.to1_edittext);
        dest2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    dest2.clearFocus();
                    String query = dest2.getText().toString();
                    if (query != "") {
                        destinationB = getLocationName(query);
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter destination", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });

        Button findRoute = (Button) findViewById(R.id.find_route);
        findRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((startLocation != null) && (destinationA != null) &&(destinationB != null)){
                    map.getOverlays().clear();
                    mapController.setCenter(startLocation);
                    Marker startMarker = new Marker(map);
                    startMarker.setPosition(startLocation);
                    startMarker.setTitle("Start Location");
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(startMarker);

                    Marker endMarker = new Marker(map);
                    endMarker.setPosition(destinationA);
                    map.getOverlays().add(endMarker);
                    Marker secondMarker = new Marker(map);
                    secondMarker.setPosition(destinationB);
                    map.getOverlays().add(secondMarker);

                    //Road Manager
                    RoadManager roadManager = new OSRMRoadManager(getApplicationContext(), "MyUserAgentVal");
                    //Set Up start and end points
                    ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                    double distance = startLocation.distanceToAsDouble(destinationA) +
                            destinationA.distanceToAsDouble(destinationB) +
                            destinationB.distanceToAsDouble(startLocation);

                    double distance1 = startLocation.distanceToAsDouble(destinationB) +
                            destinationB.distanceToAsDouble(destinationA) +
                            destinationA.distanceToAsDouble(startLocation);

                    if(distance > distance1) {
                        waypoints.add(startLocation);
                        waypoints.add(destinationB);
                        waypoints.add(destinationA);
                        waypoints.add(startLocation);
                    }else{
                        waypoints.add(startLocation);
                        waypoints.add(destinationA);
                        waypoints.add(destinationB);
                        waypoints.add(startLocation);
                    }
//                    waypoints.add(startLocation);
//                    waypoints.add(destination);
                    //Road
                    Road road = roadManager.getRoad(waypoints);
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                    map.getOverlays().add(roadOverlay);
//                    Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
                    for (int i=0; i<road.mNodes.size(); i++){
                        RoadNode node = road.mNodes.get(i);
                        Marker nodeMarker = new Marker(map);
                        nodeMarker.setPosition(node.mLocation);
//                        nodeMarker.setIcon(nodeIcon);
                        nodeMarker.setTitle("Step "+i);
                        nodeMarker.setSnippet(node.mInstructions);
                        nodeMarker.setSubDescription(Road.getLengthDurationText(getApplicationContext(), node.mLength, node.mDuration));
                        map.getOverlays().add(nodeMarker);
                    }
                    map.invalidate();
                } else {
                    System.out.println(startLocation);
                    System.out.println(destinationA);
                    System.out.print(destinationB);
                    Toast.makeText(getApplicationContext(), "Provide start and end locations", Toast.LENGTH_SHORT).show();
                }
            }
        });

   /*     EditText dest3 = (EditText) findViewById(R.id.textView);
        dest3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    dest3.clearFocus();
                    String query = dest3.getText().toString();
                    if (query != "") {
                        destinationC = getLocationName(query);
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter destination", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        }); */



    }

    public GeoPoint getLocationName(String query){
        GeoPoint location = null;
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> Results = geocoder.getFromLocationName(query, 1);
            if (Results.isEmpty()){
                Toast.makeText(getApplicationContext(), "Location Not Found", Toast.LENGTH_SHORT).show();
            }else{
                Address address = Results.get(0);
                location = new GeoPoint(address.getLatitude(), address.getLongitude());
//                map.setMultiTouchControls(true);
//                IMapController mapController = map.getController();
//                mapController.setZoom(17.0);
//                mapController.setCenter(location);
//                Marker startMarker = new Marker(map);
//                startMarker.setPosition(location);
//                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//                map.getOverlays().add(startMarker);
//                startMarker.setTitle("Start point");
//                map.invalidate();


//                double lat = location.getLatitude();
//                Toast.makeText(getApplicationContext(), "Latitude is " + lat, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e){
            System.out.print(e.getMessage());
        }
        return location;
    }
}

