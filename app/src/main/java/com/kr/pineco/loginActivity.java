package com.kr.pineco;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    Toolbar loginToolbar;
    EditText loginEmail,loginPassword;
    Button loginLogin;
    String email,password;
    ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginToolbar=findViewById(R.id.loginToolbar);
        setSupportActionBar(loginToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth=FirebaseAuth.getInstance();

        loginEmail=findViewById(R.id.loginEmail);
        loginPassword=findViewById(R.id.loginPassword);
        loginLogin=findViewById(R.id.loginLogin);

        loginProgressDialog=new ProgressDialog(this);
        loginProgressDialog.setTitle("Logging In");
        loginProgressDialog.setMessage("Fresh fruits loading...");
        loginProgressDialog.setCancelable(false);

        loginLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                email=loginEmail.getText().toString();
                password=loginPassword.getText().toString();

                loginProgressDialog.show();

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginProgressDialog.dismiss();
                        if(task.isSuccessful()){
                            Intent mainActivityIntent=new Intent(loginActivity.this,MainActivity.class);
                            startActivity(mainActivityIntent);
                            finish();
                        }else{
                            Toast.makeText(loginActivity.this,"Logging In Failed!",Toast.LENGTH_SHORT).show();
                            Log.d("FAILED",task.getException().getMessage());
                        }
                    }
                });

            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case android.R.id.home:

                onBackPressed();

                return true;
            default:

        }

        return true;
    }
}
