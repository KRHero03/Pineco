package com.kr.pineco;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class orderActivity extends AppCompatActivity {
    FirebaseUser currentUser;
    DatabaseReference orderRef;

    orderAdapter orderAdapter;
    ArrayList<Order> orderArray= new ArrayList<>();

    RecyclerView orderRecyclerView;

    Toolbar orderToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        orderToolbar=findViewById(R.id.orderToolbar);
        setSupportActionBar(orderToolbar);
        getSupportActionBar().setTitle("Pineco - Your Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderAdapter=new orderAdapter(orderActivity.this,orderArray);

        orderRecyclerView=findViewById(R.id.orderRecyclerVIew);
        LinearLayoutManager llm=new LinearLayoutManager(orderActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        orderRecyclerView.setLayoutManager(llm);
        orderRecyclerView.setAdapter(orderAdapter);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        orderRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(currentUser.getUid());
        orderRef.keepSynced(true);

        orderArray.clear();
        orderRef.orderByChild("OrderStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot orderDetails: dataSnapshot.getChildren()){
                    String orderUID,orderDelivery,orderDiscount,orderTotal,orderGrandTotal,orderTime,orderPaymentType,orderStatus,orderPromotionCode;
                    final ArrayList<Fruit> fruitArray=new ArrayList<>();

                    orderUID=orderDetails.getKey();
                    orderDelivery=orderDetails.child("Delivery").getValue().toString();
                    orderTotal=orderDetails.child("Total").getValue().toString();
                    orderDiscount=orderDetails.child("Discount").getValue().toString();
                    orderGrandTotal=orderDetails.child("GrandTotal").getValue().toString();
                    orderTime=orderDetails.child("Time").getValue().toString();
                    orderPaymentType=orderDetails.child("PaymentType").getValue().toString();
                    orderStatus=orderDetails.child("OrderStatus").getValue().toString();
                    orderPromotionCode=orderDetails.child("PromotionCode").getValue().toString();

                    for(DataSnapshot fruitDetails : orderDetails.child("Cart").getChildren()){
                        final String fruitUID=fruitDetails.getKey();
                        final String fruitQuantity=fruitDetails.getValue().toString();

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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });





                    }
                    Order orderInstance=new Order(orderUID,orderDelivery,orderTotal,orderDiscount,orderGrandTotal,orderPromotionCode,orderTime,orderPaymentType,orderStatus,fruitArray);

                    orderArray.add(orderInstance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        orderAdapter.notifyDataSetChanged();

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
