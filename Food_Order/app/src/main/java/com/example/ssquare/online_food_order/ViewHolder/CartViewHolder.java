package com.example.ssquare.online_food_order.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Interface.ItemClickListener;
import com.example.ssquare.online_food_order.R;

/**
 * Created by S square on 01-07-2018.
 */

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

    public TextView txt_cart_name,txt_price;
    public ElegantNumberButton btn_quantity;

    public ImageView cart_image;
    private ItemClickListener itemClickListener;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        txt_cart_name=(TextView)itemView.findViewById(R.id.cart_item_name);
        txt_price=(TextView)itemView.findViewById(R.id.cart_item_price);
        btn_quantity=(ElegantNumberButton)itemView.findViewById(R.id.btn_quantity);
        cart_image=(ImageView)itemView.findViewById(R.id.cart_image);
        itemView.setOnCreateContextMenuListener(this);

        view_background=(RelativeLayout)itemView.findViewById(R.id.view_background);
        view_foreground=(LinearLayout) itemView.findViewById(R.id.view_foreground);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.setHeaderTitle("Select Action");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}
