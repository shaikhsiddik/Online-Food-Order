package com.example.ssquare.online_food_order;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Database.Database;
import com.example.ssquare.online_food_order.Interface.ItemClickListener;
import com.example.ssquare.online_food_order.Model.Category;
import com.example.ssquare.online_food_order.Model.Token;
import com.example.ssquare.online_food_order.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference category;
    TextView textFullName;
    RecyclerView recycler_menu;
    CounterFab fab;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swap_layout);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    loadMenu();
                else {
                    Toast.makeText(getBaseContext(),"Please Check Your Internet Connection!!!",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    loadMenu();
                else {
                    Toast.makeText(getBaseContext(),"Please Check Your Internet Connection!!!",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        firebaseDatabase=FirebaseDatabase.getInstance();
        category=firebaseDatabase.getReference("category");

        FirebaseRecyclerOptions<Category> options=new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category,Category.class)
                .build();
        adapter=new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {
                viewHolder.textMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick)
                    {
                        //Get category id and send to next activity
                        Intent foodList=new Intent(Home.this,FoodList.class);
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });

            }

            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item,parent,false);
                return new MenuViewHolder(itemView);
            }
        };

        Paper.init(this);


        fab = (CounterFab) findViewById(R.id.fab);
        fab.setCount(10); // Set the count value to show on badge
        fab.increase(); // Increase the current count value by 1
        fab.decrease(); // Decrease the current count value by 1
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Add Cart Code
                Intent cartIntent=new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);
        textFullName=(TextView)headerView.findViewById(R.id.textfullName);
        textFullName.setText(Common.currentUser.getName());

        recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setLayoutManager(new GridLayoutManager(this,2));
        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),
                R.anim.layout_fail_down);
        recycler_menu.setLayoutAnimation(controller);


        //Connection Code For Internet

        updateToken(FirebaseInstanceId.getInstance().getToken());



    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        if (adapter!=null)
        {
            adapter.startListening();
        }
    }

    private void updateToken(String token)
    {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu()
    {



       adapter.startListening();
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        //animation
        recycler_menu.getAdapter().notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.refresh)
            loadMenu();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

        } else if (id == R.id.nav_cart) {
            Intent cartIntent=new Intent(Home.this,Cart.class);
            startActivity(cartIntent);
        }
        else if (id==R.id.nav_favorites)
        {
            Intent favoritesIntent=new Intent(Home.this,FavoritesActivity.class);
            startActivity(favoritesIntent);
        }
        else if (id == R.id.nav_order) {
            Intent intentOrder=new Intent(Home.this,OrderStatus.class);
            startActivity(intentOrder);

        } else if (id == R.id.nav_log_out) {


            Paper.book().destroy();
            Intent signIn=new Intent(Home.this,SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }
        else if (id==R.id.nav_change_Password)
        {
            showChangePassword();
        }
        else if (id==R.id.nav_home_address)
        {
            showHomeAddressDialog();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHomeAddressDialog()
    {
        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(Home.this);
        alertBuilder.setTitle("CHANGE HOME ADDRESS");
        alertBuilder.setMessage("Please fill the all information");

        LayoutInflater layoutInflater=LayoutInflater.from(this);
        @SuppressLint("InflateParams") View layout_home=layoutInflater.inflate(R.layout.home_address_layout,null);

        final MaterialEditText edthomeAddress=(MaterialEditText)layout_home.findViewById(R.id.edtHomeAddress);
        alertBuilder.setView(layout_home);

        alertBuilder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Common.currentUser.setHomeAddress(edthomeAddress.getText().toString());
                FirebaseDatabase.getInstance().getReference("User")
                        .child(Common.currentUser.getPhone())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Home.this,"Update Address Successful",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        alertBuilder.show();

    }

    private void showChangePassword()
    {
        AlertDialog.Builder alertBuilder=new AlertDialog.Builder(Home.this);
        alertBuilder.setTitle("CHANGE PASSWORD");
        alertBuilder.setMessage("Please fill the all information");

        LayoutInflater layoutInflater=LayoutInflater.from(this);
        @SuppressLint("InflateParams") View layout_pwd=layoutInflater.inflate(R.layout.change_password_layout,null);

        final MaterialEditText edtPassword=(MaterialEditText)layout_pwd.findViewById(R.id.edtpassword);
        final MaterialEditText edtNewPassword=(MaterialEditText)layout_pwd.findViewById(R.id.edtNewpassword);
        MaterialEditText edtRepeatpassword=(MaterialEditText)layout_pwd.findViewById(R.id.edtRepeatpassword);

        alertBuilder.setView(layout_pwd);

        alertBuilder.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final android.app.AlertDialog watingDialog=new SpotsDialog(Home.this);
                watingDialog.show();

                if (edtPassword.getText().toString().equals(Common.currentUser.getPassword()))
                {
                    if (edtNewPassword.getText().toString().equals(Common.currentUser.getPassword()))
                    {
                        Map<String,Object> passwordUpdate=new HashMap<>();
                        passwordUpdate.put("password",edtNewPassword.getText().toString());

                        DatabaseReference user=FirebaseDatabase.getInstance().getReference("user");
                        user.child(Common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        watingDialog.dismiss();
                                        Toast.makeText(Home.this,"Old Password Update",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Home.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else
                {
                    watingDialog.dismiss();
                    Toast.makeText(Home.this,"Wrong old Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
            }
        });
        alertBuilder.show();
    }

}
