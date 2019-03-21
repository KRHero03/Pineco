package com.kr.pineco;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        currentUser= mAuth.getCurrentUser();

        if(currentUser==null || !currentUser.isEmailVerified()){
           sendToStart();
        }else{

        }

    }

    private void sendToStart(){
        Intent startActivityIntent=new Intent(MainActivity.this,startActivity.class);
        startActivity(startActivityIntent);
        finish();
    }


}
