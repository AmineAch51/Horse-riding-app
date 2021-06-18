package com.firstappjava.administratorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import java.util.Objects;

public class add_comment_or_title_and_detail extends AppCompatActivity {

    private EditText title, detail, comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_add_comment_or_title_and_detail);
        comment=findViewById(R.id.comment);
        title=findViewById(R.id.title);
        detail=findViewById(R.id.detail);
        if(add_something.inp_types.getSelectedItem().toString().trim().equals("seance")) {
            comment.setVisibility(View.VISIBLE);
            title.setVisibility(View.INVISIBLE);
            detail.setVisibility(View.INVISIBLE);
        }else {
            comment.setVisibility(View.INVISIBLE);
            title.setVisibility(View.VISIBLE);
            detail.setVisibility(View.VISIBLE);
        }
        comment.setText( add_something.Comments );
        title.setText( add_something.Title );
        detail.setText( add_something.Detail );
    }

    public void Return(View view) {
        add_comment_or_title_and_detail.this.finish();
    }

    public void Save(View view) {
        add_something.Comments=comment.getText().toString();
        add_something.Title=title.getText().toString();
        add_something.Detail=detail.getText().toString();
        add_comment_or_title_and_detail.this.finish();
    }
}