package com.firstappjava.administratorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import java.util.Objects;

public class modify_seance extends AppCompatActivity {

    private static int SeanceId, SeanceGrpId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_modify_seance);

        SeanceId=getIntent().getIntExtra("SeanceId", -1);
        SeanceGrpId=getIntent().getIntExtra("SeanceGrpId", -1);
    }
}