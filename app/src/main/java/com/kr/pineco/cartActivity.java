package com.kr.pineco;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class cartActivity extends AppCompatActivity {
    FirebaseUser currentUser;
    DatabaseReference cartRef;

    RelativeLayout cartSecondaryRelativeLayout,cartSecondaryRelativeLayout1;
    RecyclerView cartRecyclerView;
    Button cartPlaceOrder, cartPromotion,cartRemovePromotion;
    TextView cartTotal, cartDiscount, cartDelivery, cartGrandTotal;

    ProgressDialog cartActivityProgressDialog;

    ArrayList<Fruit> fruitArray=new ArrayList<>();
    cartAdapter cartAdapter;

    double cartTotalS=0.0, cartDiscountS=0.0, cartDeliveryS=10.0, cartGrandTotalS=0.0;
    String promotionCode,tempPromoCode;
    Toolbar cartToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartToolbar=findViewById(R.id.cartToolbar);
        setSupportActionBar(cartToolbar);
        getSupportActionBar().setTitle("Pineco - Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        cartRef= FirebaseDatabase.getInstance().getReference().child("Cart").child(currentUser.getUid());
        cartRef.keepSynced(true);

        cartSecondaryRelativeLayout=findViewById(R.id.cartPrimaryRelativeLayout);
        cartSecondaryRelativeLayout1=findViewById(R.id.cartSecondaryRelativeLayout1);



        cartRecyclerView=findViewById(R.id.cartRecylerView);
        cartPlaceOrder=findViewById(R.id.cartPlaceOrder);
        cartPromotion=findViewById(R.id.cartPromotion);
        cartRemovePromotion=findViewById(R.id.cartRemovePromotion);
        cartTotal=findViewById(R.id.cartTotalText);
        cartDiscount=findViewById(R.id.cartDiscountText);
        cartDelivery=findViewById(R.id.cartDeliveryText);
        cartGrandTotal=findViewById(R.id.cartGrandTotalText);

        cartAdapter=new cartAdapter(cartActivity.this,fruitArray);

        cartRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(cartActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        cartRecyclerView.setLayoutManager(llm);
        cartRecyclerView.setAdapter(cartAdapter);

        cartActivityProgressDialog=new ProgressDialog(this);

        cartRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               cartTotalS=0.0;
               fruitArray.clear();
               if(!dataSnapshot.exists()){
                   cartSecondaryRelativeLayout1.setVisibility(View.VISIBLE);
                   cartSecondaryRelativeLayout.setVisibility(View.GONE);
                   cartPlaceOrder.setVisibility(View.GONE);
               }else {
                   for (DataSnapshot fruitData : dataSnapshot.getChildren()) {
                        final String fruitUID=fruitData.getKey();
                        final String fruitQuantity=fruitData.child("Quantity").getValue().toString();

                        DatabaseReference fruitRef=FirebaseDatabase.getInstance().getReference().child("Fruits").child(fruitUID);
                        fruitRef.keepSynced(true);
                        fruitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String fruitName, fruitDescription,fruitCost,fruitValidity,fruitImage;
                                fruitName=dataSnapshot.child("Name").getValue().toString();
                                fruitDescription=dataSnapshot.child("Description").getValue().toString();
                                fruitCost=dataSnapshot.child("Cost").getValue().toString();
                                fruitValidity=dataSnapshot.child("Validity").getValue().toString();
                                fruitImage=dataSnapshot.child("Image").getValue().toString();
                                Fruit fruitInstance=new Fruit(fruitUID,fruitName,fruitDescription,fruitCost,fruitImage,fruitValidity);
                                fruitInstance.setQuantity(fruitQuantity);

                                fruitArray.add(fruitInstance);
                                cartTotalS+=(Double.parseDouble(fruitCost)*Double.parseDouble(fruitQuantity));
                                calculateTotal(); //Efficiency Bug
                                cartAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                   }
                   cartSecondaryRelativeLayout.setVisibility(View.VISIBLE);
                   cartSecondaryRelativeLayout1.setVisibility(View.GONE);
                   cartPlaceOrder.setVisibility(View.VISIBLE);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        cartPromotion.setOnClickListener(new View.OnClickListener(){

           @Override
           public void onClick(View view) {
                addPromotion();
           }
        });

        cartRemovePromotion.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                removePromotion();
            }
        });

        cartPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent orderActivityIntent=new Intent(cartActivity.this,orderActivity.class);
                orderActivityIntent.putExtra("Total",cartTotalS);
                orderActivityIntent.putExtra("Delivery",cartDeliveryS);
                orderActivityIntent.putExtra("Discount",cartDiscountS);
                orderActivityIntent.putExtra("GrandTotal",cartGrandTotalS);
                orderActivityIntent.putExtra("PromotionCode",promotionCode);

                startActivity(orderActivityIntent);
                finish();
            }
        });



    }

    private void addPromotion(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Pineco - Promotion");
        alert.setMessage("Use a Promotional Code (Use code with Uppercase Letters)");
        final EditText input = new EditText (this);
        input.setTextColor(getResources().getColor(R.color.colorPrimary));

        alert.setView(input);

        alert.setPositiveButton("Use Promo Code", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getTempPromoCode(input.getText().toString());

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    private void getTempPromoCode(String x){
        cartActivityProgressDialog.setTitle("Pineco - Verifying Promotion");
        cartActivityProgressDialog.setMessage("Cutting costs for your fruits...");
        cartActivityProgressDialog.setCancelable(true);
        cartActivityProgressDialog.show();
        tempPromoCode=x;

        final DatabaseReference promoRef = FirebaseDatabase.getInstance().getReference().child("Promotions").child(currentUser.getUid()).child(tempPromoCode);
        promoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartActivityProgressDialog.dismiss();
                if(dataSnapshot.exists()){
                    cartDiscountS=Double.parseDouble(dataSnapshot.getValue().toString());
                    calculateTotal();
                    cartPromotion.setVisibility(View.INVISIBLE);
                    cartRemovePromotion.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(cartActivity.this,"Promotion Code Invalid!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removePromotion(){
        promotionCode="";
        cartDiscountS=0.0;
        calculateTotal();
        cartRemovePromotion.setVisibility(View.INVISIBLE);
        cartPromotion.setVisibility(View.VISIBLE);
        Toast.makeText(cartActivity.this,"Promotion Removed!",Toast.LENGTH_SHORT).show();
    }

    private void calculateTotal(){

        cartGrandTotalS=(cartTotalS+cartDeliveryS)*(1-cartDiscountS);

        cartTotal.setText("₹ "+cartTotalS);
        cartDiscount.setText(""+(cartDiscountS*100.00)+" %");
        cartDelivery.setText("₹ "+cartDeliveryS);
        cartGrandTotal.setText("₹ "+cartGrandTotalS);

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
