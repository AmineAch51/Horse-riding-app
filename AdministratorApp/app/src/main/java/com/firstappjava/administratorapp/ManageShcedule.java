package com.firstappjava.administratorapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.datepicker.OnSelectionChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class ManageShcedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayList<TextView> items, border_bottoms;
    @SuppressLint("StaticFieldLeak")
    public static NewDate currentDate;
    private int k;
    private NewDate leftDate, rightDate;
    @SuppressLint("StaticFieldLeak")
    private ListView lv;
    private TextView test;
    private ArrayList<_Job> jobs, bj;
    private static int AllOrScienceOrTask=1; // 1 => All, 2 => Science, 3 => Task
    private static int DayOrWeek=0; // 0 => Day, 1 => Week
    public static int getAllOrScienceOrTask() {
        return AllOrScienceOrTask;
    }
    public static int getDayOrWeek() {
        return DayOrWeek;
    }
    private static boolean DidHeSave=false;
    private static final String DATAOFCURRENTWEEK="DATAOFCURRENTWEEK";
    private void RetreiveDate() {
        String SAVEDATE="SAVEDATE",
               DATE="DATE",
               DAYORWEEK="DAYORWEEK",
               ALLORSEANCEORTASK="ALLORSEANCEORTASK";
        SharedPreferences prefs=getApplicationContext().
                                getSharedPreferences(MainActivity.MYPREFS, MODE_PRIVATE);
        boolean savedate=prefs.getBoolean(SAVEDATE, false);
        if(savedate) {
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed=prefs.edit();
            ed.putBoolean(SAVEDATE, false);
            ed.apply();
            String []_date=prefs.getString(DATE, "").split("-");
            boolean isDayOrWeek=prefs.getBoolean(DAYORWEEK, false);
            int isAllOrSeanceOrTask=prefs.getInt(ALLORSEANCEORTASK, -1)-1;
            ((Spinner)findViewById(R.id.DayOrWeek)).setSelection( isDayOrWeek?1:0 );
            ((Spinner)findViewById(R.id.AllOrScienceOrTask)).setSelection(isAllOrSeanceOrTask);
            currentDate.setDate (
                    Integer.parseInt( _date[2] ),
                    Integer.parseInt( _date[1] ),
                    Integer.parseInt( _date[0] )
            );
        }
    }
    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_manage_shcedule);

        jobs=new ArrayList<>();
        bj=new ArrayList<>();

        Spinner DayOrWeek=findViewById(R.id.DayOrWeek),
                AllOrScienceOrTask=findViewById(R.id.AllOrScienceOrTask);

        ArrayAdapter<CharSequence> ad1=ArrayAdapter.createFromResource(this,
                                       R.array.option_day_or_week,
                                       android.R.layout.simple_spinner_item),
                                   ad2=ArrayAdapter.createFromResource(this,
                                       R.array.option_monitor_or_client_or_all,
                                       android.R.layout.simple_spinner_item);

        ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        DayOrWeek.setAdapter(ad1);
        AllOrScienceOrTask.setAdapter(ad2);

        DayOrWeek.setOnItemSelectedListener(this);
        AllOrScienceOrTask.setOnItemSelectedListener(this);

        items=new ArrayList<>();
        border_bottoms=new ArrayList<>();

        items.add( findViewById(R.id.item0) );
        items.add( findViewById(R.id.item1) );
        items.add( findViewById(R.id.item2) );
        items.add( findViewById(R.id.item3) );
        items.add( findViewById(R.id.item4) );
        items.add( findViewById(R.id.item5) );
        items.add( findViewById(R.id.item6) );

        border_bottoms.add( findViewById(R.id.bb0) );
        border_bottoms.add( findViewById(R.id.bb1) );
        border_bottoms.add( findViewById(R.id.bb2) );
        border_bottoms.add( findViewById(R.id.bb3) );
        border_bottoms.add( findViewById(R.id.bb4) );
        border_bottoms.add( findViewById(R.id.bb5) );
        border_bottoms.add( findViewById(R.id.bb6) );


        currentDate=new NewDate(findViewById(R.id.MonthAndDay),
                                findViewById(R.id.year));

        this.RetreiveDate();

        currentDate.UpdateTextView();
        k=currentDate.getDayByNumber();

        lv=findViewById(R.id.ShceduleList);
        findViewById(R.id.linlay1).setOnTouchListener(new OnSwipeTouchListener(ManageShcedule.this) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSwipeLeft() {
                String s=((Spinner)findViewById(R.id.DayOrWeek)).getSelectedItem().toString();
                if(s.equals("week")) {
                    Next();
                    return;
                }
                super.onSwipeLeft();
                if(currentDate.getDayByNumber()==7) {
                    Next();
                    _Items(0);
                }else {
                    _Items(currentDate.getDayByNumber());
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSwipeRight() {
                String s=((Spinner)findViewById(R.id.DayOrWeek)).getSelectedItem().toString();
                if(s.equals("week")) {
                    Prev();
                    return;
                }
                super.onSwipeRight();
                if(currentDate.getDayByNumber()==1) {
                    Prev();
                    _Items(6);
                }else {
                    _Items(currentDate.getDayByNumber()-2);
                }
            }
        });

        /*lv.setOnTouchListener(new OnSwipeTouchListener(ManageShcedule.this) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSwipeLeft() {
                String s=((Spinner)findViewById(R.id.DayOrWeek)).getSelectedItem().toString();
                if(s.equals("week")) {
                    Next();
                    return;
                }
                super.onSwipeLeft();
                if(currentDate.getDayByNumber()==7) {
                    Next();
                    _Items(0);
                }else {
                    _Items(currentDate.getDayByNumber());
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSwipeRight() {
                String s=((Spinner)findViewById(R.id.DayOrWeek)).getSelectedItem().toString();
                if(s.equals("week")) {
                    Prev();
                    return;
                }
                super.onSwipeRight();
                if(currentDate.getDayByNumber()==1) {
                    Prev();
                    _Items(6);
                }else {
                    _Items(currentDate.getDayByNumber()-2);
                }
            }
        });*/
        if(isNetworkAvailable() && !DidHeSave) {
            DidHeSave=true;
            SaveDateOfCurrentWeek();
        }
        RetreiveDateOfCurrentWeek();
    }
    private void RetreiveDateOfCurrentWeek() {
        bj.clear();
        SharedPreferences prefs=getApplicationContext().
                getSharedPreferences(MainActivity.MYPREFS, MODE_PRIVATE);
        String s=prefs.getString(DATAOFCURRENTWEEK, null);
        if(s!=null) {
            String []t=s.split("#");
            for(String str : t) {
                String []tt=str.split("\\|");
                if(tt[0].equals("sc")) {
                      Science sc = new Science(
                                        Integer.parseInt(tt[1]),
                                        Integer.parseInt(tt[2]),
                                        Integer.parseInt(tt[3]),
                                        Integer.parseInt(tt[4]),
                                        tt[5]+" "+tt[6]+":00",
                                        Integer.parseInt(tt[7]),
                                        tt[8].equals("true"),
                                        Integer.parseInt(tt[9]),
                                        tt[10].equals("&")?"":tt[10]
                      );
                      //NewDate nd=sc.getDate();
                      //sc.getDate().setDate( nd.getValueDay(), nd.getValueMonth(), nd.getValueYear() );
                      bj.add(new _Job(sc));
                      //Log.d("abc", tt[0]+" "+tt[1]+" "+tt[2]+" "+tt[3]+" "+tt[4]+" "+tt[5]+" "+tt[6]+" "+tt[7]+" "+tt[8]+" "+tt[9]+" "+tt[10]);
                }else {
                    Task ts=new Task(
                           Integer.parseInt(tt[1]),
                           tt[2]+" "+tt[3]+":00",
                           Integer.parseInt(tt[4]),
                           tt[5].equals("&")?"":tt[5],
                           tt[6].equals("&")?"":tt[6],
                           tt[7].equals("true"),
                           Integer.parseInt(tt[8])
                    );
                    bj.add(new _Job(ts));
                }
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }
        return networkInfo!=null && networkInfo.isConnected();
    }
    private void SaveDateOfCurrentWeek() {
        StringBuilder sb=new StringBuilder();
        NewDate l=currentDate.clone().WhatDateWillGonnaBeAfterGo( -currentDate.getDayByNumber()+1 ),
                r=currentDate.clone().WhatDateWillGonnaBeAfterGo( 7-currentDate.getDayByNumber() );
        StringRequest req = new StringRequest(
                Request.Method.POST,
                MainActivity.HTTP+"Sciences.php",
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String msg1) {
                        try {
                            JSONObject response=new JSONObject(msg1);

                            JSONArray t=response.getJSONArray("result");
                            JSONObject jo;
                            for(int i=0; i<t.length(); ++i) {
                                jo=t.getJSONObject(i);
                                Science sc=new Science(
                                        Integer.parseInt( jo.getString( "seanceId" ) ),
                                        Integer.parseInt( jo.getString("scienceGrpId") ),
                                        Integer.parseInt( jo.getString("clientId") ),
                                        Integer.parseInt( jo.getString("monitorId") ),
                                        jo.getString("startTime"),
                                        Integer.parseInt( jo.getString("durationMinute") ),
                                        (jo.getString("isDone").equals("1")),
                                        Integer.parseInt( jo.getString("paymentId") ),
                                        jo.getString("comment")
                                );
                                sb.append(sc.toString()).append("#");
                            }
                            StringRequest req1 = new StringRequest(
                                    Request.Method.POST,
                                    MainActivity.HTTP+"Tasks.php",
                                    new Response.Listener<String>() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onResponse(String msg1) {
                                            try {
                                                JSONObject response=new JSONObject(msg1);
                                                JSONArray t=response.getJSONArray("result");
                                                JSONObject jo;
                                                for(int i=0; i<t.length(); ++i) {
                                                    jo=t.getJSONObject(i);
                                                    Task ts=new Task(
                                                            Integer.parseInt( jo.getString("taskID") ),
                                                            jo.getString("startDate"),
                                                            Integer.parseInt( jo.getString("durationMinute") ),
                                                            jo.getString("title"),
                                                            jo.getString("detail"),
                                                            (jo.getString("isDone").equals("1")),
                                                            Integer.parseInt( jo.getString("user_Fk") )
                                                    );
                                                    sb.append(ts.toString()).append("#");
                                                }
                                                if(sb.length()!=0) {
                                                    sb.delete(sb.length()-1, sb.length());
                                                }
                                                SharedPreferences prefs=getApplicationContext().
                                                        getSharedPreferences(MainActivity.MYPREFS, MODE_PRIVATE);
                                                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed=prefs.edit();
                                                ed.putString(DATAOFCURRENTWEEK, sb.toString());
                                                ed.apply();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Log.d("abc", "Fuck");
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("abc", "FuckV");
                                        }
                                    }
                            ){
                                @Override
                                protected Map<String,String> getParams() {
                                    Map<String,String> params = new HashMap<>();
                                    params.put("email", MainActivity.EmailAdmin);
                                    params.put("password", MainActivity.PasswordAdmin);
                                    params.put("startDate", l.toString());
                                    params.put("endDate", r.toString());
                                    return params;
                                }
                            };
                            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("abc", "Fuck");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("abc", "FuckV");
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("email", MainActivity.EmailAdmin);
                params.put("password", MainActivity.PasswordAdmin);
                params.put("startDate", l.toString());
                params.put("endDate", r.toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        this.test=findViewById(R.id.test);
        //this.FetchAll();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @SuppressLint("SetTextI18n")
    private void FetchSciences() {
        jobs.clear();
        StringRequest req1 = new StringRequest(
                Request.Method.POST,
                MainActivity.HTTP+"Sciences.php",
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String msg1) {
                        try {
                            JSONObject response=new JSONObject(msg1);

                            JSONArray t=response.getJSONArray("result");
                            JSONObject jo;
                            for(int i=0; i<t.length(); ++i) {
                                jo=t.getJSONObject(i);
                                Science sc=new Science(
                                        Integer.parseInt( jo.getString( "seanceId" ) ),
                                        Integer.parseInt( jo.getString("scienceGrpId") ),
                                        Integer.parseInt( jo.getString("clientId") ),
                                        Integer.parseInt( jo.getString("monitorId") ),
                                        jo.getString("startTime"),
                                        Integer.parseInt( jo.getString("durationMinute") ),
                                        (jo.getString("isDone").equals("1")),
                                        Integer.parseInt( jo.getString("paymentId") ),
                                        jo.getString("comment")
                                );
                                jobs.add(new _Job(sc));
                            }

                            lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("email", MainActivity.EmailAdmin);
                params.put("password", MainActivity.PasswordAdmin);
                params.put("startDate", leftDate.toString());
                params.put("endDate", rightDate.toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req1);
    }
    @SuppressLint("SetTextI18n")
    private void FetchTasks() {
        jobs.clear();
        StringRequest req1 = new StringRequest(
                Request.Method.POST,
                MainActivity.HTTP+"Tasks.php",
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String msg1) {
                        try {
                            JSONObject response=new JSONObject(msg1);
                            JSONArray t=response.getJSONArray("result");
                            JSONObject jo;
                            for(int i=0; i<t.length(); ++i) {
                                jo=t.getJSONObject(i);
                                Task ts=new Task(
                                        Integer.parseInt( jo.getString("taskID") ),
                                        jo.getString("startDate"),
                                        Integer.parseInt( jo.getString("durationMinute") ),
                                        jo.getString("title"),
                                        jo.getString("detail"),
                                        (jo.getString("isDone").equals("1")),
                                        Integer.parseInt( jo.getString("user_Fk") )
                                );
                                jobs.add(new _Job(ts));
                            }
                            lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("email", MainActivity.EmailAdmin);
                params.put("password", MainActivity.PasswordAdmin);
                params.put("startDate", leftDate.toString());
                params.put("endDate", rightDate.toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req1);
    }
    @SuppressLint("SetTextI18n")
    private void FetchAll() {
        /*if(AllOrScienceOrTask==2) {
            this.FetchSciences();
            return;
        }
        if(AllOrScienceOrTask==3) {
            this.FetchTasks();
            return;
        }*/
        if(isNetworkAvailable() && !MainActivity.NormalConnect) {
            Intent intent=new Intent(ManageShcedule.this, MainActivity.class);
            startActivity(intent);
            ManageShcedule.this.finish();
            return;
        }
        jobs.clear();
        StringRequest req = new StringRequest(
                Request.Method.POST,
                MainActivity.HTTP+"Sciences.php",
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String msg) {
                        try {
                            JSONObject response=new JSONObject(msg);
                            if(response.has("msgError")) {
                                LogOut();
                                return;
                            }
                            JSONArray t=response.getJSONArray("result");
                            JSONObject jo;
                            for(int i=0; i<t.length(); ++i) {
                                jo=t.getJSONObject(i);
                                Science sc=new Science(
                                        Integer.parseInt( jo.getString( "seanceId" ) ),
                                        Integer.parseInt( jo.getString("scienceGrpId") ),
                                        Integer.parseInt( jo.getString("clientId") ),
                                        Integer.parseInt( jo.getString("monitorId") ),
                                        jo.getString("startTime"),
                                        Integer.parseInt( jo.getString("durationMinute") ),
                                        (jo.getString("isDone").equals("1")),
                                        Integer.parseInt( jo.getString("paymentId") ),
                                        jo.getString("comment")
                                );
                                jobs.add(new _Job(sc));
                            }

                            StringRequest req1 = new StringRequest(
                                    Request.Method.POST,
                                    MainActivity.HTTP+"Tasks.php",
                                    new Response.Listener<String>() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onResponse(String msg1) {
                                            try {
                                                JSONObject response=new JSONObject(msg1);
                                                if(response.has("msgError")) {
                                                    LogOut();
                                                    return;
                                                }
                                                JSONArray t=response.getJSONArray("result");
                                                JSONObject jo;
                                                for(int i=0; i<t.length(); ++i) {
                                                    jo=t.getJSONObject(i);
                                                    Task ts=new Task(
                                                            Integer.parseInt( jo.getString("taskID") ),
                                                            jo.getString("startDate"),
                                                            Integer.parseInt( jo.getString("durationMinute") ),
                                                            jo.getString("title"),
                                                            jo.getString("detail"),
                                                            (jo.getString("isDone").equals("1")),
                                                            Integer.parseInt( jo.getString("user_Fk") )
                                                    );
                                                    jobs.add(new _Job(ts));
                                                }
                                                lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }
                            ){
                                @Override
                                protected Map<String,String> getParams() {
                                    Map<String,String> params = new HashMap<>();
                                    params.put("email", MainActivity.EmailAdmin);
                                    params.put("password", MainActivity.PasswordAdmin);
                                    params.put("startDate", leftDate.toString());
                                    params.put("endDate", rightDate.toString());
                                    return params;
                                }
                            };
                            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("email", MainActivity.EmailAdmin);
                params.put("password", MainActivity.PasswordAdmin);
                params.put("startDate", leftDate.toString());
                params.put("endDate", rightDate.toString());
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<_Job> SetCurrentItems() {
        test.setText("");
        ArrayList<_Job> arr;
        if(isNetworkAvailable()) {
            arr=new ArrayList<>(jobs);
        }else {
            arr=new ArrayList<>(bj);
        }
        String s=((Spinner)findViewById(R.id.DayOrWeek)).getSelectedItem().toString();
        ArrayList<_Job> result=new ArrayList<>();
        Job jb;
        for(int i = 0; i< arr.size(); ++i) {
            jb= arr.get(i).getVal();
            if((AllOrScienceOrTask==2 && jb.Typeof().equals("Task")) ||
               (AllOrScienceOrTask==3 && jb.Typeof().equals("Science"))) {
                continue;
            }
            if(s.equals("day")) {
                if(currentDate.equals(jb.getDate())) {
                    result.add(new _Job(jb));
                }
            }else {
                NewDate []t=new NewDate[7];
                t[0]=currentDate.clone();
                t[1]=currentDate.WhatDateWillGonnaBeAfterGo(1);
                t[2]=currentDate.WhatDateWillGonnaBeAfterGo(2);
                t[3]=currentDate.WhatDateWillGonnaBeAfterGo(3);
                t[4]=currentDate.WhatDateWillGonnaBeAfterGo(4);
                t[5]=currentDate.WhatDateWillGonnaBeAfterGo(5);
                t[6]=currentDate.WhatDateWillGonnaBeAfterGo(6);
                for(NewDate newDate : t) {
                    if(jb.getDate().equals(newDate)) {
                        result.add(new _Job(jb));
                        break;
                    }
                }
            }
        }
        Log.d("abc", String.valueOf(result.size()));
        result.sort((_Job a, _Job b)-> {
            NewDate x=a.getVal().getDate(),
                    y=b.getVal().getDate();
            if(x.getValueYear()>y.getValueYear()) { return 1; }
            if(x.getValueYear()<y.getValueYear()) { return -1; }
            if(x.getValueMonth()>y.getValueMonth()) { return 1; }
            if(x.getValueMonth()<y.getValueMonth()) { return -1; }
            if(x.getValueDay()>y.getValueDay()) { return 1; }
            if(x.getValueDay()<y.getValueDay()) { return -1; }
            NewTime _x=a.getVal().getStartTime(),
                    _y=b.getVal().getStartTime();
            if(_x.getHour()>_y.getHour()) { return 1; }
            if(_x.getHour()<_y.getHour()) { return -1; }
            if(_x.getMinute()>_y.getMinute()) { return 1; }
            if(_x.getMinute()<_y.getMinute()) { return -1; }
            return Integer.compare(_x.getSecond(), _y.getSecond());
        });
        HashSet<Integer> se1=new HashSet<>(),
                         se2=new HashSet<>();
        ArrayList<_Job> r=new ArrayList<>();
        for(int i=0; i<result.size(); ++i) {
            if(result.get(i).getVal().Typeof().equals("Science")) {
                Science sc=(Science)result.get(i).getVal();
                if(se1.contains(sc.getScienceId())) {
                    continue;
                }
                r.add(result.get(i));
                se1.add(sc.getScienceId());
            }else {
                Task ts=(Task)result.get(i).getVal();
                if(se2.contains(ts.getTaskID())) {
                    continue;
                }
                r.add(result.get(i));
                se2.add(ts.getTaskID());
            }
        }
        return r;
    }

    @SuppressLint("SetTextI18n")
    private void BuildWeeks() {
        currentDate.goBackWard( Math.abs(1-k) );
        currentDate.UpdateTextView();
        NewDate nd;
        for(int i=0; i<7; ++i) {
            nd=currentDate.WhatDateWillGonnaBeAfterGo( 7*i );
            //items.get(i).setText(nd.getDay()+"/"+nd.getMonthByNumberString());
            items.get(i).setText(nd.getMonthByNumberString()+"/"+nd.getDay());
        }
        SetItem(currentDate.getDayByNumber()-1);
        k=1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.DayOrWeek: {
                String s=parent.getItemAtPosition(position).toString();
                if(s.equals("day")) {
                    DayOrWeek=0;
                    items.get(0).setText("Lun");
                    items.get(1).setText("Mar");
                    items.get(2).setText("Mer");
                    items.get(3).setText("Jeu");
                    items.get(4).setText("Ven");
                    items.get(5).setText("Sam");
                    items.get(6).setText("Dim");
                    SetItem( currentDate.getDayByNumber()-1 );
                    k=currentDate.getDayByNumber();

                    leftDate=new NewDate(currentDate);
                    rightDate=new NewDate(currentDate);
                    leftDate.goBackWard( currentDate.getDayByNumber()-1 );
                    rightDate.goForward( 7-currentDate.getDayByNumber() );

                    this.FetchAll();
                    lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );

                }else {
                    DayOrWeek=1;
                    Log.d("abc", "I am here");
                    this.BuildWeeks();

                    leftDate=new NewDate(currentDate);
                    leftDate.goBackWard( currentDate.getDayByNumber()-1 );
                    rightDate=new NewDate(leftDate);
                    rightDate.goForward( 42 );

                    jobs.clear();
                    lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
                    this.FetchAll();
                    lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
                }
            }break;
            case R.id.AllOrScienceOrTask: {
                lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, new ArrayList<_Job>()) );
                String s=parent.getItemAtPosition(position).toString();
                if(s.equals("seance")) {
                    AllOrScienceOrTask=2;
                }else if(s.equals("task")) {
                    AllOrScienceOrTask=3;
                }else {
                    AllOrScienceOrTask=1;
                }
                lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, new ArrayList<_Job>()) );
                lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
            }break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @SuppressLint("WrongConstant")
    private void SetItem(int itemIndex) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/ibmplexsanscondensedbold.ttf");
        for(int i=0; i<items.size(); ++i) {
            if(itemIndex==i) {
                border_bottoms.get(i).setAlpha(1f);
                items.get(i).setTypeface(typeface);
            }else {
                border_bottoms.get(i).setAlpha(0f);
                items.get(i).setTypeface(Typeface.DEFAULT);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void _Items(int id) {
        if(isNetworkAvailable() && !MainActivity.NormalConnect) {
            Intent intent=new Intent(ManageShcedule.this, MainActivity.class);
            startActivity(intent);
            ManageShcedule.this.finish();
            return;
        }
        String s=((Spinner)findViewById(R.id.DayOrWeek)).getSelectedItem().toString();
        int step=0, curr=k;
        switch (id) {
            case 0: {
                step=1-k;
                if(s.equals("day")) {
                    currentDate.go(step);
                }else {
                    currentDate.go(step*7);
                }
                k=1;
            }break;
            case 1: {
                step=2-k;
                if(s.equals("day")) {
                    currentDate.go(step);
                }else {
                    currentDate.go(step*7);
                }
                k=2;
            }break;
            case 2: {
                step=3-k;
                if(s.equals("day")) {
                    currentDate.go(step);
                }else {
                    currentDate.go(step*7);
                }
                k=3;
            }break;
            case 3: {
                step=4-k;
                if(s.equals("day")) {
                    currentDate.go(step);
                }else {
                    currentDate.go(step*7);
                }
                k=4;
            }break;
            case 4: {
                step=5-k;
                if(s.equals("day")) {
                    currentDate.go(step);
                }else {
                    currentDate.go(step*7);
                }
                k=5;
            }break;
            case 5: {
                step=6-k;
                if(s.equals("day")) {
                    currentDate.go(step);
                }else {
                    currentDate.go(step*7);
                }
                k=6;
            }break;
            case 6: {
                step=7-k;
                if(s.equals("day")) {
                    currentDate.go(step);
                }else {
                    currentDate.go(step*7);
                }
                k=7;
            }break;
            default:{ /*Nothing*/ }
        }
        currentDate.UpdateTextView();
        SetItem( curr+step-1 );
        lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"NonConstantResourceId", "WrongConstant"})
    public void Items(View view) {
        if(view.getId()==R.id.item0) {
            _Items(0);
        }else if(view.getId()==R.id.item1) {
            _Items(1);
        }else if(view.getId()==R.id.item2) {
            _Items(2);
        }else if(view.getId()==R.id.item3) {
            _Items(3);
        }else if(view.getId()==R.id.item4) {
            _Items(4);
        }else if(view.getId()==R.id.item5) {
            _Items(5);
        }else if(view.getId()==R.id.item6) {
            _Items(6);
        }
    }
    @SuppressLint("SetTextI18n")
    private void SetNextWeeks() {
        NewDate nd=leftDate.clone();
        for(int i=0; i<7; ++i, nd.goForward(7)) {
            //items.get(i).setText(nd.getDay()+"/"+nd.getMonthByNumberString());
            items.get(i).setText(nd.getMonthByNumberString()+"/"+nd.getDay());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Next()  {
        if(isNetworkAvailable() && !MainActivity.NormalConnect) {
            Intent intent=new Intent(ManageShcedule.this, MainActivity.class);
            startActivity(intent);
            ManageShcedule.this.finish();
            return;
        }
        lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, new ArrayList<_Job>()) );
        currentDate.goForward(7);
        currentDate.UpdateTextView();
        String s=((Spinner)findViewById(R.id.DayOrWeek)).getSelectedItem().toString();
        if(s.equals("week")) {
            if(k==7) {
                k=1;
                leftDate=new NewDate(currentDate);
                rightDate=new NewDate(leftDate);
                rightDate.goForward( 42 );
                SetNextWeeks();
                this.FetchAll();
            }else {
                ++k;
            }
            SetItem(k-1);
            lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
        }else {
            leftDate=new NewDate(currentDate);
            rightDate=new NewDate(currentDate);
            leftDate.goBackWard( currentDate.getDayByNumber()-1 );
            rightDate.goForward( 7-currentDate.getDayByNumber() );
            this.FetchAll();
        }
        if(!isNetworkAvailable()) {
            lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Next(View view) {
        this.Next();
    }

    @SuppressLint("SetTextI18n")
    private void SetPreviousWeeks() {
        NewDate nd=leftDate.clone();
        for(int i=0; i<7; ++i, nd.goForward(7)) {
            //items.get(i).setText(nd.getDay()+"/"+nd.getMonthByNumberString());
            items.get(i).setText(nd.getMonthByNumberString()+"/"+nd.getDay());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Prev() {
        if(isNetworkAvailable() && !MainActivity.NormalConnect) {
            Intent intent=new Intent(ManageShcedule.this, MainActivity.class);
            startActivity(intent);
            ManageShcedule.this.finish();
            return;
        }
        lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, new ArrayList<_Job>()) );
        currentDate.goBackWard(7);
        currentDate.UpdateTextView();
        String s=((Spinner)findViewById(R.id.DayOrWeek)).getSelectedItem().toString();
        if(s.equals("week")) {
            if(k==1) {
                k=7;
                rightDate=new NewDate(currentDate);
                leftDate=new NewDate(rightDate);
                leftDate.goBackWard( 42 );
                SetPreviousWeeks();
                this.FetchAll();
            }else {
                --k;
            }
            SetItem(k-1);
            lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
        }else {
            leftDate=new NewDate(currentDate);
            rightDate=new NewDate(currentDate);
            leftDate.goBackWard( currentDate.getDayByNumber()-1 );
            rightDate.goForward( 7-currentDate.getDayByNumber() );
            this.FetchAll();
        }
        if(!isNetworkAvailable()) {
            lv.setAdapter( new MyShceduleAdapter(ManageShcedule.this, SetCurrentItems()) );
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Prev(View view) {
        this.Prev();
    }

    @SuppressLint("NonConstantResourceId")
    public void Move(View view) {
        switch(view.getId()) {
            case R.id.plus_button: {
                Intent intent=new Intent(ManageShcedule.this, add_something.class);
                startActivity(intent);
            }break;
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
        ManageShcedule.this.finish();
    }
    public void LogOut(View view) {
        LogOut();
    }
}