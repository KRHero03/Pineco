package com.kr.pineco;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class fruitInfoActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    DatabaseReference fruitDataRef,quantityRef;

    Toolbar fruitInfoToolbar;
    String fruitUID;
    CircularImageView fruitInfoFruitImage;
    TextView fruitInfoFruitName,fruitInfoFruitDescription,fruitInfoFruitCost,fruitInfoFruitValidity,fruitInfoQuantityText,fruitInfoFinalCost;
    String fruitName,fruitDescription,fruitCost,fruitValidity,fruitImage;
    ImageButton fruitInfoAdd,fruitInfoSubtract;
    Button fruitInfoAddtoCart,fruitInfoRemoveFromCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_info);

        fruitUID=getIntent().getStringExtra("FruitUID");

        Log.d("fruitInfoActivityLog",fruitUID);


        fruitInfoToolbar=findViewById(R.id.fruitInfoToolbar);
        setSupportActionBar(fruitInfoToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fruitInfoFruitName=findViewById(R.id.fruitInfoFruitName);
        fruitInfoFruitCost=findViewById(R.id.fruitInfoFruitCost);
        fruitInfoFruitDescription=findViewById(R.id.fruitInfoFruitDescription);
        fruitInfoFruitValidity=findViewById(R.id.fruitInfoFruitValidity);
        fruitInfoFruitImage=findViewById(R.id.fruitInfoFruitImage);
        fruitInfoQuantityText = findViewById(R.id.fruitInfoQuantityText);
        fruitInfoAdd=findViewById(R.id.fruitInfoAdd);
        fruitInfoSubtract=findViewById(R.id.fruitInfoSubtract);
        fruitInfoAddtoCart=findViewById(R.id.fruitInfoAddToCart);
        fruitInfoRemoveFromCart=findViewById(R.id.fruitInfoRemoveFromCart);
        fruitInfoFinalCost=findViewById(R.id.fruitInfoFinalCost);


        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        fruitDataRef=FirebaseDatabase.getInstance().getReference().child("Fruits").child(fruitUID);
        fruitDataRef.keepSynced(true);
        quantityRef= FirebaseDatabase.getInstance().getReference().child("Cart").child(currentUser.getUid()).child(fruitUID);
        quantityRef.keepSynced(true);



        getFruitData();

        setButtonActions();

    }

    private void getFruitData(){



        fruitDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fruitName=dataSnapshot.child("Name").getValue().toString();
                fruitDescription=dataSnapshot.child("Description").getValue().toString();
                fruitCost=dataSnapshot.child("Cost").getValue().toString();
                fruitValidity=dataSnapshot.child("Validity").getValue().toString();
                fruitImage=dataSnapshot.child("Image").getValue().toString();

                fruitInfoFruitName.setText(fruitName);
                fruitInfoFruitCost.setText("₹"+ fruitCost+" /kg");
                fruitInfoFruitDescription.setText(fruitDescription);
                fruitInfoFruitValidity.setText("Fresh for "+fruitValidity+" days!");


                getSupportActionBar().setTitle("Pineco - " + fruitName);

                final Uri fruitImageUri = Uri.parse(fruitImage);
                Picasso.with(fruitInfoActivity.this).load(fruitImageUri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.default_fruit_image).into(fruitInfoFruitImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(fruitInfoActivity.this).load(fruitImageUri).placeholder(R.mipmap.default_fruit_image).into(fruitInfoFruitImage);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        quantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    fruitInfoQuantityText.setText(dataSnapshot.child("Quantity").getValue().toString());
                    fruitInfoFinalCost.setText("₹ "+(Double.parseDouble(dataSnapshot.child("Quantity").getValue().toString())*Double.parseDouble(fruitCost)));
                }catch(Exception e){
                    fruitInfoQuantityText.setText("0.0");
                    fruitInfoFinalCost.setText("₹ 0.0");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void setButtonActions(){
        fruitInfoAdd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                fruitInfoQuantityText.setText(""+(Double.parseDouble(fruitInfoQuantityText.getText().toString())+0.5));
                fruitInfoFinalCost.setText("₹ "+((Double.parseDouble(fruitInfoQuantityText.getText().toString()))*Double.parseDouble(fruitCost)));

            }
        });

        fruitInfoSubtract.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if((Double.parseDouble(fruitInfoQuantityText.getText().toString())-0.5)>=0) {
                    fruitInfoQuantityText.setText("" + (Double.parseDouble(fruitInfoQuantityText.getText().toString()) - 0.5));
                    fruitInfoFinalCost.setText("₹ "+((Double.parseDouble(fruitInfoQuantityText.getText().toString()))*Double.parseDouble(fruitCost)));

                }
            }
        });

        fruitInfoAddtoCart.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                String quantity=fruitInfoQuantityText.getText().toString();
                if(quantity.equals("0.0")){
                    Toast.makeText(fruitInfoActivity.this, "Please select valid Quantity!", Toast.LENGTH_LONG).show();
                }else {
                    quantityRef.child("Quantity").setValue(quantity);
                    Toast.makeText(fruitInfoActivity.this, quantity + " KG Fresh " + fruitName + " added to Cart!", Toast.LENGTH_LONG).show();
                }
            }
        });
        fruitInfoRemoveFromCart.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                quantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try{
                            String quantity=dataSnapshot.child("Quantity").getValue().toString();
                            if(!quantity.equals("0.0")){
                                quantityRef.removeValue();
                                Toast.makeText(fruitInfoActivity.this,fruitName+" removed from Cart!",Toast.LENGTH_LONG).show();
                                fruitInfoQuantityText.setText("0.0");
                                fruitInfoFinalCost.setText("₹ 0.0");
                            }
                        }catch(Exception e){
                            Toast.makeText(fruitInfoActivity.this,fruitName+" not yet added to Cart!",Toast.LENGTH_LONG).show();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
