package com.myawesome.kariukimessagingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.myawesome.kariukimessagingapp.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_splash_screen);
        openActivitiesMethod();
    }

    private void openActivitiesMethod() {

        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();

    }
}
