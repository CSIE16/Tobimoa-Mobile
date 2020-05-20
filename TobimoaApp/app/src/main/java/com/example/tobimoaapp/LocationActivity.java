package com.example.tobimoaapp;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
public class LocationActivity extends AppCompatActivity
        implements OnMapReadyCallback
{
    private GoogleMap mMap;
    private Spinner spinner;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    JSONArray SD;
    StringBuffer sb = new StringBuffer();
    int choose = 0;
    String str =
            "[{'Player':'홍길동','PhoneNum':'01012345678','SerialNum':'101','LocationX':'37.56','LocationY':'126.97','Visited1':'1','Visited2':'1','Visited3':'0','Visited4':'1'," +
                    "'Visited5':'0','Visited6':'0','Visited7':'1','Visited8':'0','Visited9':'1'},"
                    + "{'Player':'김영웅','PhoneNum':'01012341234','SerialNum':'102','LocationX':'37.57','LocationY':'126.98','Visited1':'0','Visited2':'0','Visited3':'0','Visited4':'1'," +
                    "'Visited5':'1','Visited6':'1','Visited7':'1','Visited8':'0','Visited9':'0'},"
                    + "{'Player':'황수민','PhoneNum':'01073900430','SerialNum':'103','LocationX':'37.58','LocationY':'126.99','Visited1':'1','Visited2':'0','Visited3':'1','Visited4':'1'," +
                    "'Visited5':'1','Visited6':'1','Visited7':'1','Visited8':'0','Visited9':'0'}]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        arrayList = new ArrayList<>();
        new JSONTask().execute("http://tobimoa.ml/api/location/get");
        try {
            JSONArray array = new JSONArray(str);   // JSONArray 생성
            for (int i = 0; i < array.length(); i++) {
                JSONObject Object = array.getJSONObject(i);  // JSONObject 추출
                String Player = Object.getString("Player");
                arrayList.add(Player);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), arrayList.get(i) + "이 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
                choose = i;
                onMapReady(mMap);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

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

    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject body = new JSONObject();
                body.put("PhoneNum", "01073900430");
                HttpURLConnection conn = null;
                StringBuffer response = new StringBuffer();
                try {
                    URL url = new URL(urls[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "text/html");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.connect();
                    DataOutputStream outputStream;//보낼 데이터 저장소
                    outputStream = new DataOutputStream(conn.getOutputStream());
                    outputStream.writeBytes(body.toString());
                    outputStream.flush();
                    outputStream.close();//보낸 데이터 다음에 response써야함 안그러면 에러

                    InputStream stream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    System.out.println("값 받아옴" + response.toString());
                    return response.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                SD = new JSONArray(result);
                System.out.println("성공"+SD);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        try {
            mMap.clear();
            JSONArray jarray = new JSONArray(str);   // JSONArray 생성
            JSONObject jObject = jarray.getJSONObject(choose);  // JSONObject 추출
            String Player = jObject.getString("Player");
            String SerialNum = jObject.getString("SerialNum");
            String LocationX = jObject.getString("LocationX");
            String LocationY = jObject.getString("LocationY");
            String Visited1 = jObject.getString("Visited1");
            String Visited2 = jObject.getString("Visited2");
            String Visited3 = jObject.getString("Visited3");
            String Visited4 = jObject.getString("Visited4");
            String Visited5 = jObject.getString("Visited5");
            String Visited6 = jObject.getString("Visited6");
            String Visited7 = jObject.getString("Visited7");
            String Visited8 = jObject.getString("Visited8");
            String Visited9 = jObject.getString("Visited9");

            LatLng stamp1 = new LatLng(37.5,127);
            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(stamp1);
            markerOptions1.title("1st Stamp Here");
            if(Visited1.equals("1")) {
                markerOptions1.snippet("Success!!");
                markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
            }
            else {
                markerOptions1.snippet("Not yet...");
                markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
            }
            mMap.addMarker(markerOptions1);

            LatLng stamp2 = new LatLng(37.6,126.99);
            MarkerOptions markerOptions2 = new MarkerOptions();
            markerOptions2.position(stamp2);
            markerOptions2.title("2nd Stamp Here");
            if(Visited2.equals("1")) {
                markerOptions2.snippet("Success!!");
                markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
            }
            else {
                markerOptions2.snippet("Not yet...");
                markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
            }
            mMap.addMarker(markerOptions2);

            LatLng stamp3 = new LatLng((37.55),126.95);
            MarkerOptions markerOptions3 = new MarkerOptions();
            markerOptions3.position(stamp3);
            markerOptions3.title("3rd Stamp Here");
            if(Visited3.equals("1")) {
                markerOptions3.snippet("Success!!");
                markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
            }
            else {
                markerOptions3.snippet("Not yet...");
                markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
            }
            mMap.addMarker(markerOptions3);

            LatLng stamp4 = new LatLng((37.59),126.91);
            MarkerOptions markerOptions4 = new MarkerOptions();
            markerOptions4.position(stamp4);
            markerOptions4.title("4th Stamp Here");
            if(Visited4.equals("1")) {
                markerOptions4.snippet("Success!!");
                markerOptions4.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
            }
            else {
                markerOptions4.snippet("Not yet...");
                markerOptions4.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
            }
            mMap.addMarker(markerOptions4);

            LatLng stamp5 = new LatLng((37.57),126.943);
            MarkerOptions markerOptions5 = new MarkerOptions();
            markerOptions5.position(stamp5);
            markerOptions5.title("5th Stamp Here");
            if(Visited5.equals("1")) {
                markerOptions5.snippet("Success!!");
                markerOptions5.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
            }
            else {
                markerOptions5.snippet("Not yet...");
                markerOptions5.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
            }
            mMap.addMarker(markerOptions5);

            LatLng stamp6 = new LatLng((37.49),126.939);
            MarkerOptions markerOptions6 = new MarkerOptions();
            markerOptions6.position(stamp6);
            markerOptions6.title("6th Stamp Here");
            if(Visited6.equals("1")) {
                markerOptions6.snippet("Success!!");
                markerOptions6.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
            }
            else {
                markerOptions6.snippet("Not yet...");
                markerOptions6.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
            }
            mMap.addMarker(markerOptions6);

            LatLng stamp7 = new LatLng((37.44),126.9);
            MarkerOptions markerOptions7 = new MarkerOptions();
            markerOptions7.position(stamp7);
            markerOptions7.title("7th Stamp Here");
            if(Visited7.equals("1")) {
                markerOptions7.snippet("Success!!");
                markerOptions7.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
            }
            else {
                markerOptions7.snippet("Not yet...");
                markerOptions7.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
            }
            mMap.addMarker(markerOptions7);

            LatLng stamp8 = new LatLng((37.57),126.942);
            MarkerOptions markerOptions8 = new MarkerOptions();
            markerOptions8.position(stamp8);
            markerOptions8.title("8th Stamp Here");
            if(Visited8.equals("1")) {
                markerOptions8.snippet("Success!!");
                markerOptions8.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
            }
            else {
                markerOptions8.snippet("Not yet...");
                markerOptions8.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
            }
            mMap.addMarker(markerOptions8);

            LatLng stamp9 = new LatLng((37.58),126.937);
            MarkerOptions markerOptions9 = new MarkerOptions();
            markerOptions9.position(stamp9);
            markerOptions9.title("9th Stamp Here");
            if(Visited9.equals("1")) {
                markerOptions9.snippet("Success!!");
                markerOptions9.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
            }
            else {
                markerOptions9.snippet("Not yet...");
                markerOptions9.icon(BitmapDescriptorFactory.fromResource(R.drawable.starsmall));
            }
            mMap.addMarker(markerOptions9);

            LatLng LOCATION = new LatLng(Double.parseDouble(LocationX),Double.parseDouble(LocationY));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(LOCATION);
            markerOptions.title(Player);
            markerOptions.snippet(SerialNum);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.babysmall));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LOCATION));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

