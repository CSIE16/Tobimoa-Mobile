package com.example.tobimoaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.tobimoaapp.LocationActivity;
import com.example.tobimoaapp.LoginActivity;
import com.example.tobimoaapp.R;

public class StampActivity extends AppCompatActivity {
    ImageView[] IMG = new ImageView[7];
    boolean hstamp = false;
    int stampCNT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stamp);

        IMG[0] = (ImageView)findViewById(R.id.hiddenStamp);
        IMG[1] = (ImageView)findViewById(R.id.Stamp1);
        IMG[2] = (ImageView)findViewById(R.id.Stamp2);
        IMG[3] = (ImageView)findViewById(R.id.Stamp3);
        IMG[4] = (ImageView)findViewById(R.id.Stamp4);
        IMG[5] = (ImageView)findViewById(R.id.Stamp5);
        IMG[6] = (ImageView)findViewById(R.id.Stamp6);

        IMG[0].setColorFilter(Color.parseColor("#5A3799"), PorterDuff.Mode.SRC_IN);
        for(int i = 1; i<= 5; i++){
            IMG[i].setColorFilter(Color.parseColor("#5A3799"), PorterDuff.Mode.SRC_IN);
        }

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

        ImageButton button3 = findViewById(R.id.locationButton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                startActivity(intent);
            }
        });

    }
}
