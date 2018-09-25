package com.example.ssquare.online_food_order.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.ssquare.online_food_order.Interface.ItemClickListener;
import com.example.ssquare.online_food_order.R;

/**
 * Created by S square on 05-06-2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAddress;


    public OrderViewHolder(View itemView) {
        super(itemView);
        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        txtOrderAddress=(TextView)itemView.findViewById(R.id.order_address);




        itemView.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {


    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        ItemClickListener itemClickListener1 = itemClickListener;
    }



}
