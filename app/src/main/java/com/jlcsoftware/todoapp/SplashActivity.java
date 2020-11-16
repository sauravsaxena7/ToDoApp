package com.jlcsoftware.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences todo_pref ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }


        todo_pref= getSharedPreferences("USER_TODO", Activity.MODE_PRIVATE);

        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(3000);
                    if(todo_pref.contains("token")){
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));

                        finish();
                    }else{
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }

                }catch (Exception e){

                }
            }
        };

        thread.start();



    }



}