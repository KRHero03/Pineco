package com.kr.pineco;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class orderAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final ArrayList<Order> orderArray;


    public  orderAdapter(Context context,ArrayList<Order> orderArray){
        this.context=context;
        this.orderArray=orderArray;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_layout, viewGroup, false);
        orderAdapter.ViewHolder vh1 = new orderAdapter.ViewHolder(v);
        return vh1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderUID, orderTotal, orderDelivery, orderDiscount, orderGrandTotal, orderEstimatedDelivery,orderPromotionCode,orderPaymentType;
        RecyclerView orderCartRecyclerView;
        Button orderCancelOrder;
        public ViewHolder(View v) {
            super(v);

            orderUID=v.findViewById(R.id.orderUID);
            orderTotal=v.findViewById(R.id.orderTotal);
            orderDelivery=v.findViewById(R.id.orderDelivery);
            orderDiscount=v.findViewById(R.id.orderDiscount);
            orderGrandTotal=v.findViewById(R.id.orderGrandTotal);
            orderEstimatedDelivery=v.findViewById(R.id.orderEstimatedDelivery);
            orderPromotionCode=v.findViewById(R.id.orderPromotionCode);
            orderPaymentType=v.findViewById(R.id.orderPaymentType);

            orderCartRecyclerView=v.findViewById(R.id.orderCartRecyclerLayout);

            orderCancelOrder=v.findViewById(R.id.orderCancelOrder);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        orderAdapter.ViewHolder viewholder = (orderAdapter.ViewHolder) viewHolder;

        TextView orderUID, orderTotal, orderDelivery, orderDiscount, orderGrandTotal, orderEstimatedDelivery,orderPromotionCode,orderPaymentType;
        RecyclerView orderCartRecyclerView;
        Button orderCancelOrder;

        cartAdapter cartAdapter=new cartAdapter(context,orderArray.get(i).getFruitArray(),"orderActivity");

        orderUID=viewholder.orderUID;
        orderTotal=viewholder.orderTotal;
        orderDelivery=viewholder.orderDelivery;
        orderDiscount=viewholder.orderDiscount;
        orderGrandTotal=viewholder.orderGrandTotal;
        orderEstimatedDelivery=viewholder.orderEstimatedDelivery;
        orderPromotionCode=viewholder.orderPromotionCode;
        orderPaymentType=viewholder.orderPaymentType;

        orderCartRecyclerView=viewholder.orderCartRecyclerView;

        orderCancelOrder=viewholder.orderCancelOrder;


        orderUID.setText(orderArray.get(i).getOrderUID());
        orderTotal.setText("₹ "+orderArray.get(i).getOrderTotal());
        orderDelivery.setText("₹ "+orderArray.get(i).getOrderDelivery());
        orderDiscount.setText(orderArray.get(i).getOrderDiscount()+" %");
        orderGrandTotal.setText("₹ "+orderArray.get(i).getOrderGrandTotal());
        orderPaymentType.setText(orderArray.get(i).getOrderPaymentType());

        if(orderArray.get(i).getOrderPromotionCode().isEmpty()){
            orderPromotionCode.setVisibility(View.INVISIBLE);
        }else {
            orderPromotionCode.setVisibility(View.VISIBLE);
            orderPromotionCode.setText("Promotional Code: "+orderArray.get(i).getOrderPromotionCode());
        }

        switch(orderArray.get(i).orderStatus){
            case "0":
                String orderTime =orderArray.get(i).getOrderTime();
                orderTime= ""+(Long.parseLong(orderTime)+ 3*24*60*60*1000);
                Long time=Long.parseLong(orderTime)-System.currentTimeMillis();
                if(time<(60*60*1000)){
                    orderEstimatedDelivery.setText("Anytime soon.");
                }else if(time>(60*60*1000) && time<(60*60*24*1000)){
                    orderEstimatedDelivery.setText(""+time/(1000*60*60)+" hours till you taste them!");
                }else{
                    orderEstimatedDelivery.setText(""+time/(1000*60*60*24)+" Day(s) till you get your fruits!");
                }
                break;

            case "1":
                orderEstimatedDelivery.setText("Delivered Successfully!");
                orderCancelOrder.setVisibility(View.GONE);
                break;
            case "2":
                orderEstimatedDelivery.setText("Delivery Cancelled!");
                orderCancelOrder.setVisibility(View.GONE);
                break;
        }

        orderCartRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        orderCartRecyclerView.setLayoutManager(llm);
        orderCartRecyclerView.setAdapter(cartAdapter);

        orderCancelOrder.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Pineco - Cancel Order");
                alert.setMessage("Are you sure you want to cancel order?");

                alert.setPositiveButton("Cancel Order", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference orderRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(currentUser.getUid()).child(orderArray.get(i).getOrderUID()).child("OrderStatus");
                        orderRef.setValue("2").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(context,"Order cancelled successfully!",Toast.LENGTH_LONG).show();
                                    notifyDataSetChanged();
                                }else{
                                    Toast.makeText(context,"Failed to cancel Order!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return orderArray.size();
    }
}
