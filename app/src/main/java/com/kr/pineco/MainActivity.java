package com.kr.pineco;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference accessCodeRef,fruitRef;

    Toolbar mainActivityToolbar;
    DrawerLayout mainActivityDrawerLayout;
    NavigationView mainActivityNavigationView;

    String accessCode;

    RecyclerView mainActivityRecyclerView;
    fruitAdapter fruitAdapter;
    ArrayList<Fruit> fruitArray;
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
                                sendToStart();
                                return true;
                        }

                        return true;
                    }
                });


        mainActivityRecyclerView=findViewById(R.id.mainActivityRecyclerView);
        fruitArray=new ArrayList<>();
        fruitAdapter=new fruitAdapter(MainActivity.this,fruitArray);



        mainActivityRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(MainActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mainActivityRecyclerView.setLayoutManager(llm);
        mainActivityRecyclerView.setAdapter(fruitAdapter);

        if(currentUser==null || !currentUser.isEmailVerified()){
           sendToStart();
        }else{

            accessCodeRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("AccessCode");
            accessCodeRef.keepSynced(true);
            accessCodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    accessCode=dataSnapshot.getValue().toString();
                    switch(accessCode){

                        case "0":
                            Intent introActivityIntent=new Intent(MainActivity.this,introActivity.class);
                            startActivity(introActivityIntent);
                            finish();
                            break;
                        case "1":
                            getFruits();
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

    private void getFruits(){

        fruitRef=FirebaseDatabase.getInstance().getReference().child("Fruits");
        fruitRef.keepSynced(true);
        fruitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot fruitSnapshot: dataSnapshot.getChildren()) {
                    Log.d("FruitValues", fruitSnapshot.toString());
                    String fruitID, fruitName, fruitDescription, fruitCost, fruitValidity, fruitImage;

                    fruitID = fruitSnapshot.getKey();
                    fruitName = fruitSnapshot.child("Name").getValue().toString();
                    fruitDescription = fruitSnapshot.child("Description").getValue().toString();
                    fruitCost = fruitSnapshot.child("Cost").getValue().toString();
                    fruitValidity = fruitSnapshot.child("Validity").getValue().toString();
                    fruitImage = fruitSnapshot.child("Image").getValue().toString();

                    Fruit fruitInstance = new Fruit(fruitID, fruitName, fruitDescription, fruitCost, fruitImage, fruitValidity);

                    fruitArray.add(fruitInstance);

                }
                fruitAdapter.notifyDataSetChanged();


            }   public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
