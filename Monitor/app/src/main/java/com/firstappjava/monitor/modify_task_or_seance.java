package com.firstappjava.monitor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class modify_task_or_seance extends AppCompatActivity {
    private String Type;
    private int TaskId;
    private int SeanceId;
    private int MonitorId;
    private int ClientId;
    private TextView monitor;
    private TextView client;
    private final HashMap<String, Integer> Monitors=new HashMap<>();
    private final HashMap<String, Integer> Clients=new HashMap<>();
    private EditText comment, detail;
    private CheckBox done;
    private int YouCanStartCatchEventSpinner=3;
    private boolean isMonitorFinishFetching=false,
                    isClientFinishFetching=false;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_modify_task_or_seance);

        Type=getIntent().getStringExtra("Type");
        TaskId=getIntent().getIntExtra("TaskId", -1);
        SeanceId=getIntent().getIntExtra("SeanceId", -1);
        MonitorId=getIntent().getIntExtra("MonitorId", -1);
        ClientId=getIntent().getIntExtra("ClientId", -1);

        monitor=findViewById(R.id.monitor);
        client=findViewById(R.id.client);

        comment=findViewById(R.id.Comment);
        detail=findViewById(R.id.Detail);
        done=findViewById(R.id.Done);

        comment.setText(getIntent().getStringExtra("Comment"));
        detail.setText(getIntent().getStringExtra("Detail"));

        done.setChecked( getIntent().getBooleanExtra("isDone", false) );
        ((TextView)findViewById(R.id.job)).setText(Type);
        if(Type.equals("Seance")) {
            this.FetchClients();
        }else {
            isClientFinishFetching=true;
        }
        this.FetchMonitor();


        NewDate nd=ManageShcedule.currentDate.clone();
        ArrayList<String> _year=new ArrayList<>();
        int n=nd.getValueYear()+10;
        for(int i=n-10; i<n; ++i) {
            _year.add( String.valueOf(i) );
        }
        String []Year= ToArray(_year);

        ArrayAdapter<CharSequence> ad=new ArrayAdapter<>(this,
                R.layout.sp_date,
                Year);

        ad.setDropDownViewResource(R.layout.sp_date_dropdown);


        this.FixMonth(true);

        String []Hour=new String[15],
                Minute=new String[60];
        for(int i=8; i<23; ++i) {
            Hour[i-8]=" "+(i<10?"0":"")+i+" ";
        }
        for(int i=0; i<60; ++i) {
            Minute[i]=" "+(i<10?"0":"")+i+" ";
        }
        ad=new ArrayAdapter<>(this,
                R.layout.sp_date,
                Hour);
        ad.setDropDownViewResource(R.layout.sp_date_dropdown);

        ad=new ArrayAdapter<>(this,
                R.layout.sp_date,
                Minute);
        ad.setDropDownViewResource(R.layout.sp_date_dropdown);

        LinearLayout Main=findViewById(R.id.main);
        if(Type.equals("Seance")) {
            detail=null;
            Main.removeView( findViewById(R.id.dt) );
            /*findViewById(R.id.tl).setVisibility(View.INVISIBLE);
            ((LinearLayout)findViewById(R.id.tl)).removeAllViews();
            findViewById(R.id.tl).getLayoutParams().height=0;
            findViewById(R.id.tl).requestLayout();

            findViewById(R.id.dt).setVisibility(View.INVISIBLE);
            ((LinearLayout)findViewById(R.id.dt)).removeAllViews();
            findViewById(R.id.dt).getLayoutParams().height=0;
            findViewById(R.id.dt).requestLayout();*/
        }else {
            client=null;
            comment=null;
            Main.removeView( findViewById(R.id.cl) );
            Main.removeView( findViewById(R.id.cm) );
            /*findViewById(R.id.cl).setVisibility(View.INVISIBLE);
            findViewById(R.id.cl).getLayoutParams().height=0;
            findViewById(R.id.cl).requestLayout();
            findViewById(R.id.cm).setVisibility(View.INVISIBLE);
            findViewById(R.id.cm).getLayoutParams().height=0;
            findViewById(R.id.cm).requestLayout();*/
        }
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
    private void FetchMonitor() {
        Monitors.clear();
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
                            if(response.has("msgError")) {
                                LogOut();
                                return;
                            }
                            JSONArray t=response.getJSONArray("result");
                            JSONObject jo;
                            for(int i=0; i<t.length(); ++i) {
                                jo=t.getJSONObject(i);
                                Monitors.put( jo.getString("MonitorFName").trim()+" "+jo.getString("MonitorLName").trim(),
                                        Integer.parseInt(jo.getString("MonitorId")) );

                            }
                            for(String key : Monitors.keySet()) {
                                monitor.setText(key);
                            }
                            isMonitorFinishFetching=true;
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
    private void FetchClients() {
        Clients.clear();
        JsonObjectRequest req=new JsonObjectRequest(
                Request.Method.GET,
                MainActivity.HTTP+"FetchClients.php?"+
                        "email=" + MainActivity.EmailAdmin+
                        "&password="+MainActivity.PasswordAdmin+
                        "&ClientId="+ClientId,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.has("msgError")) {
                                LogOut();
                                return;
                            }
                            JSONArray t=response.getJSONArray("result");
                            JSONObject jo;
                            for(int i=0; i<t.length(); ++i) {
                                jo=t.getJSONObject(i);
                                Clients.put( jo.getString("ClientfName").trim()+" "+jo.getString("ClientlName").trim(),
                                        Integer.parseInt(jo.getString("clientId")) );
                            }
                            for(String key : Clients.keySet()) {
                                client.setText(key);
                            }
                            isClientFinishFetching=true;
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
    String[] ToArray(ArrayList<String> t) {
        if(t==null) {
            return null;
        }
        int n=t.size();
        String[] result=new String[n];
        for(int i=0; i<n; ++i) {
            result[i]=t.get(i);
        }
        return result;
    }
    private void FixMonth(boolean v) {
        ArrayList<String> t=new ArrayList<>();
        for(int i=1; i<13; ++i) {
            t.add( NewDate.NumberToMonth(i) );
        }
        String []Month=ToArray(t);
        ArrayAdapter<CharSequence> ad=new ArrayAdapter<>(this,
                R.layout.sp_date,
                Month);
        ad.setDropDownViewResource(R.layout.sp_date_dropdown);
    }
    public void Return(View view) {
        modify_task_or_seance.this.finish();
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
    public void Modify(View view) throws JSONException {
        Log.d("abc", String.valueOf(SeanceId));
        Log.d("abc", String.valueOf(TaskId));
        if(!isMonitorFinishFetching || !isClientFinishFetching) {
            return;
        }
        int _monitorId=Monitors.get( monitor.getText().toString() ),
            _clientId=Type.equals("Seance")?Clients.get( client.getText().toString() ):-1,
            _isDone=done.isChecked()?1:0;
        String _comment=comment==null?"":comment.getText().toString(),
                _detail=detail==null?"":detail.getText().toString(),
                _page=Type.equals("Seance")?"M_UpdateSeance.php":"M_UpdateTask.php";
        StringRequest req = new StringRequest(
                Request.Method.POST,
                MainActivity.HTTP+_page,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String msg) {
                        try {
                            JSONObject response=new JSONObject(msg);
                            if(response.has("msgError")) {
                                LogOut();
                                return;
                            }
                            if(response.has("Success")) {
                                Intent intent=new Intent(getApplicationContext(), ManageShcedule.class);
                                SaveDate();
                                startActivity(intent);
                                modify_task_or_seance.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("abc",e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("abc", "Fuuuuuck");
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("email", MainActivity.EmailAdmin);
                params.put("password", MainActivity.PasswordAdmin);
                params.put("TaskId", String.valueOf(TaskId));
                params.put("SeanceId", String.valueOf(SeanceId));
                params.put("Comment", _comment);
                params.put("Detail", _detail);
                params.put("isDone", String.valueOf(_isDone));
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
    }
}