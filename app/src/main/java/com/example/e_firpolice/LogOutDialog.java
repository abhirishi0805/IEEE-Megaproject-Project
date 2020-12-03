package com.example.e_firpolice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LogOutDialog extends AppCompatDialogFragment
{
    Context ctx;
    String Token;
    public LogOutDialog(Context ctx) {
        this.ctx=ctx;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        SharedPreferences preferences = ctx.getSharedPreferences("eFIR", Context.MODE_PRIVATE);
        Token  = preferences.getString("TOKEN",null);//second parameter default value.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Log out?");
        builder.setMessage("Please confirm if you want to log out?");
        builder.setPositiveButton("Yes, log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                logout();
            }
        });
        builder.setNegativeButton("No, cancel", null);

        return builder.create();
    }
    public void logout(){
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(ctx));
        String url="https://dry-anchorage-43299.herokuapp.com/users/logout";
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);

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
}
