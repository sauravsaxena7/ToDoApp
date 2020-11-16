package com.jlcsoftware.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jlcsoftware.todoapp.UtilsService.SharedPreferencesClass;
import com.jlcsoftware.todoapp.UtilsService.UtilService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Button already_have_an_account_btn,register_btn;
    private EditText name_et,email_et,password_et;
    private ProgressBar progressBar;

    private String name,email,password;

    private UtilService utilService;

    private SharedPreferencesClass sharedPreferencesClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        utilService = new UtilService();

        already_have_an_account_btn = findViewById(R.id.already_have_an_account);
        already_have_an_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });


        name_et=findViewById(R.id.register_name_edit);
        password_et=findViewById(R.id.register_password_edit);
        email_et=findViewById(R.id.register_email_edit);

        register_btn=findViewById(R.id.register_btn);

        progressBar=findViewById(R.id.register_progress_bar);

        sharedPreferencesClass = new SharedPreferencesClass(this);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilService.hideKeyboardMethod(view,RegisterActivity.this);
                name = name_et.getText().toString();
                email = email_et.getText().toString();
                password = password_et.getText().toString();
                if(validation(view)){
                    registerUser(view);
                }
            }
        });






    }

    private void registerUser(View view) {
        progressBar.setVisibility(View.VISIBLE);
        final HashMap<String,String> params = new HashMap<>();
        params.put("username",name);
        params.put("email",email);
        params.put("password",password);

        String apiKey = "https://todoappst.herokuapp.com/api/todo/auth/register";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                apiKey, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response.getBoolean("success")){
                        String token = response.getString("token");
                        sharedPreferencesClass.setValue_string("token",token);
                        Toast.makeText(RegisterActivity.this, token, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse response = error.networkResponse;
                if(error instanceof ServerError && response!=null){

                    try{

                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers,"utf-8"));
                        JSONObject object = new JSONObject(res);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this,object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }catch (JSONException | UnsupportedEncodingException e){
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);

                    }

                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               HashMap<String , String> headers = new HashMap<>();
               headers.put("Content-Type","application/json");
               return params;
            }



        };


        //set Retry policy
        int socketTimes = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimes,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(policy);


        //request add
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonObjectRequest);


    }


    public boolean validation(View view){
        boolean isValid = false;
        if(!TextUtils.isEmpty(name)){

            if(!TextUtils.isEmpty(email)){

                if(!TextUtils.isEmpty(password)){
                    isValid=true;
                }
                else{
                    isValid=false;
                    utilService.showSnackBar(view,"password is required..");
                }
            }
            else{
                isValid=false;
                utilService.showSnackBar(view,"email is required..");
            }
        }
        else{
            isValid=false;
           utilService.showSnackBar(view,"name is required..");
        }

        return isValid;

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences todo_pref = getSharedPreferences("USER_TODO", Activity.MODE_PRIVATE);

        if(todo_pref.contains("token")){
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));

            finish();
        }
    }
}