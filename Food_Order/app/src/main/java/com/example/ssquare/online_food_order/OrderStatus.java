package com.example.ssquare.online_food_order;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Interface.ItemClickListener;
import com.example.ssquare.online_food_order.Model.Food;
import com.example.ssquare.online_food_order.Model.Order;
import com.example.ssquare.online_food_order.Model.Request;
import com.example.ssquare.online_food_order.ViewHolder.FoodViewHolder;
import com.example.ssquare.online_food_order.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrderStatus extends AppCompatActivity {


    public RecyclerView  recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;
    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (getIntent()==null)
            loadOrders(Common.currentUser.getPhone());
        else
            if (getIntent().getStringExtra("userPhone")==null)
            {
                loadOrders(Common.currentUser.getPhone());
            }
            else
            {
                loadOrders(getIntent().getStringExtra("userPhone"));
            }


    }

    private void loadOrders(String phone)
    {
        Query getOrderByUser=requests.orderByChild("phone").equalTo(phone);
        FirebaseRecyclerOptions<Request> OrderOption=new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(getOrderByUser,Request.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(OrderOption) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull Request model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Common.currentKey=adapter.getRef(position).getKey();
                        startActivity(new Intent(OrderStatus.this,Tracking.class));
                    }
                });
            }

            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);

                return new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
