package com.kr.pineco;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    Button mainActivityLogin,mainActivitySignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityLogin=findViewById(R.id.mainActivityLogin);
        mainActivitySignUp=findViewById(R.id.mainActivitySignUp);

        mainActivityLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

            }
        });

        mainActivitySignUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

            }
        });

    }


}
