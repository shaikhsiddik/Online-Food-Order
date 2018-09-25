package com.example.ssquare.online_food_order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Database.Database;
import com.example.ssquare.online_food_order.Interface.ItemClickListener;
import com.example.ssquare.online_food_order.Model.Favorites;
import com.example.ssquare.online_food_order.Model.Food;
import com.example.ssquare.online_food_order.Model.Order;
import com.example.ssquare.online_food_order.ViewHolder.FoodViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId="";

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;
    //search button
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggestList=new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    Database localDb;

    SwipeRefreshLayout swipeRefreshLayout;

    //Facebook
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    //Target
    Target target=new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto photo=new SharePhoto.Builder()
                    .setBitmap(bitmap).build();
            if (ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content=new SharePhotoContent.Builder().addPhoto(photo).build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //init facebook
        callbackManager= CallbackManager.Factory.create();
        shareDialog=new ShareDialog(this);

        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Foods");

        localDb=new Database(this);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swip_layout);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent() != null)

                    categoryId=getIntent().getStringExtra("CategoryId");
                if (!categoryId.isEmpty())
                {
                    if(Common.isConnectedToInternet(getBaseContext()))
                        loadListFood(categoryId);
                    else
                    {
                        Toast.makeText(FoodList.this,"Please Check Your Internet Connection!!!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(getIntent() != null)

                    categoryId=getIntent().getStringExtra("CategoryId");
                if (!categoryId.isEmpty())
                {
                    if(Common.isConnectedToInternet(getBaseContext()))
                        loadListFood(categoryId);
                    else
                    {
                        Toast.makeText(FoodList.this,"Please Check Your Internet Connection!!!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
                materialSearchBar.setHint("Enter Your Food");
                //materialSearchBar.setSpeechMode(false);
                loadsuggest();

                materialSearchBar.setCardViewElevation(10);
                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<String> suggest=new ArrayList<>();
                        for (String search:suggestList)
                        {
                            if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggest);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        if(!enabled)
                        {
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),R.anim.layout_fail_down);
        recyclerView.setLayoutAnimation(controller);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (adapter!=null)
        {
            adapter.startListening();
        }
    }

    //startsearch
    private void startSearch(CharSequence text)
    {
        // query for search by name
        Query serachQuery=foodList.orderByChild("name").equalTo(text.toString());
        //Create option with query

        FirebaseRecyclerOptions<Food> foodOption=new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(serachQuery,Food.class)
                .build();

        searchAdapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOption) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                final Food local=model;
                viewHolder.setItemClickListener( new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this,"Click"+local.getName(),Toast.LENGTH_SHORT).show();
                        Intent foodDetails=new Intent(FoodList.this,FoodDetail.class);
                        foodDetails.putExtra("FoodId",searchAdapter.getRef(position).getKey());

                        startActivity(foodDetails);
                    }
                });
            }

            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);

                return new FoodViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recyclerView.setAdapter(searchAdapter);

    }

    private void loadsuggest()
    {
        foodList.orderByChild("menuid").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Food item=postSnapshot.getValue(Food.class);
                            assert item != null;
                            suggestList.add(item.getName());
                        }
                        materialSearchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId)
    {
        // query for search by category
        Query serachQuery=foodList.orderByChild("menuid").equalTo(categoryId);
        //Create option with query

        FirebaseRecyclerOptions<Food> foodOption=new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(serachQuery,Food.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOption) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, @SuppressLint("RecyclerView") final int position, @NonNull final Food model) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText(String.format("$ %s", model.getPrice()));
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                //Add To Cart Each item


                viewHolder.btn_quik_cart.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    boolean isExists = new Database(getBaseContext()).CheckFoodExists(adapter.getRef(position).getKey(), Common.currentUser.getPhone());
                                                                    if (!isExists) {
                                                                        new Database(getBaseContext()).addToCart(new Order(
                                                                                Common.currentUser.getPhone(),
                                                                                adapter.getRef(position).getKey(),
                                                                                model.getName(),
                                                                                "1",
                                                                                model.getPrice(),
                                                                                model.getDiscount(),
                                                                                model.getImage()
                                                                        ));

                                                                    } else {
                                                                        new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(), adapter.getRef(position).getKey());
                                                                    }
                                                                    Toast.makeText(FoodList.this, "Add To Cart", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });


                        //Add Fav

                if (localDb.isFavorite(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                //Click to share
                viewHolder.btn_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.with(getApplicationContext()).load(model.getImage())
                                .into(target);
                    }
                });

                //Click to change the

                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Favorites favorites=new Favorites();
                        favorites.setFoodId(adapter.getRef(position).getKey());
                        favorites.setFoodName(model.getName());
                        favorites.setFoodPrice(model.getPrice());
                        favorites.setFoodDiscount(model.getDiscount());
                        favorites.setFoodMenuId(model.getMenuId());
                        favorites.setFoodDescription(model.getDescription());
                        favorites.setFoodImage(model.getImage());
                        favorites.setUserPhone(Common.currentUser.getPhone());


                        if (!localDb.isFavorite(adapter.getRef(position).getKey(),Common.currentUser.getPhone()))
                        {
                            localDb.addToFavorites(favorites);
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this,""+model.getName()+"Add to favorite",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            localDb.removeFromFavorites(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this,""+model.getName()+"remove from favorite",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this,"Click"+ model.getName(),Toast.LENGTH_SHORT).show();
                        Intent foodDetails=new Intent(FoodList.this,FoodDetail.class);
                        foodDetails.putExtra("FoodId",adapter.getRef(position).getKey());

                        startActivity(foodDetails);
                    }
                });

            }


            @Override
            public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.food_item,parent,false);

                return new FoodViewHolder(itemView);
            }
        };


        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if (searchAdapter!=null)
        {
            searchAdapter.stopListening();
        }
    }
}
