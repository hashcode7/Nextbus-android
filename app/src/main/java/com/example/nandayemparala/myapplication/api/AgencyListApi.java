package com.example.nandayemparala.myapplication.api;

import com.example.nandayemparala.myapplication.Agencies;
import com.example.nandayemparala.myapplication.model.Body;
import com.example.nandayemparala.myapplication.model.Routes;

import org.simpleframework.xml.Path;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Nanda Yemparala on 8/25/16.
 */
public interface AgencyListApi {

    @GET("/service/publicXMLFeed?command=agencyList")
    Call<Agencies> getAgencies();

    @GET("/service/publicXMLFeed?a=rutgers-newark&command=routeConfig")
    Call<Routes> getRoutes();

    @GET("/service/publicXMLFeed?a=rutgers&command=predictions")
    Call<Body> getPredictions(@Query("r") String routeTag, @Query("s") String stopTag);

//    @GET("/service/publicXMLFeed?a=rutgers&command=predictions&r=penn&s=njit")
//    Call<Body> getPredictions();

}
