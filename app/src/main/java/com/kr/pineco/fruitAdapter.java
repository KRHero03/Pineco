package com.kr.pineco;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
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
            TextView fruitName,fruitDescription,fruitCost,fruitValidity;
            public ViewHolder(View v) {
                super(v);

                fruitImage=v.findViewById(R.id.fruitImage);
                fruitName=v.findViewById(R.id.fruitName);
                fruitDescription=v.findViewById(R.id.fruitDescription);
                fruitCost=v.findViewById(R.id.fruitCost);
                fruitValidity=v.findViewById(R.id.fruitValidity);
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


        }

        @Override
        public int getItemCount() {
                return fruitArray.size();
        }
    }


