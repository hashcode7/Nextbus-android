package com.example.nandayemparala.myapplication.adapter;/*
 * Created by Nanda Yemparala on 9/12/16.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UpdateAppearance;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.model.Body;

import java.util.ArrayList;
import java.util.List;

public class StopsAdapter extends ArrayAdapter<Body.Predictions> {

    List<Body.Predictions> predictions;

    public StopsAdapter(Context context, int resource, List<Body.Predictions> objects) {
        super(context, resource, objects);
        predictions = new ArrayList<>(objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stop_list_row, parent, false);
        }
        Body.Predictions stop = getItem(position);
        ((TextView) convertView.findViewById(R.id.stop_title)).setText(stop.stopTitle);

        String s= "23";
        SpannableString ss1=  new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(2f), 0, s.length(), 0); // set size
        ss1.setSpan( new android.text.style.StyleSpan(Typeface.BOLD), 0, s.length(), 0);
        SpannableString ss2=  new SpannableString(" min");
        ((TextView) convertView.findViewById(R.id.text1)).setText(TextUtils.concat(ss1, ss2));
        TextView timeText =((TextView) convertView.findViewById(R.id.text1));
        timeText.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-LightItalic.ttf"));
        if(stop.direction != null && stop.direction.predictions != null){

            for(int i=0;i<stop.direction.predictions.size(); i++){
                Body.Prediction prediction = stop.direction.predictions.get(i);
                int predTime = (prediction.getMinutes() > 0 ? prediction.getMinutes():prediction.getSeconds());
                String secondaryTxt = (prediction.getMinutes() > 0 ? " min ":" sec ");
                final SpannableString timeSpan = new SpannableString("" + predTime);
                if(i == 0){
                    timeSpan.setSpan(new RelativeSizeSpan(2f), 0, timeSpan.length(), 0); // set size
                    timeSpan.setSpan( new android.text.style.StyleSpan(Typeface.BOLD), 0, timeSpan.length(), 0);
                    if(predTime < 10){
                        timeSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, timeSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    timeText.setText(TextUtils.concat(timeSpan, secondaryTxt));
                }else if(i == 1){
                    timeSpan.setSpan(new RelativeSizeSpan(1.5f), 0, timeSpan.length(), 0); // set size
                    timeSpan.setSpan( new android.text.style.StyleSpan(Typeface.BOLD), 0, timeSpan.length(), 0);
                    timeText.setText(TextUtils.concat(timeText.getText(), timeSpan, secondaryTxt));
                }else if(i == 2){
                    timeSpan.setSpan(new RelativeSizeSpan(1.5f), 0, timeSpan.length(), 0); // set size
                    timeSpan.setSpan( new android.text.style.StyleSpan(Typeface.BOLD), 0, timeSpan.length(), 0);
                    timeText.setText(TextUtils.concat(timeText.getText(), timeSpan, secondaryTxt));
                }
            }
        }
        return convertView;
    }

    private class AnimatedColorSpan extends CharacterStyle implements UpdateAppearance {
        private final int[] colors;
        private Shader shader = null;
        private Matrix matrix = new Matrix();
        private float translateXPercentage = 0;

        public AnimatedColorSpan(Context context) {
            colors = context.getResources().getIntArray(R.array.rainbow);
        }

        public void setTranslateXPercentage(float percentage) {
            translateXPercentage = percentage;
        }

        public float getTranslateXPercentage() {
            return translateXPercentage;
        }

        @Override
        public void updateDrawState(TextPaint paint) {
            paint.setStyle(Paint.Style.FILL);
            float width = paint.getTextSize() * colors.length;
            if (shader == null) {
                shader = new LinearGradient(0, 0, 0, width, colors, null,
                        Shader.TileMode.MIRROR);
            }
            matrix.reset();
            matrix.setRotate(90);
            matrix.postTranslate(width * translateXPercentage, 0);
            shader.setLocalMatrix(matrix);
            paint.setShader(shader);
        }
    }
}
