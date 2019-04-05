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
    EditText signUpPassword,signUpEmail;
    //CountryCodePicker signUpCCP;
    String email,password,UID,deviceToken;
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
        signUpPassword=findViewById(R.id.signUpPassword);
        signUpEmail=findViewById(R.id.signUpEmail);
        //signUpCCP = findViewById(R.id.signUpCCP);
        //signUpCCP.setCountryForPhoneCode(91);

        mAuth=FirebaseAuth.getInstance();

        signUpProgressDialog=new ProgressDialog(this);

        signUpRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                password = signUpPassword.getText().toString();
                email = signUpEmail.getText().toString();
                password = password.trim();
                email = email.trim();

                signUpProgressDialog.setTitle("Registering User");
                signUpProgressDialog.setMessage("Gathering fruits...");
                signUpProgressDialog.setCancelable(false);
                signUpProgressDialog.show();

                createUser(email,password);

            }
        });
    }


    private void createUser(String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {

                        signUpProgressDialog.dismiss();
                        if (task.isSuccessful()) {


                            currentUser=mAuth.getCurrentUser();
                            currentUser.sendEmailVerification().addOnSuccessListener(signUpActivity.this, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    setDBValue();
                                    Toast.makeText(signUpActivity.this,"Account Successfully Created! Please verify your email before Logging in!",Toast.LENGTH_LONG).show();
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
                            Log.d("FAILURE123",task.getException().getMessage());
                            Toast.makeText(signUpActivity.this, "Registration failed!",
                                    Toast.LENGTH_LONG).show();
                            signUpProgressDialog.hide();

                        }



                    }
                });
    }

    private void setDBValue(){
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        UID=currentUser.getUid();
        dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        deviceToken=FirebaseInstanceId.getInstance().getToken();
        HashMap<String,String> userInfo=new HashMap<>();
        userInfo.put("Email",email);
        userInfo.put("UID",UID);
        userInfo.put("DeviceToken",deviceToken);
        userInfo.put("ProfileImage","default");
        userInfo.put("AccessCode","0");
        dbRef.setValue(userInfo);
        FirebaseDatabase.getInstance().getReference().child("Promotions").child(currentUser.getUid()).child("FIRST50").setValue("0.5");
        FirebaseAuth.getInstance().signOut();

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
