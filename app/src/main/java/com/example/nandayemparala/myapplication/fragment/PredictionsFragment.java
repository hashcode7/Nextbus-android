package com.example.nandayemparala.myapplication.fragment;/*
 * Created by Nanda Yemparala on 9/12/16.
 */

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nandayemparala.myapplication.R;
import com.example.nandayemparala.myapplication.activity.BaseActivity;
import com.example.nandayemparala.myapplication.adapter.StopsAdapter;
import com.example.nandayemparala.myapplication.api.PredictionsApi;
import com.example.nandayemparala.myapplication.application.App;
import com.example.nandayemparala.myapplication.model.Body;
import com.example.nandayemparala.myapplication.model.Route;
import com.example.nandayemparala.myapplication.model.Stop;
import com.example.nandayemparala.myapplication.model.ormlite.DatabaseHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
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
    ListView stopsList;

    @ViewById(R.id.refresh_list)
    Button refreshList;

    @ViewById(R.id.noOfVehicles)
    TextView noOfVehicles;


    @AfterViews
    void downloadPredictions(){
        getPredictions();
    }

    @Click(R.id.refresh_list)
    void refreshList(){
        getPredictions();
    }

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
            Response<Body> response = null;
            response = predictionsApi.getPredictions(stops).execute();
            Log.i("Response", "Response: " + response.raw().toString());
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
//            noOfVehicles.setText("Vehicles: "+bodyPredictions.get(0).direction.getNoOfVehicles());
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
        StopsAdapter adapter = new StopsAdapter(getActivity(), R.layout.stop_list_row, predictions);
        stopsList.setAdapter(adapter);
    }
}
