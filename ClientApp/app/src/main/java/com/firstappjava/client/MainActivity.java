package com.firstappjava.client;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String HTTP="http://192.168.1.5:84/WebService/";
    public static final String MYPREFS="myprefs",
            PREF_AUTOLOG="pref_autolog",
            PREF_LOGIN="pref_login",
            PREF_PASSWD="pref_passwd";

    public static String EmailAdmin="",
            PasswordAdmin="";

    public static boolean NormalConnect=false;
    public static int ClientId;
    LottieAnimationView openingAnimation, noInternetAnimation;
    private EditText email, password;
    private Button connectBt, tryAgain;
    private TextView error_msg;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch StayConnected;
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }
        return networkInfo!=null && networkInfo.isConnected();
    }
    private void NoInternetConnectionAnimation() {
        error_msg.setAlpha(0);
        noInternetAnimation.setVisibility(View.VISIBLE);
        tryAgain.setVisibility(View.VISIBLE);
        noInternetAnimation.setAlpha(0f);
        tryAgain.setAlpha(0f);
        final long TimermilisInFutute=500;
        CountDownTimer timer2=new CountDownTimer(TimermilisInFutute, 15) {
            @Override
            public void onTick(long millisUntilFinished) {
                float opacity=(float)(TimermilisInFutute-millisUntilFinished)/TimermilisInFutute;
                noInternetAnimation.setAlpha(opacity);
                tryAgain.setAlpha(opacity);
                openingAnimation.setAlpha(1f-opacity);
            }
            @Override
            public void onFinish() {
                noInternetAnimation.setAlpha(1f);
                tryAgain.setAlpha(1f);
                openingAnimation.setVisibility(View.INVISIBLE);
            }
        }.start();
    }
    private void _OpeningAnimation() {
        final long TimermilisInFutute=500;
        email.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
        connectBt.setVisibility(View.VISIBLE);
        StayConnected.setVisibility(View.VISIBLE);
        CountDownTimer timer1=new CountDownTimer(TimermilisInFutute, 15) {
            @Override
            public void onTick(long millisUntilFinished) {
                float opacity=(float)(TimermilisInFutute-millisUntilFinished)/TimermilisInFutute;
                email.setAlpha(opacity);
                password.setAlpha(opacity);
                connectBt.setAlpha(opacity);
                StayConnected.setAlpha(opacity);
                openingAnimation.setAlpha(1f-opacity);
            }
            @Override
            public void onFinish() {
                email.setAlpha(1f);
                password.setAlpha(1f);
                connectBt.setAlpha(1f);
                StayConnected.setAlpha(1f);
                openingAnimation.setVisibility(View.INVISIBLE);
            }
        }.start();
    }
    private static boolean NoInternetAnimationStarted=false, InternetBack=false;
    private void OpeningAnimation() {
        error_msg.setAlpha(0);
        openingAnimation.setAlpha(1f);
        CountDownTimer timer0 = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                SharedPreferences prefs=getApplicationContext().
                        getSharedPreferences(MYPREFS, MODE_PRIVATE);
                //@SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed=prefs.edit();
                //ed.putBoolean(PREF_AUTOLOG, false);
                //ed.apply();
                boolean autologin=prefs.getBoolean(PREF_AUTOLOG, false);

                if(!isNetworkAvailable()) {
                    MainActivity.NormalConnect=false;
                    if(autologin) {
                        Intent intent=new Intent(MainActivity.this, ManageShcedule.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                    }else {
                        NoInternetConnectionAnimation();
                    }
                    return;
                }
                if(autologin) {
                    String _email=prefs.getString(PREF_LOGIN, ""),
                            _password=prefs.getString(PREF_PASSWD, "");
                    _Connect(_email, _password, false);
                }else {
                    _OpeningAnimation();
                }
            }
        }.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onResume() {
        super.onResume();
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        error_msg=findViewById(R.id.error_msg);
        connectBt=findViewById(R.id.connectBt);
        StayConnected=findViewById(R.id.StayConnected);
        tryAgain=findViewById(R.id.try_again_button);
        openingAnimation = findViewById(R.id.loading_animation);
        noInternetAnimation=findViewById(R.id.no_internet_animation);
        TextWatcher emailWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                error_msg.setAlpha(0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        email.addTextChangedListener(emailWatcher);
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                error_msg.setAlpha(0);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        password.addTextChangedListener(passwordWatcher);
        this.OpeningAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    public void _Connect(String _email, String _password, boolean ShowError) {
        /*JsonObjectRequest req=new JsonObjectRequest(
                Request.Method.GET,
                HTTP+"AdminConnect.php?email="+_email+"&password="+_password,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.has("msgError")) {
                                _OpeningAnimation();
                                if(!ShowError) {
                                    return;
                                }
                                String msgError=response.getString("msgError");
                                if(!msgError.equals("Email or password is not valid")) {
                                    Toast.makeText(MainActivity.this, msgError, Toast.LENGTH_LONG).show();
                                }else {
                                    error_msg.setAlpha(1f);
                                }
                            }else {
                                if(ShowError) {
                                    SharedPreferences prefs=getApplicationContext().
                                            getSharedPreferences(MYPREFS, MODE_PRIVATE);

                                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed=prefs.edit();
                                    if(StayConnected.isChecked()) {
                                        ed.putBoolean(PREF_AUTOLOG, true);
                                        ed.putString(PREF_LOGIN, _email);
                                        ed.putString(PREF_PASSWD, _password);
                                    }else {
                                        ed.putBoolean(PREF_AUTOLOG, false);
                                    }
                                    ed.apply();
                                }
                                MainActivity.EmailAdmin=_email;
                                MainActivity.PasswordAdmin=_password;
                                Intent intent=new Intent(MainActivity.this, ManageShcedule.class);
                                startActivity(intent);
                                MainActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            _OpeningAnimation();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(ShowError && !Objects.requireNonNull(error.getMessage()).isEmpty()) {
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req);*/
        StringRequest req = new StringRequest(
                Request.Method.POST,
                MainActivity.HTTP+"ClientConnect.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String msg) {
                        try {
                            JSONObject response=new JSONObject(msg);
                            if(response.has("msgError")) {
                                _OpeningAnimation();
                                if(!ShowError) {
                                    return;
                                }
                                String msgError=response.getString("msgError");
                                if(!msgError.equals("Email or password is not valid")) {
                                    Toast.makeText(MainActivity.this, msgError, Toast.LENGTH_LONG).show();
                                }else {
                                    error_msg.setAlpha(1f);
                                }
                            }else {
                                if(ShowError) {
                                    SharedPreferences prefs=getApplicationContext().
                                            getSharedPreferences(MYPREFS, MODE_PRIVATE);

                                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed=prefs.edit();
                                    if(StayConnected.isChecked()) {
                                        ed.putBoolean(PREF_AUTOLOG, true);
                                        ed.putString(PREF_LOGIN, _email);
                                        ed.putString(PREF_PASSWD, _password);
                                    }else {
                                        ed.putBoolean(PREF_AUTOLOG, false);
                                    }
                                    ed.apply();
                                }
                                MainActivity.EmailAdmin=_email;
                                MainActivity.PasswordAdmin=_password;
                                MainActivity.NormalConnect=true;
                                MainActivity.ClientId=Integer.parseInt(response.getString("Connected"));
                                Intent intent=new Intent(MainActivity.this, ManageShcedule.class);
                                startActivity(intent);
                                MainActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            _OpeningAnimation();
                            e.printStackTrace();
                            Log.d("abc", "Error : "+e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(ShowError && !Objects.requireNonNull(error.getMessage()).isEmpty()) {
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ){
            @Override
            protected Map<String,String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("email", _email);
                params.put("password", _password);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
    }
    public void Connect(View view) {
        //Intent intent=new Intent(MainActivity.this, ManageShcedule.class);
        //startActivity(intent);

        if(!isNetworkAvailable()) {
            if(NoInternetAnimationStarted) {
                return;
            }
            NoInternetAnimationStarted=true;
            noInternetAnimation.setVisibility(View.VISIBLE);
            tryAgain.setVisibility(View.VISIBLE);
            final long TimermilisInFutute=500;
            CountDownTimer timer4=new CountDownTimer(TimermilisInFutute, 15) {
                @Override
                public void onTick(long millisUntilFinished) {
                    float opacity=(float)(TimermilisInFutute-millisUntilFinished)/TimermilisInFutute;
                    noInternetAnimation.setAlpha(opacity);
                    tryAgain.setAlpha(opacity);
                    email.setAlpha(1f-opacity);
                    password.setAlpha(1f-opacity);
                    connectBt.setAlpha(1f-opacity);
                    StayConnected.setAlpha(1f-opacity);
                }
                @Override
                public void onFinish() {
                    noInternetAnimation.setAlpha(1f);
                    tryAgain.setAlpha(1f);
                    email.setVisibility(View.INVISIBLE);
                    password.setVisibility(View.INVISIBLE);
                    connectBt.setVisibility(View.INVISIBLE);
                    StayConnected.setVisibility(View.INVISIBLE);
                    NoInternetAnimationStarted=false;
                }
            }.start();
            return;
        }
        this._Connect(email.getText().toString(), password.getText().toString(), true);
    }

    public void TryAgain(View view) {
        error_msg.setAlpha(0);
        if(isNetworkAvailable()) {
            if(InternetBack) {
                return;
            }
            InternetBack=true;
            openingAnimation.setVisibility(View.VISIBLE);
            final long TimermilisInFutute=500;
            CountDownTimer timer3=new CountDownTimer(TimermilisInFutute, 15) {
                @Override
                public void onTick(long millisUntilFinished) {
                    float opacity=(float)(TimermilisInFutute-millisUntilFinished)/TimermilisInFutute;
                    openingAnimation.setAlpha(opacity);
                    noInternetAnimation.setAlpha(1f-opacity);
                    tryAgain.setAlpha(1f-opacity);
                }
                @Override
                public void onFinish() {
                    openingAnimation.setAlpha(1f);
                    noInternetAnimation.setVisibility(View.INVISIBLE);;
                    tryAgain.setVisibility(View.INVISIBLE);
                    OpeningAnimation();
                    InternetBack=false;
                }
            }.start();
        }else {
            Toast.makeText(this, "No connection network", Toast.LENGTH_LONG).show();
        }
    }
}