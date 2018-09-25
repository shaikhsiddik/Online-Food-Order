package com.example.ssquare.online_food_order.Interface;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by S square on 01-07-2018.
 */

public interface RecyclerItemTouchHelperListener
{
    void onSwiped(RecyclerView.ViewHolder viewHolder,int direction,int position);
}
