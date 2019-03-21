package com.kr.pineco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class startActivity extends AppCompatActivity {

    Button startActivityLogin,startActivitySignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startActivityLogin=findViewById(R.id.startActivityLogin);
        startActivitySignUp=findViewById(R.id.startActivitySignUp);

        startActivityLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

            }
        });

        startActivitySignUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent signUpActivityIntent=new Intent(startActivity.this,signUpActivity.class);
                startActivity(signUpActivityIntent);
            }
        });
        
    }
}
