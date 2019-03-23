package com.kr.pineco;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class startActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser currentUser;
    String UID;
    DatabaseReference dbRef;
    String deviceToken;

    Button startActivityLogin,startActivitySignUp;
    ImageButton startActivityGoogleSignIn;
    ProgressDialog startActivityProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth=FirebaseAuth.getInstance();

        startActivityLogin=findViewById(R.id.startActivityLogin);
        startActivitySignUp=findViewById(R.id.startActivitySignUp);
        startActivityProgressDialog=new ProgressDialog(this);
        startActivityGoogleSignIn=findViewById(R.id.startActivityGoogleSignIn);

        startActivityLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent loginActivityIntent=new Intent(startActivity.this,loginActivity.class);
                startActivity(loginActivityIntent);
            }
        });

        startActivitySignUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent signUpActivityIntent=new Intent(startActivity.this,signUpActivity.class);
                startActivity(signUpActivityIntent);
            }
        });

        startActivityGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 9001);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        startActivityProgressDialog.setTitle("Logging In");
        startActivityProgressDialog.setMessage("Packing up Fruits for Delivery...");
        startActivityProgressDialog.setCancelable(false);
        startActivityProgressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser=mAuth.getCurrentUser();
                            setDBValue();
                            Intent mainActivityIntent=new Intent(startActivity.this,MainActivity.class);
                            startActivity(mainActivityIntent);
                            finish();
                        } else {
                        }

                        startActivityProgressDialog.hide();
                    }
                });
    }

    private void setDBValue(){
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        UID=currentUser.getUid();
        dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        deviceToken= FirebaseInstanceId.getInstance().getToken();
        HashMap<String,String> userInfo=new HashMap<>();
        userInfo.put("Username","");
        userInfo.put("Email",currentUser.getEmail());
        userInfo.put("UID",UID);
        userInfo.put("DeviceToken",deviceToken);
        userInfo.put("ProfileImage","default");
        userInfo.put("AccessCode","0");
        dbRef.setValue(userInfo);


    }
}
