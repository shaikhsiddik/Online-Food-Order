package com.example.ssquare.online_food_order;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtphone,edtname,edtpass,edtSecureCode;
    FButton btnSignup;
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtphone=(MaterialEditText)findViewById(R.id.edtphone);
        edtname=(MaterialEditText)findViewById(R.id.edtname);
        edtpass=(MaterialEditText)findViewById(R.id.edtpass);
        edtSecureCode=(MaterialEditText)findViewById(R.id.edtsecureCode);
        btnSignup=(FButton) findViewById(R.id.btnsignup);
        layout=(RelativeLayout)findViewById(R.id.RelativeLayout);

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("user");
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mProgressDialog = new ProgressDialog(SignUp.this);
                    mProgressDialog.setMessage("Please waiting");
                    mProgressDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(edtphone.getText().toString()).exists()) {
                                mProgressDialog.dismiss();
                                Snackbar snackbar = Snackbar.make(layout,"Registeration Suucess Fully...",Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                //Toast.makeText(SignUp.this, "Phone Number Already Register!!!", Toast.LENGTH_SHORT).show();
                            } else {
                                mProgressDialog.dismiss();
                                User user = new User(edtname.getText().toString(), edtpass.getText().toString(),edtSecureCode.getText().toString());
                                table_user.child(edtphone.getText().toString()).setValue(user);
                                Toast.makeText(SignUp.this, "SignUp successfully!!", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }


                    });

                }
                else
                {
                    Toast.makeText(SignUp.this,"Please Check Your Internet Connection!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

