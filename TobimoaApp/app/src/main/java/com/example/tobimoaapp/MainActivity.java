package com.example.tobimoaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tobimoaapp.LocationActivity;
import com.example.tobimoaapp.LoginActivity;
import com.example.tobimoaapp.R;
import com.example.tobimoaapp.StampActivity;

public class MainActivity extends AppCompatActivity {
    String userPH = null, userPASS = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences Users = getSharedPreferences("Users", Activity.MODE_PRIVATE);
        userPH = Users.getString("PhoneNum", null);
        userPASS = Users.getString("Password", null);

        if(userPH == null && userPASS == null){
            Toast.makeText(getApplicationContext(), "로그인 먼저 부탁드립니다!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        ImageButton button2 = findViewById(R.id.userButton);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        ImageButton button3 = findViewById(R.id.locationButton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
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
}
