package com.example.tobimoaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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

public class LoginActivity extends AppCompatActivity {
    String myPH, myPASS;
    String userPH = null, userPASS = null, userNAME = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences Users = getSharedPreferences("Users", Activity.MODE_PRIVATE);

        userPH = Users.getString("PhoneNum",null);
        userPASS = Users.getString("Password", null);
        userNAME = Users.getString("Name", null);

        if(userPH != null && userPASS != null){
            Toast.makeText(this, userNAME + "님 자동로그인 입니다!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, SubLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.page_1:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.page_2:
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.page_3:
                        intent = new Intent(getApplicationContext(), LocationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.page_4:
                        intent = new Intent(getApplicationContext(), StampActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });

        final EditText e1 = (EditText) findViewById(R.id.editTextPh);
        final EditText e2 = (EditText) findViewById(R.id.editTextpass);

        Button button2 = findViewById(R.id.Login);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPH = e1.getText().toString();
                myPASS = e2.getText().toString();
                new JSONTask().execute("http://tobimoa.ml/api/login");
            }
        });
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject body = new JSONObject();
                body.put("PhoneNum", myPH);
                body.put("Password", myPASS);

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
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {

                String error = "";
                JSONObject dataJson = new JSONObject(result);
                JSONArray Child = dataJson.getJSONArray("Child");

                error = dataJson.optString("error");

                if(error=="") {
                    SharedPreferences Users = getSharedPreferences("Users", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor userData = Users.edit();

                    userData.putString("PhoneNum", dataJson.optString("PhoneNum"));
                    userData.putString("Name", dataJson.optString("Name"));
                    userData.putString("Password", dataJson.optString("Password"));

                    userData.commit();
                    finish();
                }
                else Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}