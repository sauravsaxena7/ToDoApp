package com.jlcsoftware.todoapp;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.jlcsoftware.todoapp.Adapters.Todo_List_Adapters;
import com.jlcsoftware.todoapp.Interface.RecyclerViewClickListener;
import com.jlcsoftware.todoapp.Model.TodoModel;
import com.jlcsoftware.todoapp.UtilsService.SharedPreferencesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment implements RecyclerViewClickListener {


    private FloatingActionButton floatingActionButton;

    private SharedPreferencesClass sharedPreferencesClass;
    private String token;

    private RecyclerView recyclerView;
    private TextView empty_tv;
    private ProgressBar progressBar;


    ArrayList<TodoModel> arrayList;

    private Todo_List_Adapters todo_list_adapters;

    public HomeFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        sharedPreferencesClass=new SharedPreferencesClass(getContext());
        token = sharedPreferencesClass.getValue_string("token");
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



        recyclerView=view.findViewById(R.id.recyclerview);
        progressBar=view.findViewById(R.id.pregressBar);
        empty_tv=view.findViewById(R.id.empty_tv);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);


        getTask();



        return view;
    }



    private void getTask() {
        arrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://todoappst.herokuapp.com/api/todo";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response.getBoolean("success")){

                        //Toast.makeText(getActivity(), ""+response.toString(), Toast.LENGTH_SHORT).show();

                        JSONArray  jsonArray = response.getJSONArray("todo");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TodoModel todoModel =new TodoModel(
                                    jsonObject.getString("_id"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("description")

                            );
                            arrayList.add(todoModel);
                        }





                    }

                    //Log.d("ss","saurav");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "saurav", Toast.LENGTH_SHORT).show();


                    todo_list_adapters=new Todo_List_Adapters(getActivity(),arrayList,HomeFragment.this);
                    recyclerView.setAdapter(todo_list_adapters);

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),error.toString(), Toast.LENGTH_SHORT).show();

                NetworkResponse response = error.networkResponse;
                if(error instanceof ServerError && response!=null){

                    try{
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers,"utf-8"));
                        JSONObject object = new JSONObject(res);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),object.getString("msg"), Toast.LENGTH_SHORT).show();
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
                headers.put("Authorization",token);

                return headers;
            }



        };


        //set Retry policy
        int socketTimes = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimes,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(policy);


        //request add
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        requestQueue.add(jsonObjectRequest);

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
                            dialog.dismiss();
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
                url, new JSONObject(body), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response.getBoolean("success")){
                        Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
                        getTask();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();



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

                        Toast.makeText(getActivity(),object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }catch (JSONException | UnsupportedEncodingException e){
                        e.printStackTrace();


                    }

                }

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String , String> headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization",token);
                return headers;
            }



        };


        //set Retry policy
        int socketTimes = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimes,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(policy);


        //request add
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        requestQueue.add(jsonObjectRequest);




    }

    @Override
    public void onItemClicked(int position) {

        Toast.makeText(getActivity(), "position onClick"+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(int position) {
        showUpdateDialog(arrayList.get(position).getId(),arrayList.get(position).getTitle(),arrayList.get(position).getDescription());
        Toast.makeText(getActivity(), "position onLongClick"+position, Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onEditButtonClick(int position) {

        showUpdateDialog(arrayList.get(position).getId(),arrayList.get(position).getTitle(),arrayList.get(position).getDescription());

        Toast.makeText(getActivity(), "Task title"+arrayList.get(position).getTitle(), Toast.LENGTH_SHORT).show();

    }


    private void showUpdateDialog(final String id, String title, String description) {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_layout,null);


        final EditText title_field = alertLayout.findViewById(R.id.title_et);
        final EditText description_field = alertLayout.findViewById(R.id.description_et);

        title_field.setText(title);
        description_field.setText(description);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(alertLayout)
                .setTitle("Update Task")
                .setPositiveButton("Update",null)
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

                        String title1 = title_field.getText().toString();
                        String description1 = description_field.getText().toString();

                        if(!TextUtils.isEmpty(title1)){
                            update_task(id,title1,description1);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getActivity(), "Bad request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        dialog.show();

    }

    private void update_task(String id, String title, String description) {
        String url = "https://todoappst.herokuapp.com/api/todo/"+id;

        HashMap<String ,String> body = new HashMap<>();
        body.put("title",title);
        body.put("description",description);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(body), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("success")){
                        getTask();
                        Toast.makeText(getActivity(), "Task updated", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), ""+error.toString(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String,String> params = new HashMap<>();
                params.put("Content-Type","application/json");
                params.put("Authorization", token);
                return params;
            }
        };

        jsonObjectRequest
                .setRetryPolicy(new DefaultRetryPolicy(10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onDeleteButtonClick(int position) {



        showDeleteDialog(arrayList.get(position).getId(),position);
        Toast.makeText(getActivity(), "position onDeleteButtonClick"+position, Toast.LENGTH_SHORT).show();

    }

    private void showDeleteDialog(final String id,final int position) {


        String url="https://todoappst.herokuapp.com/api/todo/"+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response.getBoolean("success")){
                        Toast.makeText(getActivity(), ""+response.toString(), Toast.LENGTH_SHORT).show();

                        arrayList.remove(position);
                        todo_list_adapters.notifyItemRemoved(position);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);

    }


    @Override
    public void onDoneButtonClick(int position) {

        Toast.makeText(getActivity(), "position onDoneButtonClick"+position, Toast.LENGTH_SHORT).show();

    }




}