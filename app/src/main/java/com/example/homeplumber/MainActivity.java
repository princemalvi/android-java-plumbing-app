package com.example.homeplumber;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler = new Handler();
        preferences = getSharedPreferences("loginPreference",0);
        String user = preferences.getString("userid","");
        String type = preferences.getString("type","");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InternetStatus status = new InternetStatus();
                if(status.isConnected(MainActivity.this)){
                    if (user != null && type.equals("0")) {
                        Intent i = new Intent(MainActivity.this,WelcomeWorker.class);
                        startActivity(i);
                        finish();
                    } else if(user != null && type.equals("1")){
                        Intent i = new Intent(MainActivity.this,WelcomeUser.class);
                        startActivity(i);
                        finish();
                    }
                    else {
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Internet isn\'t connected!")
                            .setCancelable(false)
                            .setPositiveButton("OK",null);
                        builder.create().show();
                    return;

                }
            }
        },3000);
    }
}