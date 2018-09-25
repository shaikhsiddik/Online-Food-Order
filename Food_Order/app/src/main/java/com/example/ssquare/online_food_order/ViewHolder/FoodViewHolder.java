package com.example.ssquare.online_food_order.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ssquare.online_food_order.Interface.ItemClickListener;
import com.example.ssquare.online_food_order.R;

/**
 * Created by S square on 04-06-2018.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView food_name,food_price;
    public ImageView food_image,fav_image,btn_share,btn_quik_cart;
    private ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);

        food_name=(TextView)itemView.findViewById(R.id.food_name);
        food_price=(TextView)itemView.findViewById(R.id.food_price);
        food_image=(ImageView)itemView.findViewById(R.id.food_image);
        fav_image=(ImageView)itemView.findViewById(R.id.fav);
        btn_share=(ImageView)itemView.findViewById(R.id.btnShare);
        btn_quik_cart=(ImageView)itemView.findViewById(R.id.btn_quik_cart);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
