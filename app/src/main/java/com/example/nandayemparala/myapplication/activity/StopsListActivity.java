package com.example.nandayemparala.myapplication.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.api.AgencyListApi;
import com.example.nandayemparala.myapplication.api.PredictionsApi;
import com.example.nandayemparala.myapplication.model.Body;
import com.example.nandayemparala.myapplication.model.Route;
import com.example.nandayemparala.myapplication.model.Routes;
import com.example.nandayemparala.myapplication.model.Stop;
import com.google.android.gms.maps.model.LatLng;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Nanda Yemparala on 9/7/16.
 */

@EActivity(R.layout.stops_list_layout)
public class StopsListActivity extends BaseActivity {

    @ViewById(R.id.stops_list)
    ListView stopsList;

    @ViewById(R.id.route_title)
    TextView routeTitle;

    @AfterViews
    void loadViews(){
        getData();
    }

    @Background
    void getData(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://webservices.nextbus.com")
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        AgencyListApi restInterface = retrofit.create(AgencyListApi.class);
        PredictionsApi predictionsApi = retrofit.create(PredictionsApi.class);
        Call<Routes> routesCall = restInterface.getRoutes();
        try {
            Response<Routes> routesResponse = routesCall.execute();
            List<Route> routes = routesResponse.body().routes;
            for(Route r: routes){
                try {
                    getHelper().getRouteDao().createOrUpdate(r);
                    List<Stop> stops = new ArrayList<>(r.getStops());
                    for(int i=0;i<stops.size();i++){
                        Stop s = stops.get(i);
                        s.setRoute(r);
                        s.setStopNumber(i + 1);
                        getHelper().getStopDao().createOrUpdate(s);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            Route route = routes.get(0);
            List<String> stops = new ArrayList<>();
            for(Stop stop: route.getStops()){
                stops.add(route.getTag() + "|" + stop.getTag());
            }
            Response<Body> response = predictionsApi.getPredictions(stops).execute();
            Log.i("Response", "Response: "+response.raw().toString());
            Body body = response.body();
            List<Body.Predictions> bodyPredictions = body.getPredictions();
            for(Body.Predictions predictions1: bodyPredictions){
                if(predictions1.direction == null){
                    Log.e("Preds", "Prediction Direction is null");
                    continue;
                }
                if(predictions1.direction.predictions == null){
                    Log.e("Preds", "Predictions is null");
                    continue;
                }
                for(Body.Prediction p : predictions1.direction.predictions){
                    Log.i("Prediction", "Stop"+p.getMinutes()+ " Min:" +p.getSeconds());
                }
            }

            displayData(bodyPredictions);
            findClosestStops();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.androidannotations.annotations.UiThread
    void displayData(List<Body.Predictions> predictions){

//        if(direction == null || direction.predictions == null){
//            Log.e("Error", "Preds null");
//            return;
//        }

        StopsAdapter adapter = new StopsAdapter(this, R.layout.stop_list_row, predictions);
        stopsList.setAdapter(adapter);

    }



    private class StopsAdapter extends ArrayAdapter<Body.Predictions>{

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
            ((TextView) convertView.findViewById(R.id.text1)).setText(TextUtils.concat(ss1,ss2));
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


    private void findClosestStops(){


        try{
            Route route = getHelper().getRouteDao().queryBuilder()
                    .where()
                    .eq("tag", "kearney")
                    .queryForFirst();
            List<Stop> stops = new ArrayList<>(route.getStops());
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            double myLongitude = location.getLongitude();
            double myLatitude = location.getLatitude();

            /// get distance to all stops in this route.
            for(Stop stop: stops){
                LatLng stopLatLng = stop.getMarkerPoint();
                float[] results = new float[1];
                Location.distanceBetween(myLatitude, myLongitude, stopLatLng.latitude, stopLatLng.longitude, results);

                stop.setDistance(results[0] * 0.000621371);
            }


            // order stops by nearest first
            Collections.sort(stops, new Comparator<Stop>() {
                @Override
                public int compare(Stop t1, Stop t2) {
                    return t1.getDistance().compareTo(t2.getDistance());
                }
            });


            for(Stop stop: stops){
                Log.i("Stop", "T: "+stop.getMarkerTitle() +" Dist: "+stop.getDistance() + " seq:"+stop.getStopNumber());
            }

        }catch (SecurityException | SQLException e){
            e.printStackTrace();
        }

    }
}
