package com.kr.pineco;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;

public class fruitAdapter extends RecyclerView.Adapter {
        private final Context context;
        private final ArrayList<Fruit> fruitArray;


        public  fruitAdapter(Context context,ArrayList<Fruit> fruitArray){
            this.context=context;
            this.fruitArray=fruitArray;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            CircularImageView fruitImage;
            TextView fruitName,fruitDescription,fruitCost,fruitValidity,fruitQuantityText;
            RelativeLayout fruitRelativeLayout,fruitOrderRelativeLayout;
            ImageButton fruitAdd,fruitSubtract,fruitDropDown;
            Button fruitAddtoCart,fruitRemoveFromCart;
            public ViewHolder(View v) {
                super(v);

                fruitImage=v.findViewById(R.id.fruitImage);
                fruitName=v.findViewById(R.id.fruitName);
                fruitDescription=v.findViewById(R.id.fruitDescription);
                fruitCost=v.findViewById(R.id.fruitCost);
                fruitValidity=v.findViewById(R.id.fruitValidity);
                fruitRelativeLayout=v.findViewById(R.id.fruitRelativeLayout);

                fruitOrderRelativeLayout=v.findViewById(R.id.fruitOrderRelativeLayout);
                fruitAdd=v.findViewById(R.id.fruitInfoAdd);
                fruitSubtract=v.findViewById(R.id.fruitSubtract);
                fruitAddtoCart=v.findViewById(R.id.fruitAddToCart);
                fruitDropDown=v.findViewById(R.id.fruitDropDown);
                fruitRemoveFromCart=v.findViewById(R.id.fruitRemoveFromCart);
                fruitQuantityText=v.findViewById(R.id.fruitInfoQuantityText);
            }
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v;
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.fruit_layout, viewGroup, false);
            ViewHolder vh1 = new ViewHolder(v);
            return vh1;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

            ViewHolder viewholder = (ViewHolder) viewHolder;

            TextView fruitName = viewholder.fruitName;
            TextView fruitDescription = viewholder.fruitDescription;
            TextView fruitCost = viewholder.fruitCost;
            TextView fruitValidity = viewholder.fruitValidity;
            final CircularImageView fruitImage = viewholder.fruitImage;
            RelativeLayout fruitRelativeLayout=viewholder.fruitRelativeLayout;

            final RelativeLayout fruitOrderRelativeLayout=viewholder.fruitOrderRelativeLayout;
            final TextView fruitQuantityText = viewholder.fruitQuantityText;
            ImageButton fruitAdd=viewholder.fruitAdd;
            ImageButton fruitSubtract=viewholder.fruitSubtract;
            ImageButton fruitDropDown=viewholder.fruitDropDown;
            Button fruitAddtoCart=viewholder.fruitAddtoCart;
            Button fruitRemoveFromCart=viewholder.fruitRemoveFromCart;



            fruitName.setText(fruitArray.get(i).getName());
            fruitDescription.setText(fruitArray.get(i).getDescription().substring(0,40)+"...");
            fruitCost.setText("₹ "+ fruitArray.get(i).getCost()+"/kg");
            fruitValidity.setText("Fresh for "+fruitArray.get(i).getValidity()+" Days");

            final Uri fruitImageUri = Uri.parse(fruitArray.get(i).getImage());
            Picasso.with(context).load(fruitImageUri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.default_fruit_image).into(fruitImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(fruitImageUri).placeholder(R.mipmap.default_fruit_image).into(fruitImage);
                }
            });

            if(i%2==0){
                fruitRelativeLayout.setBackgroundColor(context.getResources().getColor(R.color.colorBackgroundLight));
            }

            fruitRelativeLayout.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent fruitInfoActivityIntent =new Intent(context,fruitInfoActivity.class);
                    fruitInfoActivityIntent.putExtra("FruitUID",fruitArray.get(i).getFruitUID());
                    context.startActivity(fruitInfoActivityIntent);
                }
            });


            FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference quantityRef= FirebaseDatabase.getInstance().getReference().child("Cart").child(currentUser.getUid()).child(fruitArray.get(i).getFruitUID());
            quantityRef.keepSynced(true);

                quantityRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            fruitQuantityText.setText(dataSnapshot.child("Quantity").getValue().toString());
                        }catch(Exception e){
                            fruitQuantityText.setText("0.0");
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            fruitAdd.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    fruitQuantityText.setText(""+(Double.parseDouble(fruitQuantityText.getText().toString())+0.5));
                }
            });

            fruitSubtract.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    if((Double.parseDouble(fruitQuantityText.getText().toString())-0.5)>=0) {
                        fruitQuantityText.setText("" + (Double.parseDouble(fruitQuantityText.getText().toString()) - 0.5));
                    }
                }
            });

            fruitDropDown.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    if(fruitOrderRelativeLayout.getVisibility()==View.GONE){
                        fruitOrderRelativeLayout.setVisibility(View.VISIBLE);
                    }else{
                        fruitOrderRelativeLayout.setVisibility(View.GONE);
                    }
                }
            });

            fruitAddtoCart.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    String quantity=fruitQuantityText.getText().toString();
                    if(quantity.equals("0.0")){
                        Toast.makeText(context, "Please select valid Quantity!", Toast.LENGTH_LONG).show();
                    }else {
                        quantityRef.child("Quantity").setValue(quantity);
                        Toast.makeText(context, quantity + " KG Fresh " + fruitArray.get(i).getName() + " added to Cart!", Toast.LENGTH_LONG).show();
                    }
                }
            });
            fruitRemoveFromCart.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    quantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try{
                                String quantity=dataSnapshot.child("Quantity").getValue().toString();
                                if(!quantity.equals("0.0")){
                                    quantityRef.removeValue();
                                    Toast.makeText(context,fruitArray.get(i).getName()+" removed from Cart!",Toast.LENGTH_LONG).show();
                                    fruitQuantityText.setText("0.0");
                                }
                            }catch(Exception e){
                                Toast.makeText(context,fruitArray.get(i).getName()+" not yet added to Cart!",Toast.LENGTH_LONG).show();
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
        public int getItemCount() {
                return fruitArray.size();
        }
    }


