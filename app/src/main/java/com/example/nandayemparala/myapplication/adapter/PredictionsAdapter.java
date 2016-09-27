package com.example.nandayemparala.myapplication.adapter;/*
 * Created by Nanda Yemparala on 9/12/16.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.model.Body;
import com.example.nandayemparala.myapplication.viewholder.PredictionViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PredictionsAdapter extends RecyclerView.Adapter<PredictionViewHolder> {

    private List<Body.Predictions> predictions = new ArrayList<>();

    public PredictionsAdapter(List<Body.Predictions> predictions) {
        this.predictions = new ArrayList<>(predictions);
    }

    @Override
    public PredictionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new PredictionViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.stop_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(PredictionViewHolder holder, int position) {

        Body.Predictions predictions = this.predictions.get(position);
        holder.setStopTitle(predictions.stopTitle);
        holder.setTimes(predictions.direction);
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }
}


//private class AnimatedColorSpan extends CharacterStyle implements UpdateAppearance {
//    private final int[] colors;
//    private Shader shader = null;
//    private Matrix matrix = new Matrix();
//    private float translateXPercentage = 0;
//
//    public AnimatedColorSpan(Context context) {
//        colors = context.getResources().getIntArray(R.array.rainbow);
//    }
//
//    public void setTranslateXPercentage(float percentage) {
//        translateXPercentage = percentage;
//    }
//
//    public float getTranslateXPercentage() {
//        return translateXPercentage;
//    }
//
//    @Override
//    public void updateDrawState(TextPaint paint) {
//        paint.setStyle(Paint.Style.FILL);
//        float width = paint.getTextSize() * colors.length;
//        if (shader == null) {
//            shader = new LinearGradient(0, 0, 0, width, colors, null,
//                    Shader.TileMode.MIRROR);
//        }
//        matrix.reset();
//        matrix.setRotate(90);
//        matrix.postTranslate(width * translateXPercentage, 0);
//        shader.setLocalMatrix(matrix);
//        paint.setShader(shader);
//    }
//}
