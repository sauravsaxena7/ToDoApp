package com.jlcsoftware.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    private Button create_an_account_btn,login_btn;

    private EditText email_et, password_et;
    private ProgressBar progressBar;

    private String email,password;

    private UtilService utilService;



    private SharedPreferencesClass sharedPreferencesClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        create_an_account_btn = findViewById(R.id.create_an_account_btn);
        create_an_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });


        login_btn=findViewById(R.id.login_btn);
        email_et=findViewById(R.id.login_email_edit);
        password_et=findViewById(R.id.login_password_edit);
        progressBar=findViewById(R.id.login_progress_bar);

        utilService = new UtilService();

        sharedPreferencesClass = new SharedPreferencesClass(this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utilService.hideKeyboardMethod(view,LoginActivity.this);
                email = email_et.getText().toString();
                password = password_et.getText().toString();

                if(validation(view)){
                    login_user(view);
                }



            }
        });











    }

    private void login_user(View view) {

        progressBar.setVisibility(View.VISIBLE);
        final HashMap<String,String> params = new HashMap<>();
        params.put("email",email);
        params.put("password",password);

        String apiKey = "https://todoappst.herokuapp.com/api/todo/auth/login";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                apiKey, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response.getBoolean("success")){
                        String token = response.getString("token");
                        Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
                        sharedPreferencesClass.setValue_string("token",token);

                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
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
                        Toast.makeText(LoginActivity.this,object.getString("msg"), Toast.LENGTH_SHORT).show();
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

                return headers;
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

    private boolean validation(View view) {
        boolean isValid=false;

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



        return isValid;
    }
}