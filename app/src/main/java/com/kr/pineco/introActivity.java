package com.kr.pineco;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseUser;

public class introActivity extends AppCompatActivity {
    FirebaseUser currentUser;

    Toolbar introActivityToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        introActivityToolbar=findViewById(R.id.introActivityToolbar);
        setSupportActionBar(introActivityToolbar);
        getSupportActionBar().setTitle("Fill your Details");

    }

    @Override
    public void onBackPressed() {

    }
}
