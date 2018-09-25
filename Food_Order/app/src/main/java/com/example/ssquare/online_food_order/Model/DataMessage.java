package com.example.ssquare.online_food_order.Model;

import java.util.Map;

public class DataMessage
{
    public String to;
    public Map<String,String> data;

    public DataMessage() {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
