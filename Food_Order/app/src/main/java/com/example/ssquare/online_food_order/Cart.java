package com.example.ssquare.online_food_order;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Database.Database;
import com.example.ssquare.online_food_order.Helper.RecyclerItemTouchHelper;
import com.example.ssquare.online_food_order.Interface.RecyclerItemTouchHelperListener;
import com.example.ssquare.online_food_order.Model.DataMessage;
import com.example.ssquare.online_food_order.Model.MyResponse;
import com.example.ssquare.online_food_order.Model.Notification;
import com.example.ssquare.online_food_order.Model.Order;
import com.example.ssquare.online_food_order.Model.Request;
import com.example.ssquare.online_food_order.Model.Token;
import com.example.ssquare.online_food_order.Model.User;
import com.example.ssquare.online_food_order.Remote.APIService;
import com.example.ssquare.online_food_order.Remote.IGoogleService;
import com.example.ssquare.online_food_order.ViewHolder.CartAdapter;
import com.example.ssquare.online_food_order.ViewHolder.CartViewHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ssquare.online_food_order.Common.Common.*;

public class Cart extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,LocationListener, RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;


    public TextView txtTotalPrice;
    FButton btnPlace;

    List<Order> carta=new ArrayList<>();
    CartAdapter adapter;



    Place shipAddress;


    String address,comment;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static final int UPDATE_INTERVAL=5000;
    private static final int FASTEST_INTERVAL=3000;
    private static final int DISPLACEMENT=10;

    private static final int LOCATION_REQUEST_CODE=9999;
    private static final int PLAY_SERVICE_REQUEST=9997;

    IGoogleService mGoogleService;
    APIService mService;

    RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },LOCATION_REQUEST_CODE);
        }
        else
        {
            if (checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }
        mService= getFCMService();
        mGoogleService= getGoogleMapAPI();

        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        recyclerView=(RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        txtTotalPrice=(TextView)findViewById(R.id.total);
        btnPlace=(FButton) findViewById(R.id.btnPlaceOrder);
        rootLayout=(RelativeLayout)findViewById(R.id.rootLayout);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

         btnPlace.setOnClickListener(new View.OnClickListener() {
             @RequiresApi(api = Build.VERSION_CODES.KITKAT)
             @Override
             public void onClick(View v) {
                 if (carta.size()>0) {

                     showAlertDialog();
                 }
                 //Toast.makeText(Cart.this,"Your Cart Is Empty",Toast.LENGTH_SHORT).show();


                 loadListFood();
             }
         });


    }

    private void createLocationRequest()
    {
        mLocationRequest=new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private synchronized void buildGoogleApiClient()
    {
            mGoogleApiClient=new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();
            mGoogleApiClient.connect();
    }

    private boolean checkPlayServices()
    {
        int resultCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode!= ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICE_REQUEST).show();
            else
            {
                Toast.makeText(Cart.this,"This Devcie is not supported",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showAlertDialog()
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One More Setp");
        alertDialog.setMessage("Enter Your Address:");

        LayoutInflater inflater=this.getLayoutInflater();
        @SuppressLint("InflateParams") View order_address_comment=inflater.inflate(R.layout.order_address_comment,null);

        //final MaterialEditText edtAddress=(MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        //Auto Place Address
       final PlaceAutocompleteFragment edtAddress=(PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //Hide the search bar
        Objects.requireNonNull(edtAddress.getView()).findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        //set the hint
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter Your Address");
        //set text size
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);
        //get address form place
        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shipAddress=place;
            }

            @Override
            public void onError(Status status) {
                Log.e("ERROR",status.getStatusMessage());
            }
        });

        //final MaterialEditText edtAddress=(MaterialEditText)order_address_comment.findViewById(R.id.edtAddress);
        final MaterialEditText edtComment=(MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

        //Radio Button
        final RadioButton rdiShipAddress=(RadioButton)order_address_comment.findViewById(R.id.rdiShipAddress);

        final RadioButton rdiShipToAddess=(RadioButton)order_address_comment.findViewById(R.id.rdiHomeShip);

        final RadioButton rdiCashOnDelivery=(RadioButton)order_address_comment.findViewById(R.id.rdiCOD);

        final RadioButton rdiFoodBalance=(RadioButton)order_address_comment.findViewById(R.id.rdiFoodBalance);

        rdiShipAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b)
            {
                if (b)
                {
                    mGoogleService.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f &sensor=false",
                            mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()))
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    try {
                                        JSONObject jsonObject=new JSONObject(response.body());
                                        JSONArray resultArray=jsonObject.getJSONArray("results");

                                        JSONObject firstObject=resultArray.getJSONObject(0);

                                        address=firstObject.getString("formatted_address");

                                        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                                .setText(address);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                    Toast.makeText(Cart.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        rdiShipToAddess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b)
                {
                   if (currentUser.getHomeAddress()!=null || !TextUtils.isEmpty(currentUser.getHomeAddress()))
                   {
                       address= currentUser.getHomeAddress();
                       ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                               .setText(address);


                   }
                   else {
                       Toast.makeText(Cart.this,"Please update your home address",Toast.LENGTH_SHORT).show();
                   }
                }
            }
        });


        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.shopping_cart);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (!rdiShipAddress.isChecked() && !rdiShipToAddess.isChecked()) {
                    if (shipAddress != null) {
                        address = Objects.requireNonNull(shipAddress.getAddress()).toString();

                        Request request = new Request(
                                currentUser.getPhone(),
                                currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",
                                comment,
                                "paymentMethod",
                                "UnPaid",
                                String.format("%s,%s", shipAddress.getLatLng().latitude, shipAddress.getLatLng().longitude),

                                carta
                        );
                        String orderNumber = String.valueOf(System.currentTimeMillis());
                        requests.child(orderNumber)
                                .setValue(request);

                        new Database(getBaseContext()).cleanCart(currentUser.getPhone());
                        sendNotificationOrder(orderNumber);
                        Toast.makeText(Cart.this, "Thank You, Order Place", Toast.LENGTH_SHORT).show();
                        finish();


                    } else {

                        Toast.makeText(Cart.this, "Please enter address or select option address", Toast.LENGTH_SHORT).show();
                       /* getFragmentManager().beginTransaction()
                                .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                                .commit();*/
                        return;
                    }
                }
                if (TextUtils.isEmpty(address)) {

                    Toast.makeText(Cart.this, "Please enter address or select option address", Toast.LENGTH_SHORT).show();
                    /*getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();*/
                    return;
                }

                //address=shipAddress.getAddress().toString();
                comment = edtComment.getText().toString();

                if (!rdiCashOnDelivery.isChecked() && !rdiFoodBalance.isChecked()) {
                    Toast.makeText(Cart.this, "Please select Payment Option", Toast.LENGTH_SHORT).show();
                   /* getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();*/
                } else if (rdiCashOnDelivery.isChecked()) {
                    Request request = new Request(
                            currentUser.getPhone(),
                            currentUser.getName(),
                            address,
                            txtTotalPrice.getText().toString(),
                            "0",
                            comment,
                            "COD",
                            "UnPaid",
                            String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude()),

                            carta
                    );
                    String orderNumber = String.valueOf(System.currentTimeMillis());
                    requests.child(orderNumber)
                            .setValue(request);

                    new Database(getBaseContext()).cleanCart(currentUser.getPhone());
                    sendNotificationOrder(orderNumber);
                    Toast.makeText(Cart.this, "Thank You, Order Place", Toast.LENGTH_SHORT).show();
                    finish();

                } else if (rdiFoodBalance.isChecked())
                {
                    double amount = 0;
                    try {
                        amount = formatCurrency(txtTotalPrice.getText().toString(), Locale.US).doubleValue();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (Common.currentUser.getBalance()>=amount)
                    {
                        Request request = new Request(
                                currentUser.getPhone(),
                                currentUser.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0",
                                comment,
                                "COD",
                                "Paid",
                                String.format("%s,%s", mLastLocation.getLatitude(), mLastLocation.getLongitude()),

                                carta
                        );
                        final String orderNumber = String.valueOf(System.currentTimeMillis());
                        requests.child(orderNumber)
                                .setValue(request);

                        new Database(getBaseContext()).cleanCart(currentUser.getPhone());
                        double balance=Common.currentUser.getBalance()-amount;
                        Map<String,Object> update_balance=new HashMap<>();
                        update_balance.put("balance",balance);

                        FirebaseDatabase.getInstance()
                                .getReference("user")
                                .child(Common.currentUser.getPhone())
                                .updateChildren(update_balance)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {

                                            FirebaseDatabase.getInstance()
                                                    .getReference("user")
                                                    .child(Common.currentUser.getPhone())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Common.currentUser=dataSnapshot.getValue(User.class);
                                                            sendNotificationOrder(orderNumber);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(Cart.this,"Your Balance Not Enough, Please Choose Other Payment",Toast.LENGTH_SHORT).show();
                    }
                }

               /* getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();*/

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();

            }
        });

        alertDialog.show();
    }



    private void sendNotificationOrder(final String orderNumber)
    {
        DatabaseReference token=FirebaseDatabase.getInstance().getReference("Tokens");
        Query data=token.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Token serverToken = null;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    serverToken = postSnapshot.getValue(Token.class);

                  /* Notification notification=new Notification("Company","You Have New Order"+orderNumber);

                   assert servertoken1 != null;
                   Sender content=new Sender(servertoken1.getToken(),notification);
                   //Sender sender=new Sender(servertoken1.getToken(),notification);*/
                    Map<String, String> contentSend = new HashMap<>();
                    contentSend.put("title", "Food Order");
                    contentSend.put("Message", "Your Have New Order" + orderNumber);
                    DataMessage dataMessage = new DataMessage();
                    assert serverToken != null;
                    dataMessage.setTo(serverToken.getToken());
                    dataMessage.setData(contentSend);
                    String test = new Gson().toJson(dataMessage);
                    Log.d("Content", test);

                    mService.sendNotification(dataMessage)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                    //when get request
                                    if (response.code() == 200) {

                                            Toast.makeText(Cart.this, "Thank You, Order Place", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Failed!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                @Override
                                public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                                    Log.e("Error", t.getMessage());
                                }
                            });
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void loadListFood()
    {
        carta=new Database(this).getCarts(currentUser.getPhone());
        adapter=new CartAdapter(carta,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        //calculate total price
        int total=0;
        for(Order order:carta)
            //total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale=new Locale("en","US");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position)
    {
        carta.remove(position);

        new Database(this).cleanCart(currentUser.getPhone());

        for (Order item:carta)
        {
            new Database(this).addToCart(item);
        }
        loadListFood();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    private void displayLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation!=null)
        {
            Log.d("LOCATION","YOUR LOCATION"+mLastLocation.getLatitude()+","+mLastLocation.getLongitude());
        }
        else
        {
            Log.d("LOCATION","Cound not get Your Location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation=location;
        displayLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case LOCATION_REQUEST_CODE:
            {
                if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if (checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                    }
                }
            }
            break;
        }

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder)
        {
            String name=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem=((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex=viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
           new Database(getBaseContext()).removeFromCart(deleteItem.getProductID(), currentUser.getPhone());

           loadListFood();
          /*  int total=0;
            List<Order> orders=new Database(getBaseContext()).getCarts(currentUser.getPhone());
            for(Order item:orders)
                //total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
                total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
            Locale locale=new Locale("en","US");
            NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
            txtTotalPrice.setText(fmt.format(total));
*/

            Snackbar snackbar=Snackbar.make(rootLayout,name+"remove form cart",Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    loadListFood();
/*
                    int total=0;
                    List<Order> orders=new Database(getBaseContext()).getCarts(currentUser.getPhone());
                    for(Order item:orders)
                        //total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
                        total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                    Locale locale=new Locale("en","US");
                    NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(fmt.format(total));*/


                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
