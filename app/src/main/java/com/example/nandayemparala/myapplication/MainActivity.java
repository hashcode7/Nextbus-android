package com.example.nandayemparala.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nandayemparala.myapplication.api.AgencyListApi;
import com.example.nandayemparala.myapplication.model.Path;
import com.example.nandayemparala.myapplication.model.Point;
import com.example.nandayemparala.myapplication.model.Route;
import com.example.nandayemparala.myapplication.model.Routes;
import com.example.nandayemparala.myapplication.model.Stop;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button button;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.body);
        button = (Button) findViewById(R.id.load_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMap();
    }

    private void initMap() {
        ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                googleMap = gMap;

                if (googleMap == null) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                            .show();
                }

                loadRoute();
            }
        });
    }


    private void loadRoute() {

        Retrofit retrofit = new Retrofit.Builder()
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
                textView.setText(t.getLocalizedMessage());
            }
        });

    }

    private void loadRouteIntoMap(Route route) {

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
            googleMap.addMarker(new MarkerOptions()
                    .position(stop.getMarkerPoint())
                    .title(stop.getMarkerTitle())
                    .draggable(false)
            );
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

        textView.setText("Loaded polyLine to googleMap");
    }


    private void  loadData(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://webservices.nextbus.com")
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        AgencyListApi restInterface = retrofit.create(AgencyListApi.class);

        Call<Agencies> call = restInterface.getAgencies();
        //asynchronous call
        call.enqueue(new Callback<Agencies>() {
            @Override
            public void onResponse(Call<Agencies> call, Response<Agencies> response) {
                Agencies body = response.body();
                StringBuilder builder = new StringBuilder();
                for(Agency agency : body.agencyList){
                    builder.append("TAG: ");
                    builder.append(agency.tag);
                    builder.append(" ");
                    builder.append("Name: ");
                    builder.append(agency.title);
                    builder.append("\n");
                }

                textView.setText(builder.toString());
            }

            @Override
            public void onFailure(Call<Agencies> call, Throwable t) {
                textView.setText(t.getLocalizedMessage());
            }
        });
    }

}
