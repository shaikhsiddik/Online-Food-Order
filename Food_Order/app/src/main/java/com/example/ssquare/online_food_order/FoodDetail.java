package com.example.ssquare.online_food_order;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Database.Database;
import com.example.ssquare.online_food_order.Model.Food;
import com.example.ssquare.online_food_order.Model.Order;
import com.example.ssquare.online_food_order.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_name,food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CounterFab btnCart;
    FloatingActionButton btnRating;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference ratingTb1;

    Food currentFood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        database=FirebaseDatabase.getInstance();
        foods=database.getReference("Foods");
        ratingTb1=database.getReference("Rating");

        food_description=(TextView)findViewById(R.id.food_description);
        food_name=(TextView)findViewById(R.id.food_name);
        food_price=(TextView)findViewById(R.id.food_price);
        food_image=(ImageView)findViewById(R.id.img_food);
        numberButton=(ElegantNumberButton) findViewById(R.id.number_button);
        btnCart=(CounterFab) findViewById(R.id.btnCart);
        btnRating=(FloatingActionButton) findViewById(R.id.btnrating);
        ratingBar=(RatingBar)findViewById(R.id.ratingBar);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new Database(getBaseContext()).addToCart(new Order(foodId,
                            Common.currentUser.getPhone(),
                            currentFood.getName(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getDiscount(),
                            currentFood.getImage()
                            ));
                Toast.makeText(FoodDetail.this,"Add To Cart",Toast.LENGTH_SHORT).show();
            }
        });

        btnCart.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance( R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent()!=null)
            foodId=getIntent().getStringExtra("FoodId");
        if(!foodId.isEmpty())
        {
            if (Common.isConnectedToInternet(getBaseContext())) {
                getDetailFood(foodId);
                getRatingFood(foodId);
            }
            else {
                Toast.makeText(FoodDetail.this,"Please Check Your Internet Connection!!!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getRatingFood(String foodId)
    {
        Query foodRating=ratingTb1.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0,sum=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                    for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                    {
                        Rating item=postSnapshot.getValue(Rating.class);
                        assert item != null;
                        sum+=Integer.parseInt(item.getRateValue());
                        count++;
                    }
                    if (count!=0)
                    {
                        float average=sum/count;
                        ratingBar.setRating(average);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog()
    {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate This Food")
                .setDescription("Please select some star and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here!!!")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimary)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodId)
    {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood=dataSnapshot.getValue(Food.class);
                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);
                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPositiveButtonClicked(int valueOf, String comments)
    {
        final Rating rating=new Rating(Common.currentUser.getPhone(),foodId,String.valueOf(valueOf),comments);
        ratingTb1.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                    ratingTb1.child(Common.currentUser.getPhone()).removeValue();
                    ratingTb1.child(Common.currentUser.getPhone()).setValue(rating);
                }
                else
                {
                    ratingTb1.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetail.this,"Thank Your For Submitting Your Valueable Feedback",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
