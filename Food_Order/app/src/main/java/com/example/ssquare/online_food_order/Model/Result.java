package com.example.ssquare.online_food_order.Model;

/**
 * Created by S square on 12-06-2018.
 */

class Result
{
    public String MessageId;

    public Result(String messageId) {
        MessageId = messageId;
    }

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }
}
