package com.example.e_firpolice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    String user,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.btn_LogIn);
        button.setOnClickListener((View view) -> {
            EditText username = findViewById(R.id.name);
            user = username.getText().toString().trim();
            EditText password = findViewById(R.id.password);
            pass = password.getText().toString().trim();
            if(user.isEmpty()){
                username.setError("Please enter username");
                username.requestFocus();
            }
            else if(pass.isEmpty()){password.setError("Please enter Password");
                password.requestFocus();}
            else{
                login(user,pass);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        EditText username = findViewById(R.id.name);
        EditText password = findViewById(R.id.password);
        username.setText(null);
        password.setText(null);
        username.requestFocus();
    }

    public void login(String name, String password){
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://dry-anchorage-43299.herokuapp.com/users/login";
        JSONObject o = new JSONObject();
        try{
            o.put("name",name);
            o.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, o, (JSONObject response) -> {
            Toast.makeText(MainActivity.this,"You have been logged in successfully",Toast.LENGTH_SHORT).show();
            try{
                String token = response.getString("token");
                SharedPreferences preferences = MainActivity.this.getSharedPreferences("eFIR", MODE_PRIVATE);
                preferences.edit().putString("TOKEN",token).apply();
                Intent i = new Intent(MainActivity.this,DataActivity.class);
                startActivity(i);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(MainActivity.this, "Please check your credentials", Toast.LENGTH_SHORT).show();
        });
        requestQueue.add(request);
    }
}