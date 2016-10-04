package com.example.nandayemparala.myapplication.fragment;
/*
 * Created by Nanda Yemparala on 9/12/16.
 */

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.adapter.PredictionsAdapter;
import com.example.nandayemparala.myapplication.api.PredictionsApi;
import com.example.nandayemparala.myapplication.application.App;
import com.example.nandayemparala.myapplication.model.Body;
import com.example.nandayemparala.myapplication.model.Route;
import com.example.nandayemparala.myapplication.model.Stop;
import com.example.nandayemparala.myapplication.model.ormlite.DatabaseHelper;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@EFragment(R.layout.stops_list_layout)
public class PredictionsFragment extends Fragment {

    @FragmentArg
    String routeTag;
    @ViewById(R.id.stops_list)
    RecyclerView stopsList;
    @ViewById(R.id.refresh_list)
    Button refreshList;
    @ViewById(R.id.noOfVehicles)
    TextView noOfVehicles;

    RecyclerTouchListener listener;

    @AfterViews
    void downloadPredictions(){
        stopsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        listener = new RecyclerTouchListener(getActivity(), stopsList);
        listener
                .setIndependentViews(R.id.fav_button)
                .setClickable(new RecyclerTouchListener.OnRowClickListener() {
                    @Override
                    public void onRowClicked(int position) {
                        App.showToast("Pos: "+position);
                    }

                    @Override
                    public void onIndependentViewClicked(int independentViewID, int position) {
                        if(independentViewID == R.id.fav_button){
                            App.showToast("Hello there.. I am at: "+position);
                        }
                    }
                });
//        stopsList.addItemDecoration(new BackgroundColorDecoration(getActivity()));
        getPredictions();
    }

    @Override
    public void onResume() {
        super.onResume();

        stopsList.addOnItemTouchListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();

        stopsList.removeOnItemTouchListener(listener);
    }

    @Click(R.id.refresh_list)
    void refreshList(){
        getPredictions();
    }

    int vehiclesCount = -1;

    @Background
    void getPredictions(){
        try{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://webservices.nextbus.com")
                    .client(new OkHttpClient())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();

            Route route = DatabaseHelper.getInstance(this.getActivity()).getRouteDao().queryForEq("tag", routeTag).get(0);
            List<String> stops = new ArrayList<>();
            for(Stop stop: route.getStops()){
                stops.add(route.getTag() + "|" + stop.getTag());
            }
            PredictionsApi predictionsApi = retrofit.create(PredictionsApi.class);
            Response<Body> response= predictionsApi.getPredictions(stops).execute();
            Log.i("Response", "Response: " + response.raw().toString());
            Body body = response.body();

            List<Body.Predictions> bodyPredictions = body.getPredictions();

            for(Body.Predictions predictions1: bodyPredictions){
                if(predictions1.direction == null){
                    Log.e("Preds", "Prediction Direction is null");
                    continue;
                }
                if(predictions1.direction.getPredictions().size() == 0){
                    Log.e("Preds", "Predictions is null");
                    continue;
                }
                if(vehiclesCount < 0){
                    vehiclesCount = predictions1.direction.getNoOfVehicles();
                }
                for(Body.Prediction p : predictions1.direction.getPredictions()){
                    Log.i("Prediction", "Stop"+p.getMinutes()+ " Min:" +p.getSeconds() +" Vehicles: "+p.getVehicle());
                }
            }
            loadPredictions(bodyPredictions);
        } catch (Exception e) {
            onError(e);
        }
    }

    @UiThread
    void onError(Exception e){
        e.printStackTrace();
        App.showToast("Error");
    }

    @UiThread
    void loadPredictions(List<Body.Predictions> predictions){
        try{
            Collections.sort(predictions, new StopsOrder(routeTag));
            noOfVehicles.setText("Vehicles: "+ (vehiclesCount < 0 ? 0: vehiclesCount));
            PredictionsAdapter adapter = new PredictionsAdapter(predictions);
            stopsList.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private class StopsOrder implements Comparator<Body.Predictions>{

        HashMap<String, Integer> sequence= new HashMap<>();

        StopsOrder(String routeTag) throws SQLException{
            Route route = App.getDatabaseHelper().getRouteDao().queryForEq("tag", routeTag).get(0);
            for(Stop s: route.getStops()){
                Log.i("TEST", "TAG: "+s.getTag() + " SN: "+s.getStopNumber());
                sequence.put(s.getTag(), s.getStopNumber());
            }
        }

        @Override
        public int compare(Body.Predictions p1, Body.Predictions p2) {
            int p1StopNumber = sequence.get(p1.stopTag);
            int p2StopNumber = sequence.get(p2.stopTag);
            return p1StopNumber - p2StopNumber;
        }
    }
}
