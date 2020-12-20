package com.jlcsoftware.todoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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


public class FinishedTaskFragment extends Fragment implements RecyclerViewClickListener {


    private SharedPreferencesClass sharedPreferencesClass;
    private String token;




    private RecyclerView recyclerView;
    private TextView empty_tv;
    private ProgressBar progressBar;


    ArrayList<TodoModel> arrayList;

    private Todo_List_Adapters todo_list_adapters;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        sharedPreferencesClass=new SharedPreferencesClass(getContext());
        token = sharedPreferencesClass.getValue_string("token");
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_finished_task, container, false);

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
        String url = "https://todoappst.herokuapp.com/api/todo/finished";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response.getBoolean("success")){

                        //Toast.makeText(getActivity(), ""+response.toString(), Toast.LENGTH_SHORT).show();

                        JSONArray jsonArray = response.getJSONArray("todo");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TodoModel todoModel =new TodoModel(
                                    jsonObject.getString("_id"),
                                    jsonObject.getString("title"),
                                    jsonObject.getString("description"),
                                    jsonObject.getBoolean("finished")


                            );
                            arrayList.add(todoModel);
                        }





                    }

                    //Log.d("ss","saurav");
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "saurav", Toast.LENGTH_SHORT).show();


                    todo_list_adapters=new Todo_List_Adapters(getActivity(),arrayList,FinishedTaskFragment.this);
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


                NetworkResponse response = error.networkResponse;
                if(error == null || error.networkResponse == null){
                    return;
                }

                String body;

                //final String statusCode = String.valueOf(error.networkResponse.statusCode);

                try {
                    body = new String(error.networkResponse.data,"UTF-8");
                    JSONObject errorObject = new JSONObject(body);

                    if (errorObject.getString("msg").equals("Token not valid")){
                        sharedPreferencesClass.clear();
                        startActivity(new Intent(getActivity(),LoginActivity.class));
                        getActivity().finish();
                    }

                    Toast.makeText(getActivity(), "Session Expired", Toast.LENGTH_SHORT).show();

                }catch (UnsupportedEncodingException | JSONException e){

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

    }

    @Override
    public void onLongItemClick(int position) {

    }

    @Override
    public void onEditButtonClick(int position) {

    }

    @Override
    public void onDeleteButtonClick(int position) {

        showDeleteDialog(arrayList.get(position).getId(),position);
    }

    @Override
    public void onDoneButtonClick(int position) {

    }



    private void showDeleteDialog(final String id,final int position) {



        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Are you sure to delete this task")
                .setPositiveButton("YES",null)
                .setNegativeButton("NO",null)
                .create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positive_btn = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                positive_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Toast.makeText(getActivity(), "Positive Button", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        Delete_Todo(id,position);

                    }
                });

            }
        });

        dialog.show();

    }



    //deleted todo method

    private void Delete_Todo(final String id,final int position){

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


}