package com.example.ssquare.online_food_order.Model;

/**
 * Created by S square on 12-06-2018.
 */

public class Token
{
    private String token;
    private boolean isServerToken;



    public Token(String token, boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public Token(String token) {
        this.token = token;
    }

    public Token(boolean isServerToken) {
        this.isServerToken = isServerToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServerToken() {
        return isServerToken;
    }

    public void setServerToken(boolean serverToken) {
        isServerToken = serverToken;
    }
}
