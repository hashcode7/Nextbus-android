package com.example.nandayemparala.myapplication.viewholder;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.model.Body;

import java.util.List;

/**
 * Created by Nanda Yemparala on 9/24/16.
 */

public class PredictionViewHolder extends RecyclerView.ViewHolder {

    TextView stopTitle;
    TextView times;

    public PredictionViewHolder(View itemView) {
        super(itemView);
        stopTitle = (TextView) itemView.findViewById(R.id.stop_title);
        times = (TextView) itemView.findViewById(R.id.times);
        times.setTypeface(Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Roboto-LightItalic.ttf"));
    }

    public void setStopTitle(String stopTitle) {
        this.stopTitle.setText(stopTitle);
    }

    public void setTimes(Body.Direction direction) {
        if(direction == null){
            this.times.setText("No predictions");
            return;
        }
        List<Body.Prediction> predictionList = direction.getPredictions();
        for (int i = 0; i < predictionList.size(); i++) {
            Body.Prediction prediction = predictionList.get(i);
            int predTime = (prediction.getMinutes() > 0 ? prediction.getMinutes() : prediction.getSeconds());
            String secondaryTxt = (prediction.getMinutes() > 0 ? " min " : " sec ");
            final SpannableString timeSpan = new SpannableString("" + predTime);
            if (i == 0) {
                timeSpan.setSpan(new RelativeSizeSpan(2f), 0, timeSpan.length(), 0); // set size
                timeSpan.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, timeSpan.length(), 0);
                if (predTime < 10) {
                    timeSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, timeSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                times.setText(TextUtils.concat(timeSpan, secondaryTxt));
            } else if (i == 1) {
                timeSpan.setSpan(new RelativeSizeSpan(1.5f), 0, timeSpan.length(), 0); // set size
                timeSpan.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, timeSpan.length(), 0);
                times.setText(TextUtils.concat(times.getText(), timeSpan, secondaryTxt));
            } else if (i == 2) {
                timeSpan.setSpan(new RelativeSizeSpan(1.5f), 0, timeSpan.length(), 0); // set size
                timeSpan.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, timeSpan.length(), 0);
                times.setText(TextUtils.concat(times.getText(), timeSpan, secondaryTxt));
            }
        }
    }
}
