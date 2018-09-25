package com.example.ssquare.online_food_order;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Database.Database;
import com.example.ssquare.online_food_order.Helper.RecyclerItemTouchHelper;
import com.example.ssquare.online_food_order.Interface.RecyclerItemTouchHelperListener;
import com.example.ssquare.online_food_order.Model.Favorites;
import com.example.ssquare.online_food_order.Model.Order;
import com.example.ssquare.online_food_order.ViewHolder.FavoritesAdapter;
import com.example.ssquare.online_food_order.ViewHolder.FavoritesViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.example.ssquare.online_food_order.Common.Common.currentUser;

public class FavoritesActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

   FavoritesAdapter adapter;
   RelativeLayout rootLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rootLayout=(RelativeLayout)findViewById(R.id.root_layout);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_fav);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),R.anim.layout_fail_down);
        recyclerView.setLayoutAnimation(controller);

        //swip to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        loadFavorites();
    }

    private void loadFavorites()
    {
        adapter=new FavoritesAdapter(this,new Database(this).getAllFavorites(Common.currentUser.getPhone()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavoritesViewHolder)
        {
            String name=((FavoritesAdapter)recyclerView.getAdapter()).getItem(position).getFoodName();

            final Favorites deleteItem=((FavoritesAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex=viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());

            new Database(getBaseContext()).removeFromFavorites(deleteItem.getFoodId(), Common.currentUser.getPhone());


            Snackbar snackbar=Snackbar.make(rootLayout,name+"remove form cart",Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToFavorites(deleteItem);


                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
