package com.example.tobimoaapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
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
import java.util.Timer;
import java.util.TimerTask;

public class LocationActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    double latitude ;
    double longitude ;
    private GoogleMap mMap;
    private Spinner spinner;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    JSONArray SD;
    StringBuffer sb = new StringBuffer();
    int cnt = 0, now = -3, choose = -1, past = -2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getApplicationContext(), "GPS 기능을 확인해주세요!", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_location);
        startLocationService();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                cnt++;
                new JSONTask().execute("http://tobimoa.ml/api/location/get");
            }
        };
        Timer timer = new Timer();
        timer.schedule(tt, 0, 3000);
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
                if (choose == -1) {
                    arrayList = new ArrayList<>();
                    arrayList.add("아이를 선택하세요");
                    for (int i = 0; i < SD.length(); i++) {
                        JSONObject Object = SD.getJSONObject(i);  // JSONObject 추출
                        String Name = Object.getString("Name");
                        arrayList.add(Name);
                    }
                }
                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, arrayList);
                spinner = (Spinner) findViewById(R.id.spinner);
                spinner.setAdapter(arrayAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (past == -2) // 0으로 처음 셋팅
                        {
                            Toast.makeText(getApplicationContext(), arrayList.get(i),
                                    Toast.LENGTH_SHORT).show();
                            choose = i;
                        } else if (now == cnt) // 선택할 때
                        {
                            if (i == 0)
                                Toast.makeText(getApplicationContext(), arrayList.get(i),
                                        Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), arrayList.get(i) + "이 선택되었습니다.",
                                        Toast.LENGTH_SHORT).show();
                            choose = i;
                            if (choose > 0) choose--;
                        }
                        System.out.println(i);
                        onMapReady(mMap);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        if (choose == -1) return;
        try {
            mMap.clear();
            JSONObject jObject = SD.getJSONObject(choose);  // JSONObject 추출
            System.out.println("ㅎㅎㅎㅎㅎㅎㅎ확" + SD);
            JSONObject lObject = jObject.getJSONObject("Location");  // JSONObject 추출
            JSONArray vObject = jObject.getJSONArray("Visited");  // JSONObject 추출
            String Player = jObject.getString("Name");
            double LocationX = lObject.getDouble("latitude");
            double LocationY = lObject.getDouble("longitude");
            int[] Visited = new int[10];
            for (int i = 1; i <= 9; i++) Visited[i] = vObject.optInt(i);
            MarkerOptions markerOption = new MarkerOptions();
            LatLng stamp[] = new LatLng[10];
            stamp[1] = new LatLng(37.5, 127);
            stamp[2] = new LatLng(37.6, 126.99);
            stamp[3] = new LatLng(37.57, 126.95);
            stamp[4] = new LatLng(37.59, 126.91);
            stamp[5] = new LatLng(37.57, 126.943);
            stamp[6] = new LatLng(37.49, 126.939);
            stamp[7] = new LatLng(37.44, 126.9);
            stamp[8] = new LatLng(37.67, 126.922);
            stamp[9] = new LatLng(37.58, 126.937);
            //스탬프정보
            for (int i = 1; i <= 9; i++) {
                if (i == 1) markerOption.title("1st Stamp Here");
                else if (i == 2) markerOption.title("2nd Stamp Here");
                else if (i == 3) markerOption.title("3rd Stamp Here");
                else markerOption.title(i + "th Stamp Here");
                markerOption.position(stamp[i]);
                if (Visited[i] == 1) {
                    markerOption.snippet("Success!!");
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.flagsmall));
                } else {
                    markerOption.snippet("Not yet...");
                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.noplace_s));
                }
                mMap.addMarker(markerOption);
            }
            //핸드폰위치정보
            LatLng pLOCATION = new LatLng(latitude, longitude);
            MarkerOptions pmarkerOptions = new MarkerOptions();
            pmarkerOptions.position(pLOCATION);
            pmarkerOptions.title("Me");
            pmarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.phone_s));
            mMap.addMarker(pmarkerOptions);
            //아이위치정보
            LatLng LOCATION = new LatLng(LocationX, LocationY);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(LOCATION);
            markerOptions.title(Player);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.baby_s));
            mMap.addMarker(markerOptions);
            if (choose != past) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(LOCATION));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                past = choose;
            }
            now = cnt;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                String message = "최근 위치 -> Latitude: " + latitude + "\nLongitude:" + longitude;
                System.out.println(message);
            } else System.out.println("실패애ㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐㅐ");
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        GPSListener gpsListener = new GPSListener();
        long minTime = 1000;
        float minDistance = 0;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
    }
    class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            String message = "최근 위치 -> Latitude: " + latitude + "\nLongitude:" + longitude;
            System.out.println(message);
        }
        public void onProviderDisabled(String provider) { }
        public void onProviderEnabled(String provider) { }
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }
}
