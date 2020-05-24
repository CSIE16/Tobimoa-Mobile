package com.example.tobimoaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.concurrent.ExecutionException;

public class StampActivity extends AppCompatActivity {
    ImageView[] IMG = new ImageView[7];
    int hstamp = 0;
    int stampCNT = 0;
    String userPH = null, userPASS = null, result = null;
    JSONArray Child;
    ArrayList<String> childList;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp);

        SharedPreferences Users = getSharedPreferences("Users", Activity.MODE_PRIVATE);
        userPH = Users.getString("PhoneNum", null);
        userPASS = Users.getString("Password", null);

        if(userPH == null && userPASS == null){
            Toast.makeText(getApplicationContext(), "로그인 먼저 부탁드립니다!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        try {
            result = new JSONTask().execute("http://tobimoa.ml/api/stamp/get").get();
            Child = new JSONArray(result);
            IMG[0] = (ImageView)findViewById(R.id.hiddenStamp);
            IMG[1] = (ImageView)findViewById(R.id.Stamp1);
            IMG[2] = (ImageView)findViewById(R.id.Stamp2);
            IMG[3] = (ImageView)findViewById(R.id.Stamp3);
            IMG[4] = (ImageView)findViewById(R.id.Stamp4);
            IMG[5] = (ImageView)findViewById(R.id.Stamp5);
            IMG[6] = (ImageView)findViewById(R.id.Stamp6);

            Spinner spinner = (Spinner)findViewById(R.id.spinner);

            childList = new ArrayList<>();
            for(int i=0; i<Child.length(); i++){
                String name = Child.getJSONObject(i).optString("Name");
                System.out.println(name);
                childList.add(name);
            }

            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, childList);
            spinner.setAdapter(arrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long id) {
                    for(int x=0;x<7;x++)IMG[x].setColorFilter(Color.parseColor("#BFBAC7"), PorterDuff.Mode.SRC_IN);
                    String name = childList.get(i);
                    Toast.makeText(getApplicationContext(), name + "가 선택되었습니다!", Toast.LENGTH_SHORT).show();
                    for(int x=0; x<Child.length(); x++){
                        String n = null;
                        int hs = 0, ns = 0;
                        try {
                            n = Child.getJSONObject(x).optString("Name");
                            hs = Child.getJSONObject(x).getJSONObject("StampCnt").optInt("hidden");
                            ns = Child.getJSONObject(x).getJSONObject("StampCnt").optInt("normal");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(n == name){
                            hstamp = hs;
                            stampCNT = ns;
                            System.out.println(name + "" + hstamp + "" + stampCNT);
                            break;
                        }
                    }
                    if(hstamp == 1) IMG[0].setColorFilter(Color.parseColor("#5A3799"), PorterDuff.Mode.SRC_IN);
                    for(int x = 1; x<= stampCNT; x++){
                        IMG[x].setColorFilter(Color.parseColor("#5A3799"), PorterDuff.Mode.SRC_IN);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageButton button1 = findViewById(R.id.homeButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        ImageButton button2 = findViewById(R.id.userButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        ImageButton button3 = findViewById(R.id.locationButton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public class JSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String r = null;
            try{
                JSONObject body = new JSONObject();
                body.put("PhoneNum", userPH);

                HttpURLConnection conn = null;
                StringBuffer response = new StringBuffer();

                try{
                    URL url = new URL(urls[0]);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "text/html");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.connect();

                    DataOutputStream outputStream;
                    outputStream = new DataOutputStream(conn.getOutputStream());
                    outputStream.writeBytes(body.toString());
                    outputStream.flush();
                    outputStream.close();

                    InputStream stream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line = null;

                    while((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    r = response.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return r;
        }
    }
}
