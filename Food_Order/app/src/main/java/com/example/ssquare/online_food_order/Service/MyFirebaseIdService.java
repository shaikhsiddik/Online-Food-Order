package com.example.ssquare.online_food_order.Service;

import com.example.ssquare.online_food_order.Common.Common;
import com.example.ssquare.online_food_order.Model.Token;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by S square on 12-06-2018.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService
{

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefresh=FirebaseInstanceId.getInstance().getToken();
        if (Common.currentUser!=null)
            updateTokenToFirebase(tokenRefresh);
    }

    private void updateTokenToFirebase(String tokenRefresh)
    {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token token=new Token(tokenRefresh,false);
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
