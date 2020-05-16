package com.example.tobimoaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.tobimoaapp.LoginActivity;
import com.example.tobimoaapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationActivity extends AppCompatActivity
        implements OnMapReadyCallback
{
    private GoogleMap mMap;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton button1 = findViewById(R.id.homeButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button2 = findViewById(R.id.userButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button4 = findViewById(R.id.stampButton);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StampActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        StringBuffer sb = new StringBuffer();

        String str =
                "[{'Player':'홍길동','Protector':'길동엄마','PhoneNum':'01012345678','Password':'1234','SerialNum':'101','LocationX':'37.56','LocationY':'126.97','StampCnt':['0','1'],'Visited':['0','0','0']},"
                        + "{'Player':'김영웅','Protector':'웅쓰엄마','PhoneNum':'01012341234','Password':'5678','SerialNum':'102','LocationX':'37.57','LocationY':'126.98','StampCnt':['1','1'],'Visited':['0','1','1']},"
                        + "{'Player':'황수민','Protector':'김영웅','PhoneNum':'01073900430','Password':'0430','SerialNum':'103','LocationX':'37.58','LocationY':'126.99','StampCnt':['1','5'],'Visited':['1','1','1'] }]";

        LatLng stamp1 = new LatLng((37.5),127);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(stamp1);
        markerOptions1.title("1st Stamp Here");
        markerOptions1.snippet("Success!!");
        //markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
        markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
        mMap.addMarker(markerOptions1);

        LatLng stamp2 = new LatLng(37.6,126.99);
        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(stamp2);
        markerOptions2.title("2nd Stamp Here");
        markerOptions2.snippet("Success!!");
        //markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
        markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
        mMap.addMarker(markerOptions2);

        LatLng stamp3 = new LatLng((37.55),126.95);
        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(stamp3);
        markerOptions3.title("3rd Stamp Here");
        markerOptions3.snippet("Not yet...");
        markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.stamp));
        mMap.addMarker(markerOptions3);

        LatLng stamp4 = new LatLng((37.59),126.91);
        MarkerOptions markerOptions4 = new MarkerOptions();
        markerOptions4.position(stamp4);
        markerOptions4.title("4rd Stamp Here");
        markerOptions4.snippet("Not yet...");
        markerOptions4.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.stamp));
        mMap.addMarker(markerOptions4);

        LatLng stamp5 = new LatLng((37.35),126.943);
        MarkerOptions markerOptions5 = new MarkerOptions();
        markerOptions5.position(stamp5);
        markerOptions5.title("1st Stamp Here");
        markerOptions5.snippet("Not yet...");
        markerOptions5.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.stamp));
        mMap.addMarker(markerOptions5);

        LatLng stamp6 = new LatLng((37.49),126.955);
        MarkerOptions markerOptions6 = new MarkerOptions();
        markerOptions6.position(stamp6);
        markerOptions6.title("6rd Stamp Here");
        markerOptions6.snippet("Not yet...");
        markerOptions6.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
        //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.stamp));
        mMap.addMarker(markerOptions6);




        try {
            JSONArray jarray = new JSONArray(str);   // JSONArray 생성
            for(int i=0; i < jarray.length(); i++){
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                String Player = jObject.getString("Player");
                String Protector = jObject.getString("Protector");
                String PhoneNum = jObject.getString("PhoneNum");
                String Password = jObject.getString("Password");
                String SerialNum = jObject.getString("SerialNum");
                String LocationX = jObject.getString("LocationX");
                String LocationY = jObject.getString("LocationY");
                String StampCnt = jObject.getString("StampCnt");
                String Visited = jObject.getString("Visited");

                LatLng LOCATION = new LatLng(Double.parseDouble(LocationX),Double.parseDouble(LocationY));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(LOCATION);
                markerOptions.title(Player);
                markerOptions.snippet(SerialNum);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.babysmall));
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.stamp));
                mMap.addMarker(markerOptions);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(LOCATION));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
