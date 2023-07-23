package com.example.breedsocial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Find the Button2 view by ID
        View mButton2 = findViewById(R.id.button2);
        View mButton1 = findViewById(R.id.button1);

        // Set a click listener on Button2
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the RegisterActivity
                Intent intent = new Intent(LandingActivity.this, Register.class);
                startActivity(intent);
            }




        });
        // Set a click listener on Button2
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the RegisterActivity
                Intent intent = new Intent(LandingActivity.this, Login.class);
                startActivity(intent);
            }




        });
    }
}