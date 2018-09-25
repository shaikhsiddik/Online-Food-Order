package com.example.ssquare.online_food_order.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ssquare.online_food_order.Interface.ItemClickListener;
import com.example.ssquare.online_food_order.R;

public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView food_name,food_price;
    ImageView food_image;
    private ImageView fav_image;
    private ImageView btn_share;
    ImageView quik_cart;
    private ItemClickListener itemClickListener;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;




    FavoritesViewHolder(View itemView) {
        super(itemView);

        food_name=(TextView)itemView.findViewById(R.id.food_name);
        food_price=(TextView)itemView.findViewById(R.id.food_price);
        food_image=(ImageView)itemView.findViewById(R.id.food_image);
        fav_image=(ImageView)itemView.findViewById(R.id.fav);
        btn_share=(ImageView)itemView.findViewById(R.id.btnShare);
        quik_cart=(ImageView)itemView.findViewById(R.id.btn_quik_cart);
        view_background=(RelativeLayout)itemView.findViewById(R.id.view_background);
        view_foreground=(LinearLayout) itemView.findViewById(R.id.view_foreground);
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
