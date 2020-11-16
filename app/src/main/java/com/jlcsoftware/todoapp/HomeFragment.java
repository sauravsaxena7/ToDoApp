package com.jlcsoftware.todoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jlcsoftware.todoapp.UtilsService.SharedPreferencesClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {


    private FloatingActionButton floatingActionButton;

    private SharedPreferencesClass sharedPreferencesClass;
    private String token;

    public HomeFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        floatingActionButton = view.findViewById(R.id.add_Task_button);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "saurav", Toast.LENGTH_SHORT).show();
                showAlertDialog();
            }
        });

        return view;
    }

    private void showAlertDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_layout,null);

        final EditText title_field = alertLayout.findViewById(R.id.title_et);
        final EditText description_field = alertLayout.findViewById(R.id.description_et);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(alertLayout)
                .setTitle("Add Task")
                .setPositiveButton("Add",null)
                .setNegativeButton("Cancel",null)
                .create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positive_btn = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                
                positive_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // Toast.makeText(getActivity(), "Positive Button", Toast.LENGTH_SHORT).show();

                        String title = title_field.getText().toString();
                        String description = description_field.getText().toString();

                        if(!TextUtils.isEmpty(title)){
                            add_task(title,description);
                        }else{
                            Toast.makeText(getActivity(), "Bad request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                
            }
        });

        dialog.show();

    }


    //add task phone method
    private void add_task(String title, String description) {

        String url = "https://todoappst.herokuapp.com/api/todo";

        HashMap<String , String> body = new HashMap<>();
        body.put("title",title);
        body.put("description",description);


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

}