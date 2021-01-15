package com.example.e_firpolice;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
public class DataActivityAdapter extends RecyclerView.Adapter<DataActivityAdapter.ViewHolder>{
    private List<ComplainModel> list;
    private Context ctx;
    DataActivityAdapter(List<ComplainModel> list, Context ctx){
        this.list = list;
        this.ctx =ctx;
    }

    @NonNull
    @Override
    public DataActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_row,parent,false);
        return new DataActivityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataActivityAdapter.ViewHolder holder, final int position) {
        SharedPreferences preferences = ctx.getSharedPreferences("eFIR", Context.MODE_PRIVATE);
        final String Token  = preferences.getString("TOKEN",null);//second parameter default value.
        final ComplainModel u=list.get(position);
        if(u.getStatus().equals("Solved")){
            holder.rb.setChecked(true);
            holder.rb.setEnabled(false);
        }
        holder.name.setText(u.getName());
        holder.fname.setText(u.getFather());
        holder.location.setText(u.getLocation());
        holder.type.setText(u.getCategory());
        holder.desc.setText(u.getDescription());
        holder.number.setText(u.getNumber());
        holder.rb.setOnClickListener(view -> {
            String url="https://dry-anchorage-43299.herokuapp.com/firs/"+ u.getId();
            RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(ctx));
            JSONObject o = new JSONObject();
            try {
                o.put("status","Solved");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, o, response -> {
                list.remove(position);
                notifyItemRemoved(position);
            }, error -> {

            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Content-Type", "application/json");
                    headerMap.put("Authorization", "Bearer " + Token);
                    return headerMap;
                }
            };
            requestQueue.add(request);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,fname,location,type,desc,number;
        RadioButton rb;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name =itemView.findViewById(R.id.name);
            fname=itemView.findViewById(R.id.fName);
            location=itemView.findViewById(R.id.location);
            type=itemView.findViewById(R.id.type);
            desc=itemView.findViewById(R.id.desc);
            number=itemView.findViewById(R.id.number);
            rb=itemView.findViewById(R.id.rbSolved);
        }
    }
}
