package com.example.e_firpolice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataActivity extends AppCompatActivity {

    List<ComplainModel> solvedComplains;
    List<ComplainModel> pendingComplains;
    RadioButton rbFinished, rbPending;
    RecyclerView rvTasks;
    DataActivityAdapter adapter;
    private long pressedTime = 0;
    private Toast showToast;
    String Token;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        SharedPreferences preferences = DataActivity.this.getSharedPreferences("eFIR", MODE_PRIVATE);
        Token  = preferences.getString("TOKEN",null);//second parameter default value.
        ctx =DataActivity.this;
        rbFinished = findViewById(R.id.rbFinished);
        rbPending = findViewById(R.id.rbPending);

        Toolbar actionBar = (Toolbar) findViewById(R.id.toolbar_data_activity);
        setSupportActionBar(actionBar);
        if(actionBar!=null){
            getSupportActionBar().setTitle("DataBase") ;
        }
        assert actionBar != null;
        actionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        rvTasks = (RecyclerView) findViewById(R.id.rvTasks);
        rvTasks.setHasFixedSize(true);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        solvedComplains = new ArrayList<>();
        pendingComplains = new ArrayList<>();
        Adapt();
        adapter = new DataActivityAdapter(pendingComplains,ctx);
        rvTasks.setAdapter(adapter);
        rbFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vew) {

                Adapt();
                adapter = new DataActivityAdapter(solvedComplains,ctx);
                rvTasks.setAdapter(adapter);
            }
        });

        rbPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Adapt();
                adapter = new DataActivityAdapter(pendingComplains,ctx);
                rvTasks.setAdapter(adapter);
            }
        });

    }

    public void Adapt(){
        String url="https://dry-anchorage-43299.herokuapp.com/firs";
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(DataActivity.this));
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    solvedComplains.clear();
                    pendingComplains.clear();
                    for(int i=0;i<response.length();i++){
                        JSONObject object = response.getJSONObject(i);
                        ComplainModel l = new ComplainModel(
                                object.getString("name"),
                                object.getString("father"),
                                object.getString("location"),
                                object.getString("category"),
                                object.getString("description"),
                                object.getString("number"),
                                object.getString("_id"),
                                object.getString("status")
                        );
                        if(object.getString("status").equals("Pending")){
                            pendingComplains.add(l);}
                        else{
                            solvedComplains.add(l);}
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + Token);
                return headerMap;
            }
        };
        requestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_design, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.log_out)
        {
            LogOutDialog timeUpDialog = new LogOutDialog(ctx);
            timeUpDialog.show(getSupportFragmentManager(), "Log Out?");
        }

        if(item.getItemId() == R.id.refresh)
        {
            Intent intent = new Intent(this, DataActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if(pressedTime + 2000 > System.currentTimeMillis())
        {
            showToast.cancel();
            finishAffinity();
            finish();
        }
        else
        {
            pressedTime = System.currentTimeMillis();
            showToast = Toast.makeText(this, "Press again to LogOut", Toast.LENGTH_SHORT);
            showToast.show();
        }
    }
}