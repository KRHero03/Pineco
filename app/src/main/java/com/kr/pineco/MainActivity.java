package com.kr.pineco;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    Toolbar mainActivityToolbar;
    DrawerLayout mainActivityDrawerLayout;
    NavigationView mainActivityNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        currentUser= mAuth.getCurrentUser();

        mainActivityToolbar=findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(mainActivityToolbar);
        getSupportActionBar().setTitle("Pineco");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.default_menu_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainActivityDrawerLayout=findViewById(R.id.mainActivityDrawerLayout);

        mainActivityNavigationView = findViewById(R.id.mainActivityNavigationView);
        mainActivityNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        mainActivityDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.mainActivitySignOut:
                                mAuth.signOut();
                                currentUser.delete();
                                sendToStart();
                                return true;
                        }

                        return true;
                    }
                });

        if(currentUser==null || !currentUser.isEmailVerified()){
           sendToStart();
        }else{
            DatabaseReference accessCodeRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("AccessCode");

            accessCodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String accessCode=dataSnapshot.getValue().toString();
                    switch(accessCode){
                        case "0":
                            Intent introActivityIntent=new Intent(MainActivity.this,introActivity.class);
                            startActivity(introActivityIntent);
                            break;
                        case "1":
                            break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

    }

    private void sendToStart(){
        Intent startActivityIntent=new Intent(MainActivity.this,startActivity.class);
        startActivity(startActivityIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mainActivityDrawerLayout.openDrawer(GravityCompat.START);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


}
