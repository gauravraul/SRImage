package com.example.gaurav.sendimage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Gaurav on 23-06-2016.
 */
public class FetchActivity extends AppCompatActivity {

    //Creating a List of superheroes
    private List<ConfigAdapter> listConfigAdapter;

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    String latitude, longitude;
    private String KEY_LATITUDE = "latitude";
    private String KEY_LONGITUDE = "longitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_listview_layout);

        Intent intent = this.getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
//        BackgroundTask backgroundTask = new BackgroundTask(getBaseContext());
//        backgroundTask.execute(latitude,longitude);
//        finish();
//        Log.d("latitude",latitude);
//        Log.d("longitude",longitude);
//        Toast.makeText(this,"Data received - Lat: "+latitude+" lng: "+longitude,Toast.LENGTH_LONG);

        //Initializing Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Initializing our superheroes list
        listConfigAdapter = new ArrayList<>();


        //Calling method to get data
        getData();
    }


    //This method will get data from the web api
    private void getData(){


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DATA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
//                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getBaseContext(), s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
//                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getBaseContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_LATITUDE,latitude);
                params.put(KEY_LONGITUDE,longitude);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
        //Showing a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Loading Data", "Please wait...",false,false);
        //Creating a json array request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Config.DATA_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Dismissing progress dialog
                        loading.dismiss();

                        //calling method to parse json array
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
//
//        //Creating request queue
//       requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(jsonArrayRequest);
    }

    //This method will parse json data
    private void parseData(JSONArray array){
        for(int i = 0; i<array.length(); i++) {
            ConfigAdapter configAdapter = new ConfigAdapter();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                configAdapter.setImageUrl(json.getString(Config.TAG_IMAGE_URL));
                configAdapter.setName(json.getString(Config.TAG_NAME));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            listConfigAdapter.add(configAdapter);
        }

        //Finally initializing our adapter
        adapter = new CardAdapter(listConfigAdapter, this);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);
    }
}