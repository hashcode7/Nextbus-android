package com.example.nandayemparala.myapplication.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.TextView;

import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.api.AgencyListApi;
import com.example.nandayemparala.myapplication.api.PredictionsApi;
import com.example.nandayemparala.myapplication.application.App;
import com.example.nandayemparala.myapplication.model.Path;
import com.example.nandayemparala.myapplication.model.Point;
import com.example.nandayemparala.myapplication.model.Body;
import com.example.nandayemparala.myapplication.model.Route;
import com.example.nandayemparala.myapplication.model.Routes;
import com.example.nandayemparala.myapplication.model.Stop;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@EActivity(R.layout.activity_status)
public class StatusActivity extends BaseActivity {

    @ViewById(R.id.route_name)
    TextView route;
    @ViewById(R.id.prediction_1)
    TextView prediction1;
    @ViewById(R.id.prediction_2)
    TextView prediction2;
    @FragmentById(R.id.map)
    MapFragment mapFragment;
    @ViewById(R.id.near_stop)
    TextView nearestStop;

    GoogleMap googleMap;
    List<Marker> markerList = new ArrayList<>();
    GoogleApiClient googleApiClient;
    Retrofit retrofit;

    @AfterViews
    protected void afterViews(){
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
            }
        });

        loadRoute();
    }

    private void loadRoute() {

        retrofit = new Retrofit.Builder()
                .baseUrl("http://webservices.nextbus.com")
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        AgencyListApi restInterface = retrofit.create(AgencyListApi.class);

        Call<Routes> routesCall = restInterface.getRoutes();
        routesCall.enqueue(new Callback<Routes>() {
            @Override
            public void onResponse(Call<Routes> call, Response<Routes> response) {
                Routes body = response.body();
//                for(Route route : body.routes){
//                    loadRouteIntoMap(route);
//                }
                getRoutePredictions(body.routes.get(0));
                for(Route r: body.routes){
                    try{
                        // Create all routes
                        getHelper().getRouteDao().createOrUpdate(r);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                loadRouteIntoMap(body.routes.get(0));
//                StringBuilder builder = new StringBuilder();
//                for(Route route : body.routes){
//                    builder.append("Route: ");
//                    builder.append(route.title);
//                    builder.append(" Paths: ");
//                    builder.append(route.paths.size());
//                    builder.append("\n");
//                }
//                textView.setText(builder.toString());
            }

            @Override
            public void onFailure(Call<Routes> call, Throwable t) {
                App.showToast(t.getLocalizedMessage());
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i("Location", "Location Changed: ");
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(location.getLatitude(), location.getLongitude()), 16));
                    }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.e("Error", connectionResult.getErrorMessage());
            }
        })
        .build();
        googleApiClient.connect();

    }

    private void getRoutePredictions(Route route){
        PredictionsApi predictionsApi = retrofit.create(PredictionsApi.class);
        List<String> stops = new ArrayList<>();
        for(Stop stop: route.getStops()){
            stops.add(route.getTag() + "|" + stop.getTag());
        }
        Log.i("Route", "Stops: "+ stops.toString());
        Call<Body> predCall = predictionsApi.getPredictions(stops);
        predCall.enqueue(new Callback<Body>() {
            @Override
            public void onResponse(Call<Body> call, Response<Body> response) {

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
            }

            @Override
            public void onFailure(Call<Body> call, Throwable t) {
                t.printStackTrace();
                Log.e("PredCall", t.getLocalizedMessage());
            }
        });

    }

//    public HashMap<String, String> getPathMap(List<NameValuePair> params) {
//        HashMap<String, String> paramMap = new HashMap<>();
//        String paramValue;
//
//        for (NameValuePair paramPair : params) {
//            if (!TextUtils.isEmpty(paramPair.getName())) {
//                try {
//                    if (paramMap.containsKey(paramPair.getName())) {
//                        // Add the duplicate key and new value onto the previous value
//                        // so (key, value) will now look like (key, value&key=value2)
//                        // which is a hack to work with Retrofit's QueryMap
//                        paramValue = paramMap.get(paramPair.getName());
//                        paramValue += "&" + paramPair.getName() + "=" + URLEncoder.encode(String.valueOf(paramPair.getValue()), "UTF-8");
//                    } else {
//                        // This is the first value, so directly map it
//                        paramValue = URLEncoder.encode(String.valueOf(paramPair.getValue()), "UTF-8");
//                    }
//                    paramMap.put(paramPair.getName(), paramValue);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return paramMap;
//    }


    Marker mMarker;

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            mMarker = googleMap.addMarker(new MarkerOptions().position(loc));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
        }
    };

    private void loadRouteIntoMap(Route route) {

        try {
            getHelper().getRouteDao().createOrUpdate(route);
            for(Stop s: route.getStops()){
//                s.setRoute(route); // Todo needed?
                getHelper().getStopDao().createOrUpdate(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < route.paths.size(); i++) {
            PolylineOptions polylineOptions = new PolylineOptions();
//            if (i == 1 || i ==3 || i == ) {
            Path path = route.paths.get(i);
            for (Point p : path.points) {
                polylineOptions.add(p.getLatLng());
            }
//            }
            polylineOptions.color(route.getColor());
            googleMap.addPolyline(polylineOptions);
        }

        for(Stop stop: route.getStops()){
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(stop.getMarkerPoint())
                    .title(stop.getMarkerTitle())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp)) // TODO improve image.
                    .draggable(false));
            marker.setTag(stop);
            markerList.add(marker);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;
        }else{
            googleMap.setMyLocationEnabled(true);
        }

        Log.i("Route", "Loaded polyLine to googleMap: "+ route.title);
//        showPredictionsToClosestStop();
    }

    private void showPredictionsToClosestStop(){
        final List<Stop> closestStops = findClosestStops();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://webservices.nextbus.com")
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(closestStops.get(0).getMarkerPoint().latitude,
                        closestStops.get(0).getMarkerPoint().longitude), 16));

        AgencyListApi restInterface = retrofit.create(AgencyListApi.class);
        Call<Body> predictionsCall = restInterface.getPredictions("kearney", closestStops.get(0).getTag()); // TODO get route.
//        Call<Body> predictionsCall = restInterface.getPredictions();
//        Log.i("Test", "Url: "+predictionsCall.toString());
//        Log.i("Query", "Q: "+predictionsCall.request().url().query());
        predictionsCall.enqueue(new Callback<Body>() {
            @Override
            public void onResponse(Call<Body> call, Response<Body> response) {
                Body predictions = response.body();
                Body.Predictions p = predictions.getPredictions().get(0);
//                for(int i = 0; i < predictions.getPredictions().size(); i++){
//                    if(i == 0){
//                        prediction1.setText(predictions.getPredictions().get(i).getMinutes());
//                    }else if(i == 1){
//                        prediction2.setText(predictions.getPredictions().get(i).getMinutes());
//                    }
//                }

//                route.setText(closestStops.get(0).toString());
                nearestStop.setText(p.stopTitle);
                route.setText(p.routeTitle);
                String s= "23";
                SpannableString ss1=  new SpannableString(s);
                ss1.setSpan(new RelativeSizeSpan(2f), 0, s.length(), 0); // set size
                ss1.setSpan( new android.text.style.StyleSpan(Typeface.BOLD), 0, s.length(), 0);
                SpannableString ss2=  new SpannableString(" min");

                prediction1.setText(ss1);

                if(p.direction != null){
                    for(int i = 0; i < p.direction.predictions.size(); i++){
                        if(i == 0){
//                            String s= p.direction.predictions.get(i).getMinutes() + "";
//                            SpannableString ss1=  new SpannableString(s);
//                            ss1.setSpan(new RelativeSizeSpan(2f), 0, s.length(), 0); // set size
//                            ss1.setSpan( new android.text.style.StyleSpan(Typeface.BOLD), 0, s.length(), 0);
//
//                            SpannableString ss2=  new SpannableString(" min");

//                            prediction1.setText(p.direction.predictions.get(i).getMinutes() + " min");
                            prediction1.setText(TextUtils.concat(ss1, ss2));
                        }else if(i == 1){
                            prediction2.setText(p.direction.predictions.get(i).getMinutes() + " min");
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<Body> call, Throwable t) {
                Log.i("Distance", "T: "+t.getLocalizedMessage());
                t.printStackTrace();
                App.showToast(t.getLocalizedMessage());
            }
        });

    }

    private List<Stop> findClosestStops(){

        double[] distances = new double[markerList.size()];
        List<Stop> stops = new ArrayList<>();
        for(int i=0; i<markerList.size(); i++){
            Marker marker = markerList.get(i);
            try{
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                LatLng markerLatLng = marker.getPosition();
                float[] results = new float[1];
                Location.distanceBetween(latitude, longitude, markerLatLng.latitude, markerLatLng.longitude, results);
                distances[i] = results[0] * 0.000621371;

                Stop stop = (Stop) marker.getTag();
                stop.setDistance(distances[i]);
                stops.add(stop);

                Log.i("Distance", marker.getTitle() + " ("+ stop.getTag() + ") : " + (results[0] * 0.000621371));
            }catch (SecurityException e){
                App.showToast(e.getLocalizedMessage());
            }
        }

        // order stops by closest on top
//        Arrays.sort(distances);

        Collections.sort(stops, new Comparator<Stop>() {
            @Override
            public int compare(Stop t1, Stop t2) {
                return t1.getDistance().compareTo(t2.getDistance());
            }
        });

        Log.i("Distance", "TTS: "+stops.toString());
        return stops;
    }

}
