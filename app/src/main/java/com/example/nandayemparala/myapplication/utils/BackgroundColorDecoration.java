package com.example.nandayemparala.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.viewholder.PredictionViewHolder;

/**
 * Created by nandayemparala on 9/28/16.
 */

public class BackgroundColorDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    // TODO Use interface in contructor params..
    public BackgroundColorDecoration(Context context) {
        this.mContext = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int color= 0;
        final int itemPosition = parent.getChildAdapterPosition(view);
        final PredictionViewHolder viewHolder = (PredictionViewHolder) parent.getChildViewHolder(view);
        if(viewHolder.getNextPrediction() != null){

            long millis = viewHolder.getNextPrediction().getTimeInMillis() - System.currentTimeMillis();
            if(millis > 0){ // valid future prediction
                long mins = millis / 60000;
                Log.i("Time", "Mins: "+mins +" T: "+viewHolder.getStopTitle());
                if(mins > 1){
                    int baseColor = mContext.getResources().getColor(R.color.grey_400);
//                            color = getContext().getResources().getColor(android.R.color.holo_green_light);
                    if(mins < 5){
//                                if(mins == 1) {
//                                    color = ColorUtils.lighter(baseColor, 0.80f);
//                                }else
                        if(mins == 2) {
                            color = ColorUtils.lighter(baseColor, 0.60f);
                        }else if(mins == 3) {
                            color = ColorUtils.lighter(baseColor, 0.40f);
                        }else if(mins == 4) {
                            color = ColorUtils.lighter(baseColor, 0.20f);
                        }
                    }else{
                        color = baseColor;
                    }
                }else{
                    color = mContext.getResources().getColor(android.R.color.holo_red_light);
                }
            }

        }else{

            if(itemPosition % 2 == 0){
                color = mContext.getResources().getColor(R.color.grey_100);
            }else{
                color = mContext.getResources().getColor(R.color.grey_200);
            }
        }
        view.setBackgroundColor(color);
    }
}
