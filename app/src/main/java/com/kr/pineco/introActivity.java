package com.kr.pineco;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
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
    Toolbar introToolbar;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        introToolbar = findViewById(R.id.introToolbar);
        setSupportActionBar(introToolbar);
        getSupportActionBar().setTitle("Fill your Details");

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

    }
    private void verifyPhone(){

        username=introUsername.getText().toString();
        address=introAddress.getText().toString();
        phone=introPhone.getText().toString();
        Log.d("introActivityError","IntroActivity Button Clicked!");
        if(username.isEmpty()||address.isEmpty()||phone.isEmpty()||username.length()==0||address.length()<=6||phone.length()!=10){
            Toast.makeText(introActivity.this,"Please enter valid Phone Number!",Toast.LENGTH_SHORT);
        }else{
            phone="+"+introCCP.getSelectedCountryCode()+phone;
            Log.d("introActivityError",phone);
            sendPhoneVerification();
        }
    }

    private void sendPhoneVerification() {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("introActivityError",e.getMessage());
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d("introActivityError","Verification Automatically Done"+phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                verificationCode=token.toString();
                Log.d("introActivityError",verificationCode);
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

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
            Toast.makeText(introActivity.this,"Your location received!",Toast.LENGTH_SHORT).show();
        }
    }
}


