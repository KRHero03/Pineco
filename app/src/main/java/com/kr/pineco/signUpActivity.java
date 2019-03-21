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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;


public class signUpActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference dbRef;
    Toolbar signUpActivityToolbar;
    Button signUpRegister;
    EditText signUpUsername,signUpPassword,signUpEmail;
    //CountryCodePicker signUpCCP;
    String username,email,password,UID,deviceToken;
    ProgressDialog signUpProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpActivityToolbar = findViewById(R.id.signUpToolbar);
        setSupportActionBar(signUpActivityToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        signUpRegister = findViewById(R.id.signUpRegister);
        signUpUsername=findViewById(R.id.signUpUsername);
        signUpPassword=findViewById(R.id.signUpPassword);
        signUpEmail=findViewById(R.id.signUpEmail);
        //signUpCCP = findViewById(R.id.signUpCCP);
        //signUpCCP.setCountryForPhoneCode(91);

        mAuth=FirebaseAuth.getInstance();

        signUpProgressDialog=new ProgressDialog(this);

        signUpRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                username = signUpUsername.getText().toString();
                password = signUpPassword.getText().toString();
                email = signUpPassword.getText().toString();

                username = username.trim();
                password = password.trim();
                email = email.trim();

                signUpProgressDialog.setTitle("Registering User");
                signUpProgressDialog.setMessage("Please wait while you are registered...");
                signUpProgressDialog.setCancelable(false);
                signUpProgressDialog.show();

                createUser(email,password,username);

            }
        });
    }


    private void createUser(String email, String password, final String username){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        signUpProgressDialog.dismiss();
                        if (task.isSuccessful()) {


                            currentUser=mAuth.getCurrentUser();
                            currentUser.sendEmailVerification().addOnSuccessListener(signUpActivity.this, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    setDBValue(username);
                                    Toast.makeText(signUpActivity.this,"Please verify your email before Logging in!",Toast.LENGTH_SHORT).show();
                                    Intent loginActivityIntent=new Intent(signUpActivity.this,MainActivity.class);
                                    loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(loginActivityIntent);
                                    finish();
                                }
                            }).addOnFailureListener(signUpActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(signUpActivity.this,"Invalid Email ID Entered!",Toast.LENGTH_SHORT).show();
                                    currentUser.delete();
                                }
                            });




                        } else {
                            Log.d("FAILED",task.toString());
                            Toast.makeText(signUpActivity.this, "Registration failed!",
                                    Toast.LENGTH_LONG).show();
                            signUpProgressDialog.hide();

                        }



                    }
                });
    }

    private void setDBValue(String Username){
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        UID=currentUser.getUid();
        dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        deviceToken=FirebaseInstanceId.getInstance().getToken();
        HashMap<String,String> userInfo=new HashMap<>();
        userInfo.put("Username",Username);
        userInfo.put("Email",email);
        userInfo.put("UID",UID);
        userInfo.put("DeviceToken",deviceToken);
        userInfo.put("ProifleImage","default");
        dbRef.setValue(userInfo);


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
