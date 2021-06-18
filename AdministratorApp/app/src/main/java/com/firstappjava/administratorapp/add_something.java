package com.firstappjava.administratorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class add_something extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String SEANCEGRPID="SEANCEGRPID";
    // inp_types.getSelectedItem().toString().trim().equals("seance")
    @SuppressLint("StaticFieldLeak")
    public static Spinner inp_types;
    private Spinner year,
                    month,
                    day,
                    hour,
                    minute,
                    NumberOfRepetition,
                    monitor,
                    client;
    private EditText Duration;
    private final HashMap<String, Integer> Monitors=new HashMap<>();
    private final HashMap<String, Integer> Clients=new HashMap<>();
    public static String Comments="";
    public static String Title="";
    public static String Detail="";
    private static int SetTheCurrentDate=2;
    private boolean isMonitorFinishFetching=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_add_something);

        inp_types=findViewById(R.id.inp_type);

        year=findViewById(R.id.year);
        month=findViewById(R.id.month);
        day=findViewById(R.id.day);
        hour=findViewById(R.id.hour);
        minute=findViewById(R.id.minute);
        NumberOfRepetition=findViewById(R.id.NumberOfRepetition);
        Duration=findViewById(R.id.Duration);
        ImageView addTitleAndDetail = findViewById(R.id.AddTitleAndDetail);

        NewDate nd=ManageShcedule.currentDate.clone();
        ArrayList<String> _year=new ArrayList<>(),
                          _month=new ArrayList<>(),
                          _day=new ArrayList<>();


        int n=nd.getValueYear()+10;
        for(int i=n-10; i<n; ++i) {
            _year.add( String.valueOf(i) );
        }
        String []Year= ToArray(_year);
                //Month= ToArray(_month);

        ArrayAdapter<CharSequence> ad1=ArrayAdapter.createFromResource(this,
                                       R.array.types,
                                       R.layout.spinner_colored_layout),
                                   ad2=new ArrayAdapter<>(this,
                                       R.layout.sp_date,
                                       Year);

        ad1.setDropDownViewResource(R.layout.spinner_drop_down);
        ad2.setDropDownViewResource(R.layout.sp_date_dropdown);


        year.setAdapter(ad2);
        
        int yr=Integer.parseInt(year.getSelectedItem().toString());
        if(yr==nd.getValueYear()) {
            for(int i=nd.getValueMonth(); i<13; ++i) {
                _month.add( NewDate.NumberToMonth(i) );
            }
        }else {
            for(int i=1; i<13; ++i) {
                _month.add( NewDate.NumberToMonth(i) );
            }   
        }
        String []Month=ToArray(_month);
        ArrayAdapter<CharSequence> ad3=new ArrayAdapter<>(this,
                                       R.layout.sp_date,
                                       Month);
        ad3.setDropDownViewResource(R.layout.sp_date_dropdown);

        inp_types.setAdapter(ad1);
        month.setAdapter(ad3);

        String mt=month.getSelectedItem().toString();
        int nmt,
            pas=1;

        if(yr==nd.getValueYear() && NewDate.MonthToNumber(mt)==nd.getValueMonth()) {
            pas=ManageShcedule.currentDate.getValueDay();
            nmt=NewDate.NumberOfDayInMonth(NewDate.MonthToNumber(mt), yr)-pas+1;
        }else {
            nmt=NewDate.NumberOfDayInMonth(NewDate.MonthToNumber(mt), yr);
        }
        String []Day=new String[nmt];
        for(int i=0; i<nmt; ++i) {
            Day[i]=" "+(pas++)+" ";
        }
        ArrayAdapter<CharSequence> ad=new ArrayAdapter<>(this,
                R.layout.sp_date,
                Day);
        ad.setDropDownViewResource(R.layout.sp_date_dropdown);
        day.setAdapter(ad);

        ArrayList<String> _hours=new ArrayList<>(),
                          _minutes=new ArrayList<>();
        for(int i=8; i<23; ++i) {
            _hours.add( " "+(i<10?"0":"")+ i +" " );
        }
        for(int i=0; i<60; ++i) {
            _minutes.add( " "+(i<10?"0":"")+ i +" " );
        }
        String[] hours=ToArray(_hours),
                 minutes=ToArray(_minutes);

        ArrayAdapter<CharSequence> ad4=new ArrayAdapter<>(this,
                                       R.layout.sp_date,
                                       hours),
                                   ad5=new ArrayAdapter<>(this,
                                       R.layout.sp_date,
                                       minutes);

        ad4.setDropDownViewResource(R.layout.sp_date_dropdown);
        ad5.setDropDownViewResource(R.layout.sp_date_dropdown);

        hour.setAdapter(ad4);
        minute.setAdapter(ad5);

        inp_types.setOnItemSelectedListener(this);
        year.setOnItemSelectedListener(this);
        month.setOnItemSelectedListener(this);
        day.setOnItemSelectedListener(this);
        hour.setOnItemSelectedListener(this);
        minute.setOnItemSelectedListener(this);
        this.FixNumberOfRepetition();
        this.FetchMonitor();
        this.FetchClients();
    }

    private void FetchMonitor() {
        Monitors.clear();
        JsonObjectRequest req=new JsonObjectRequest(
                Request.Method.GET,
                MainActivity.HTTP+"FetchMonitors.php?"+
                        "email=" + MainActivity.EmailAdmin+
                        "&password="+MainActivity.PasswordAdmin,
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
                            String[] monitorName=new String[Monitors.size()];
                            int p=0;
                            for(String key : Monitors.keySet()) {
                                monitorName[p++]=key;
                            }
                            monitor=findViewById(R.id.monitor);
                            ArrayAdapter<CharSequence> ad=new ArrayAdapter<>(add_something.this,
                                                          R.layout.sp_date,
                                                          monitorName);
                            ad.setDropDownViewResource(R.layout.sp_date_dropdown);
                            monitor.setAdapter(ad);
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
    private boolean isFetchingClientIsCompleted=false;
    private void FetchClients() {
        Clients.clear();
        JsonObjectRequest req=new JsonObjectRequest(
                Request.Method.GET,
                MainActivity.HTTP+"FetchClients.php?"+
                        "email=" + MainActivity.EmailAdmin+
                        "&password="+MainActivity.PasswordAdmin,
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
                            String[] clientName=new String[Clients.size()];
                            int p=0;
                            for(String key : Clients.keySet()) {
                                clientName[p++]=key;
                            }
                            client=findViewById(R.id.client);
                            ArrayAdapter<CharSequence> ad=new ArrayAdapter<>(add_something.this,
                                                         R.layout.sp_date,
                                                         clientName);
                            ad.setDropDownViewResource(R.layout.sp_date_dropdown);
                            client.setAdapter(ad);
                            isFetchingClientIsCompleted=true;
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
    @Override
    protected void onResume() {
        super.onResume();
        Duration.setSelection(Duration.getText().toString().length());
        int maxDur=240;
        TextWatcher textWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {
                String s1=Duration.getText().toString();
                int ind=isGood(s1, maxDur);
                if(ind!=-1) {
                    Duration.setText( s1.substring(0, ind) );
                    Duration.setSelection(Duration.getText().toString().length());
                }
            }
        };
        Duration.addTextChangedListener(textWatcher);
    }
    private int isGood(String s, int MaxVal) {
        int n=s.length(),
            num=0;
        char c;
        for(int i=0; i<n; ++i) {
            c=s.charAt(i);
            if(c<'0' || c>'9') {
                return i;
            }
            num=(num*10)+(c-'0');
            if(num>MaxVal) {
                return i;
            }
        }
        return -1;
    }
    private void FixNumberOfRepetition() {
        String []nbr=new String[7];
        for(int i=0; i<7; ++i) {
            nbr[i]=" "+String.valueOf(i+1)+" ";
        }
        ArrayAdapter<CharSequence> ad=new ArrayAdapter<>(this,
                                      R.layout.sp_date,
                                      nbr);

        ad.setDropDownViewResource(R.layout.sp_date_dropdown);
        NumberOfRepetition.setAdapter(ad);
        NumberOfRepetition.setOnItemSelectedListener(this);
    }
    private void FixMonth() {
        int yr=Integer.parseInt(year.getSelectedItem().toString());
        ArrayList<String> t=new ArrayList<>();
        if(yr==ManageShcedule.currentDate.getValueYear()) {
            for(int i=ManageShcedule.currentDate.getValueMonth(); i<13; ++i) {
                t.add( NewDate.NumberToMonth(i) );
            }
        }else {
            for(int i=1; i<13; ++i) {
                t.add( NewDate.NumberToMonth(i) );
            }
        }
        String []Month=ToArray(t);
        ArrayAdapter<CharSequence> ad=new ArrayAdapter<>(this,
                                      R.layout.sp_date,
                                      Month);
        ad.setDropDownViewResource(R.layout.sp_date_dropdown);
        month.setAdapter(ad);
    }
    private void FixDay() {
        int yr=Integer.parseInt(year.getSelectedItem().toString());
        String mt=month.getSelectedItem().toString();
        int nmt,
            pas=1;

        if(yr==ManageShcedule.currentDate.getValueYear() && NewDate.MonthToNumber(mt)==ManageShcedule.currentDate.getValueMonth()) {
            pas=ManageShcedule.currentDate.getValueDay();
            nmt=NewDate.NumberOfDayInMonth(NewDate.MonthToNumber(mt), yr)-pas+1;
        }else {
            nmt=NewDate.NumberOfDayInMonth(NewDate.MonthToNumber(mt), yr);
        }

        String []t=new String[nmt];
        for(int i=0; i<nmt; ++i) {
            t[i]=" "+String.valueOf(pas++)+" ";
        }
        ArrayAdapter<CharSequence> ad=new ArrayAdapter<>(this,
                                      R.layout.sp_date,
                                      t);
        ad.setDropDownViewResource(R.layout.sp_date_dropdown);
        day.setAdapter(ad);
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId()==R.id.year || parent.getId()==R.id.month) {
            if(SetTheCurrentDate<1) {
                this.FixDay();
            }else {
                --SetTheCurrentDate;
            }
            if(parent.getId()==R.id.year) {
                this.FixMonth();
            }
        }else if(parent.getId()==R.id.inp_type && isFetchingClientIsCompleted) {
            if(inp_types.getSelectedItem().equals("task")) {
                ((TextView)findViewById(R.id.textClient)).setVisibility(View.INVISIBLE);
                client.setVisibility(View.INVISIBLE);
            }else {
                ((TextView)findViewById(R.id.textClient)).setVisibility(View.VISIBLE);
                client.setVisibility(View.VISIBLE);
            }
        }
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
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    boolean isAddingAJob=false;
    public void AddJob(View view) {
        if(!isMonitorFinishFetching || !isFetchingClientIsCompleted) {
            return;
        }
        if(isAddingAJob) {
           return;
        }
        isAddingAJob=true;
        /*String msg=inp_types.getSelectedItem().toString()+" "+
                   year.getSelectedItem().toString()+" "+
                   month.getSelectedItem().toString()+" "+
                   day.getSelectedItem().toString()+" "+
                   hour.getSelectedItem().toString()+" "+
                   minute.getSelectedItem().toString()+" "+
                   NumberOfRepetition.getSelectedItem().toString()+" "+
                   Duration.getText().toString();*/
        NewDate d=new NewDate(
                Integer.parseInt(day.getSelectedItem().toString().trim()),
                NewDate.MonthToNumber(month.getSelectedItem().toString().trim()),
                Integer.parseInt( year.getSelectedItem().toString().trim() )
        );

        NewTime ti=new NewTime(
                Integer.parseInt(hour.getSelectedItem().toString().trim()),
                Integer.parseInt(minute.getSelectedItem().toString().trim()),
                0
        );

        String _duration=Duration.getText().toString().trim();
        int duration=Math.max(30, Integer.parseInt(_duration.isEmpty()?"30":_duration)),
            n=Integer.parseInt(NumberOfRepetition.getSelectedItem().toString().trim());

        StringBuilder sb=new StringBuilder();


        SharedPreferences prefs=getApplicationContext().
                getSharedPreferences(MainActivity.MYPREFS, MODE_PRIVATE);
        String Page="CheckTimeFor";
        if(inp_types.getSelectedItem().toString().trim().equals("seance")) {
            int id=prefs.getInt(SEANCEGRPID, 500);
            sb.append("&group_id=").append(id);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed=prefs.edit();
            ed.putInt(SEANCEGRPID, id+1);
            ed.apply();
            Page+="Seance";
        }else {
            sb.append("&group_id");
            Page+="Task";
        }
        Page+=".php?";

        sb.append("&type=").append(inp_types.getSelectedItem().toString().trim());
        sb.append("&duration=").append(duration);
        sb.append("&monitorId=").append( Monitors.get( monitor.getSelectedItem().toString() ) );
        sb.append("&clientId=").append( Clients.get( client.getSelectedItem().toString() ) );
        sb.append("&comments=").append(Comments);
        sb.append("&title=").append(Title);
        sb.append("&detail=").append(Detail);

        for(int i=0; i<n; ++i, d.goForward(7)) {
            sb.append("&date").append(i).append("=").append(d.toString()).append("|").append(ti.toString());
        }
        JsonObjectRequest req=new JsonObjectRequest(
                Request.Method.GET,
                MainActivity.HTTP+Page+
                        "email=" + MainActivity.EmailAdmin+
                        "&password="+MainActivity.PasswordAdmin+
                        sb.toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response.has("Error")) {
                            Log.d("abc", "I am here");
                            isAddingAJob=false;
                            try {
                                Toast.makeText(add_something.this, response.getString("Error"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            JsonObjectRequest req=new JsonObjectRequest(
                                    Request.Method.GET,
                                    MainActivity.HTTP+"AddJob.php?"+
                                            "email=" + MainActivity.EmailAdmin+
                                            "&password="+MainActivity.PasswordAdmin+
                                            sb.toString(),
                                    null,
                                    new Response.Listener<JSONObject>() {
                                        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            if(response.has("msgError")) {
                                                LogOut();
                                                return;
                                            }
                                            Intent intent=new Intent(add_something.this, ManageShcedule.class);
                                            SaveDate();
                                            startActivity(intent);
                                            add_something.Comments="";
                                            add_something.Title="";
                                            add_something.Detail="";
                                            add_something.this.finish();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            isAddingAJob=false;
                                        }
                                    });

                            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isAddingAJob=false;
                    }
                });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
        /**/
    }

    public void Return(View view) {
        add_something.this.finish();
    }

    public void GoToInfoPage(View view) {
        Intent intent=new Intent(add_something.this, add_comment_or_title_and_detail.class);
        startActivity(intent);
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