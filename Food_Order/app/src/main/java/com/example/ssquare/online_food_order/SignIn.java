package com.example.ssquare.online_food_order;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ssquare.online_food_order.Common.Common;


import com.example.ssquare.online_food_order.Model.User;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import com.rey.material.widget.CheckBox;

import info.hoang8f.widget.FButton;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    MaterialEditText edtphone,edtpass;
    FButton btnsigninn;
    CheckBox chkrember;
    TextView txtForgotPWD;
    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        edtphone=(MaterialEditText)findViewById(R.id.edtphonee);
        edtpass=(MaterialEditText)findViewById(R.id.edtpassd);
        btnsigninn=(FButton) findViewById(R.id.btnSignIn);
        chkrember=(CheckBox) findViewById(R.id.remberBox);

        txtForgotPWD=(TextView)findViewById(R.id.txtForgotPWD);

        Paper.init(this);

        database=FirebaseDatabase.getInstance();
        table_user=database.getReference("user");

        txtForgotPWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });

        btnsigninn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Internet Connection
                if (Common.isConnectedToInternet(getBaseContext()))
                {
                    if (chkrember.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY,edtphone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,edtpass.getText().toString());
                    }

                    //Close
                    final ProgressDialog mProgressDialog = new ProgressDialog(SignIn.this);
                    mProgressDialog.setMessage("Please waiting");
                    mProgressDialog.show();
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(edtphone.getText().toString()).exists()) {
                                mProgressDialog.dismiss();

                                User user = dataSnapshot.child(edtphone.getText().toString()).getValue(User.class);
                                assert user != null;
                                user.setPhone(edtphone.getText().toString());
                                if (user.getPassword().equals(edtpass.getText().toString())) {
                                    //Toast.makeText(SignIn.this, "Sign In Successfully..!", Toast.LENGTH_SHORT).show();
                                    Intent homeintent = new Intent(SignIn.this, Home.class);
                                    com.example.ssquare.online_food_order.Common.Common.currentUser = user;
                                    startActivity(homeintent);
                                    finish();

                                    table_user.removeEventListener(this);
                                } else {
                                    Toast.makeText(SignIn.this, "Sign In is failed!!! plz try again", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mProgressDialog.dismiss();
                                Toast.makeText(SignIn.this, "User Not Vailde", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else
                {
                    Toast.makeText(SignIn.this,"Please Check Your Internet Connection!!!",Toast.LENGTH_SHORT).show();
                    return;
                }

            }


        });
    }

    private void showForgotPwdDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter Your Secure Code!!!");

        LayoutInflater inflater=this.getLayoutInflater();
        View forgot_view=inflater.inflate(R.layout.forgot_password_layout,null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone=(MaterialEditText)forgot_view.findViewById(R.id.edtPhone);
         final MaterialEditText edtSecureCode=(MaterialEditText)forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                            if (user.getSecureCode().equals(edtSecureCode.getText().toString()))
                                Toast.makeText(SignIn.this,"Your Password!!!"+user.getPassword(),Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(SignIn.this,"Wrong Secure Code!!!"+user.getPassword(),Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
