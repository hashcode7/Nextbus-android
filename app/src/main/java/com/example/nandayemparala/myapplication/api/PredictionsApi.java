package com.example.nandayemparala.myapplication.api;

import com.example.nandayemparala.myapplication.model.Body;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Nanda Yemparala on 9/2/16.
 */
public interface PredictionsApi {


//    /service/publicXMLFeed?a=rutgers&command=predictionsForMultiStops&stops=kearney|njit&stops=connect|boydhall_c
//    &stops=kearney|njit&stops=connect|boydhall_c
    @GET("/service/publicXMLFeed?a=rutgers&command=predictionsForMultiStops")
    Call<Body> getPredictions(@Query("stops") List<String> stops);
//    Call<Body> getPredictions(@QueryMap Map<String, String> stops);

}
