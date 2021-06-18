package com.firstappjava.administratorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class delete_task extends AppCompatActivity {
    private int MonitorId;
    private String startDate;
    private boolean DeletingRightNow=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_delete_task);


        MonitorId=getIntent().getIntExtra("MonitorId", -1);
        startDate=getIntent().getStringExtra("startDate");

        Log.d("abc", startDate);
        this.FetchMonitor();

        String title = getIntent().getStringExtra("Title");
        String fullDate = getIntent().getStringExtra("FullDate");

        ((TextView)findViewById(R.id.title)).setText( title );
        ((TextView)findViewById(R.id.fulldate)).setText( fullDate );
    }
    private void FetchMonitor() {
        JsonObjectRequest req=new JsonObjectRequest(
                Request.Method.GET,
                MainActivity.HTTP+"FetchMonitors.php?"+
                        "email=" + MainActivity.EmailAdmin+
                        "&password="+MainActivity.PasswordAdmin+
                        "&MonitorId="+MonitorId,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray t=response.getJSONArray("result");
                            JSONObject jo=t.getJSONObject(0);
                            ((TextView)findViewById(R.id.monitor)).setText( jo.getString("MonitorFName")+" "+jo.getString("MonitorLName") );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
    }

    public void Return(View view) {
        delete_task.this.finish();
    }
    private void SaveDate() {
        String SAVEDATE="SAVEDATE",
                DATE="DATE",
                DAYORWEEK="DAYORWEEK",
                ALLORSEANCEORTASK="ALLORSEANCEORTASK";
        SharedPreferences prefs=getApplicationContext().
                getSharedPreferences(MainActivity.MYPREFS, MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed=prefs.edit();
        ed.putBoolean(SAVEDATE, true);
        ed.putString(DATE, ManageShcedule.currentDate.toString());
        ed.putBoolean(DAYORWEEK, ManageShcedule.getDayOrWeek()==1); // false is day, true is week;
        ed.putInt(ALLORSEANCEORTASK, ManageShcedule.getAllOrScienceOrTask());
        ed.apply();
    }
    public void Remove(View view) {
        if(DeletingRightNow) {
            return;
        }
        DeletingRightNow=true;
        JsonObjectRequest req=new JsonObjectRequest(
                Request.Method.GET,
                MainActivity.HTTP+"DeleteTask.php?"+
                        "email="+MainActivity.EmailAdmin+
                        "&password="+MainActivity.PasswordAdmin+
                        "&MonitorId="+MonitorId+
                        "&startDate="+startDate,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("msgError")) {
                            LogOut();
                            return;
                        }
                        Intent intent=new Intent(getApplicationContext(), ManageShcedule.class);
                        SaveDate();
                        startActivity(intent);
                        delete_task.this.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DeletingRightNow=false;
                    }
                });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
    }
    private void LogOut() {
        SharedPreferences prefs=getApplicationContext().
                getSharedPreferences(MainActivity.MYPREFS, MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed=prefs.edit();
        ed.putBoolean(MainActivity.PREF_AUTOLOG, false);
        ed.apply();
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}