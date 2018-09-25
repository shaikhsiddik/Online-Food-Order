package com.example.ssquare.online_food_order.ViewHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Database.Database;
import com.example.ssquare.online_food_order.FoodDetail;
import com.example.ssquare.online_food_order.FoodList;
import com.example.ssquare.online_food_order.Interface.ItemClickListener;
import com.example.ssquare.online_food_order.Model.Favorites;
import com.example.ssquare.online_food_order.Model.Order;
import com.example.ssquare.online_food_order.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    private Context context;
    private List<Favorites> favoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context)
                .inflate(R.layout.favorites_item,parent,false);
        return new FavoritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoritesViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        viewHolder.food_name.setText(favoritesList.get(position).getFoodName());
        viewHolder.food_price.setText(String.format("$ %s", favoritesList.get(position).getFoodPrice()));
        Picasso.with(context).load(favoritesList.get(position).getFoodImage()).into(viewHolder.food_image);

        //Add To Cart Each item


        viewHolder.quik_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExists = new Database(context).CheckFoodExists(favoritesList.get(position).getFoodId(), Common.currentUser.getPhone());
                if (!isExists) {
                    new Database(context).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            favoritesList.get(position).getFoodId(),
                            favoritesList.get(position).getFoodName(),
                            "1",
                            favoritesList.get(position).getFoodPrice(),
                            favoritesList.get(position).getFoodDiscount(),
                            favoritesList.get(position).getFoodImage()
                    ));

                } else {
                    new Database(context).increaseCart(Common.currentUser.getPhone(), favoritesList.get(position).getFoodId());
                }
                Toast.makeText(context, "Add To Cart", Toast.LENGTH_SHORT).show();
            }
        });


        //Add Fav

      final Favorites local=favoritesList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Toast.makeText(context,"Click"+ favoritesList.get(position).getFoodName(),Toast.LENGTH_SHORT).show();
                Intent foodDetails=new Intent(context,FoodDetail.class);
                foodDetails.putExtra("FoodId",favoritesList.get(position).getFoodId());

                context.startActivity(foodDetails);
            }
        });

    }


    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public void removeItem(int position)
    {
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Favorites item,int position)
    {
        favoritesList.add(position,item);
        notifyItemInserted(position);
    }
    public Favorites getItem(int position)
    {
        return favoritesList.get(position);
    }
}
