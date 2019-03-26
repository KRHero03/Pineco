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
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class introActivity extends AppCompatActivity {
    FirebaseUser currentUser;

    AppLocationService appLocationService;

    TextInputLayout introPhoneLayout;
    EditText introUsername, introAddress, introPhone;
    DatabaseReference dbRef;
    String username, address, phone;
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
        introGetLocation = findViewById(R.id.introGetLocation);
        introPhoneLayout=findViewById(R.id.introPhoneLayout);
        introSave=findViewById(R.id.introSave);

        introGetLocation.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
        getUserDetails();

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

        introSave.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                verifyPhone();
            }
        });

    }
    private void verifyPhone(){
        if(introPhoneLayout.isErrorEnabled()){
            Toast.makeText(introActivity.this,"Please enter valid Phone Number!",Toast.LENGTH_SHORT);
        }else{

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

    private void getUserDetails(){
        dbRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("Username");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username=dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(!(username==null)){
            introUsername.setText(username);
            introUsername.setEnabled(false);
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
            Toast.makeText(introActivity.this,"Your location received!",Toast.LENGTH_SHORT);
        }
    }
}


