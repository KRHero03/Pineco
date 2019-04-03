package com.kr.pineco;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class cartAdapter extends RecyclerView.Adapter {
    private final String contextName;
    private final Context context;
    private final ArrayList<Fruit> fruitArray;


    public  cartAdapter(Context context,ArrayList<Fruit> fruitArray,String contextName){
        this.context=context;
        this.fruitArray=fruitArray;
        this.contextName=contextName;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cart_layout, viewGroup, false);
        cartAdapter.ViewHolder vh1 = new cartAdapter.ViewHolder(v);
        return vh1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircularImageView fruitImage;
        TextView fruitName,fruitDescription,fruitCost,fruitValidity,fruitQuantity,fruitFinalCost;
        RelativeLayout fruitRelativeLayout;

        public ViewHolder(View v) {
            super(v);

            fruitImage=v.findViewById(R.id.cartFruitImage);
            fruitName=v.findViewById(R.id.cartFruitName);
            fruitDescription=v.findViewById(R.id.cartFruitDescription);
            fruitCost=v.findViewById(R.id.cartFruitCost);
            fruitValidity=v.findViewById(R.id.cartFruitValidity);
            fruitQuantity=v.findViewById(R.id.cartFruitQuantity);
            fruitFinalCost=v.findViewById(R.id.cartFruitFinalCost);
            fruitRelativeLayout=v.findViewById(R.id.cartFruitRelativeLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        cartAdapter.ViewHolder viewholder = (cartAdapter.ViewHolder) viewHolder;

        TextView fruitName = viewholder.fruitName;
        TextView fruitDescription = viewholder.fruitDescription;
        TextView fruitCost = viewholder.fruitCost;
        TextView fruitValidity = viewholder.fruitValidity;
        TextView fruitQuantity= viewholder.fruitQuantity;
        TextView fruitFinalCost=viewholder.fruitFinalCost;
        final CircularImageView fruitImage = viewholder.fruitImage;
        RelativeLayout fruitRelativeLayout=viewholder.fruitRelativeLayout;



        fruitName.setText(fruitArray.get(i).getName());
        fruitDescription.setText(fruitArray.get(i).getDescription().substring(0,40)+"...");
        fruitCost.setText("₹ "+ fruitArray.get(i).getCost()+"/kg");
        fruitValidity.setText("Fresh for "+fruitArray.get(i).getValidity()+" Days");
        fruitQuantity.setText(fruitArray.get(i).getQuantity()+" KG");
        fruitFinalCost.setText("₹ "+(Double.parseDouble(fruitArray.get(i).getQuantity())*Double.parseDouble(fruitArray.get(i).getCost())));

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

        if(contextName.equals("cartActivity")) {
            fruitRelativeLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent fruitInfoActivityIntent = new Intent(context, fruitInfoActivity.class);
                    fruitInfoActivityIntent.putExtra("FruitUID", fruitArray.get(i).getFruitUID());
                    context.startActivity(fruitInfoActivityIntent);
                }
            });
        }

        if(i%2==0){
            fruitRelativeLayout.setBackgroundColor(context.getResources().getColor(R.color.colorBackgroundLight));
        }

    }

    @Override
    public int getItemCount() {
        return fruitArray.size();
    }
}
