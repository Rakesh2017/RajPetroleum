package com.enhabyto.rajpetroleum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import mehdi.sakout.fancybuttons.FancyButton;

public class SplashScreen extends AppCompatActivity {

    FancyButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        button = findViewById(R.id.splash_submitButton);

        YoYo.with(Techniques.Landing)
                .duration(3000)
                .repeat(0)
                .playOn(findViewById(R.id.splash_logo));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences dataSave = getSharedPreferences("firstLog", 0);

                try {
                    if(dataSave.getString("LaunchApplication","keys").equals("DashBoard")){
                        Intent intent = new Intent(SplashScreen.this, DashBoard.class);
                        startActivity(intent);
                        SplashScreen.this.finish();
                    }
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }

                checkPermissions();
                button = findViewById(R.id.splash_submitButton);

                checkPermissions();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkPermissions();
                    }
                });

            }
        },3500);

    }


    public void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(SplashScreen.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(SplashScreen.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(SplashScreen.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(SplashScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SplashScreen.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION
                    , android.Manifest.permission.CALL_PHONE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            button.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.FadeIn)
                    .duration(3000)
                    .repeat(0)
                    .playOn(button);
        }

        else {

            Intent i = new Intent(getBaseContext(), Login.class);
            startActivity(i);

            //Remove activity
            SplashScreen.this.finish();
        }
    }
}