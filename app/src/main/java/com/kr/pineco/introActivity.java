package com.kr.pineco;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class introActivity extends AppCompatActivity {
    FirebaseUser currentUser;

    AppLocationService appLocationService;

    TextInputLayout introPhoneLayout,introUsernameLayout,introAddressLayout;
    EditText introUsername, introAddress, introPhone;
    DatabaseReference dbRef;
    String username="", address="", phone="";
    String verificationCode;
    ImageButton introGetLocation;
    Button introSave;
    CountryCodePicker introCCP;
    String code="";
    Toolbar introToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        introToolbar = findViewById(R.id.introToolbar);
        setSupportActionBar(introToolbar);
        getSupportActionBar().setTitle("Fill your Details");
        if(getIntent().getStringExtra("backAccess").equals("1")){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        appLocationService = new AppLocationService(introActivity.this);

        introUsername = findViewById(R.id.introUsername);
        introPhone = findViewById(R.id.introPhone);
        introCCP = findViewById(R.id.introCCP);
        introAddress = findViewById(R.id.introAddress);


        introUsernameLayout=findViewById(R.id.introUsernameLayout);
        introAddressLayout=findViewById(R.id.introAddressLayout);
        introPhoneLayout=findViewById(R.id.introPhoneLayout);

        introGetLocation = findViewById(R.id.introGetLocation);
        introSave=findViewById(R.id.introSave);

        introGetLocation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        introPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()!=10){
                    introPhone.setTextColor(Color.RED);
                    introPhoneLayout.setErrorEnabled(true);
                    introPhoneLayout.setError("Enter valid Phone Number!");
                    introPhoneLayout.setErrorTextAppearance(R.style.TextInputError);
                }else{

                    introPhone.setTextColor(getResources().getColor(R.color.colorPrimary));
                    introPhoneLayout.setErrorEnabled(false);
                    introPhoneLayout.setError("");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        introUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==0){
                    introUsername.setTextColor(Color.RED);
                    introUsernameLayout.setErrorEnabled(true);
                    introUsernameLayout.setError("Enter valid Username!");
                    introPhoneLayout.setErrorTextAppearance(R.style.TextInputError);
                }else{

                    introUsername.setTextColor(getResources().getColor(R.color.colorPrimary));
                    introUsernameLayout.setErrorEnabled(false);
                    introUsernameLayout.setError("");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        introAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()<=6){
                    introAddress.setTextColor(Color.RED);
                    introAddressLayout.setErrorEnabled(true);
                    introAddressLayout.setError("Enter valid Address!");
                    introAddressLayout.setErrorTextAppearance(R.style.TextInputError);
                }else{

                    introAddress.setTextColor(getResources().getColor(R.color.colorPrimary));
                    introAddressLayout.setErrorEnabled(false);
                    introAddressLayout.setError("");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        introSave.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                verifyPhone();
            }
        });

        getUserData();

    }
    private void getUserData(){
        dbRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("Username");
        dbRef.keepSynced(true);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    introUsername.setText(dataSnapshot.getValue().toString());
                    introUsername.setSelection(introUsername.length());
                }catch(Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbRef.getParent().child("Address").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            introAddress.setText(dataSnapshot.getValue().toString());
                            introAddress.setSelection(introAddress.length());
                        }catch(Exception e){

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        dbRef.getParent().child("Phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    String phone = dataSnapshot.getValue().toString();
                    introPhone.setText(phone.substring(phone.length() - 10));
                    introCCP.setCountryForPhoneCode(Integer.parseInt(phone.substring(0, phone.length() - 11)));
                    introPhone.setSelection(introPhone.length());
                }catch (Exception e){
                    Log.d("introActivityError",e.getMessage()+ " "+currentUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verifyPhone(){

        username=introUsername.getText().toString();
        address=introAddress.getText().toString();
        phone=introPhone.getText().toString();
        if(username.isEmpty()||address.isEmpty()||phone.isEmpty()||username.length()==0||address.length()<=6||phone.length()!=10){
            Toast.makeText(introActivity.this,"Please enter valid Phone Number!",Toast.LENGTH_SHORT);
        }else{
            phone="+"+introCCP.getSelectedCountryCode()+phone;
            sendPhoneVerification();
        }
    }

    private void sendPhoneVerification() {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("introActivityException",e.getMessage());
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                code=phoneAuthCredential.getSmsCode();

                verifyUser();


            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                verificationCode=token.toString();
                Log.d("introActivityV",verificationCode);
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

    }

    private void verifyUser(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Pineco - Verify OTP");
        alert.setMessage("Please enter the OTP received");
        final EditText input = new EditText (this);
        input.setTextColor(getResources().getColor(R.color.colorPrimary));

        alert.setView(input);

        alert.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                verifyOTP(input.getText().toString());

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }


    private void verifyOTP(String otp){
        if(otp.equals(code)){
            dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("Phone");
            dbRef.setValue(phone);

            dbRef.getParent().child("AccessCode").setValue("1");
            dbRef.getParent().child("Address").setValue(address);
            dbRef.getParent().child("Username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(introActivity.this, "Details saved Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });

        }else{
            Toast.makeText(this,"Invalid OTP!",Toast.LENGTH_SHORT).show();
        }
    }

    private void getLocation(){
        Location location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);


        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            LocationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
        } else {
            showSettingsAlert();
        }
    }



    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                introActivity.this);
        alertDialog.setTitle("Get Location");
        alertDialog.setMessage("Pineco is unable to fetch your location!");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        introActivity.this.startActivity(intent);


                    }
                });
        alertDialog.setNegativeButton("Nevermind",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getStringExtra("backAccess").equals("1")){
            super.onBackPressed();
        }else{

        }


    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            introAddress.setText(locationAddress);
            introAddress.setSelection(introAddress.getText().toString().length()-1);
            Toast.makeText(introActivity.this,"Your location has been received!",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }
}


