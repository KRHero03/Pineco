package com.kr.pineco;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class signUpActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Toolbar signUpActivityToolbar;
    Button signUpSubmit;
    EditText signUpEditTextPhone;
    CountryCodePicker signUpCCP;
    String CCP,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpActivityToolbar = findViewById(R.id.signUpToolbar);
        setSupportActionBar(signUpActivityToolbar);
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        signUpSubmit = findViewById(R.id.signUpSubmit);
        signUpEditTextPhone = findViewById(R.id.signUpEditTextPhone);
        signUpCCP = findViewById(R.id.signUpCCP);
        signUpCCP.setCountryForPhoneCode(91);


        signUpSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CCP = signUpCCP.getSelectedCountryCode();
                phone = signUpEditTextPhone.getText().toString();
                String fullPhoneNumber=CCP+phone;
                OnVerificationStateChangedCallbacks mCallBacks = new OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {


                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                    }
                };
                PhoneAuthProvider.getInstance().verifyPhoneNumber(fullPhoneNumber, 60, TimeUnit.SECONDS, this, mCallBacks);


            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            currentUser = task.getResult().getUser();
                            Log.d("SUCCESS","Signed in with "+currentUser.toString());
                        } else {

                        }
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
