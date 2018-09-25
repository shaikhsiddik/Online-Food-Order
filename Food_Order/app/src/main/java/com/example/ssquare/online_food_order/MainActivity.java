package com.example.ssquare.online_food_order;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Model.User;
import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {

   FButton btnSignIn,btnSignUp;
   TextView txtSlogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        printKeyHash();

        btnSignIn=(FButton) findViewById(R.id.btnSignin);
        btnSignUp=(FButton) findViewById(R.id.btnsignup);
        txtSlogan=(TextView)findViewById(R.id.txtSlogan);
        Paper.init(this);

        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/jasmineupcbold.ttf");
        txtSlogan.setTypeface(face);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin =new Intent(MainActivity.this,SignIn.class);
                startActivity(signin);

            }
        });
        String user=Paper.book().read(Common.USER_KEY);
        String pass=Paper.book().read(Common.PWD_KEY);
        if (user!=null && pass!=null)
        {
            if (!user.isEmpty() && !pass.isEmpty())
            {
                login(user,pass);
            }
        }




        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singup=new Intent(MainActivity.this,SignUp.class);
                startActivity(singup);
            }
        });


    }

    private void printKeyHash()
    {
        try
        {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info=getPackageManager().getPackageInfo("com.example.ssquare.online_food_order",PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures)
            {
                MessageDigest messageDigest=MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("HashKey",Base64.encodeToString(messageDigest.digest(),Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    private void login(final String phone, final String pass)
    {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("user");
        if (Common.isConnectedToInternet(getBaseContext()))
        {

            //Close
            final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Please waiting");
            mProgressDialog.show();
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phone).exists()) {
                        mProgressDialog.dismiss();

                        User user = dataSnapshot.child(phone).getValue(User.class);
                        assert user != null;
                        user.setPhone(phone);
                        if (user.getPassword().equals(pass)) {
                            //Toast.makeText(SignIn.this, "Sign In Successfully..!", Toast.LENGTH_SHORT).show();
                            Intent homeintent = new Intent(MainActivity.this, Home.class);
                            com.example.ssquare.online_food_order.Common.Common.currentUser = user;

                            startActivity(homeintent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Sign In is failed!!! plz try again", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User Not Vailde", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else
        {
            Toast.makeText(MainActivity.this,"Please Check Your Internet Connection!!!",Toast.LENGTH_SHORT).show();
        }
    }
}
