package com.example.nandayemparala.myapplication.model;

import com.google.android.gms.maps.model.LatLng;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Nanda Yemparala on 8/26/16.
 */
@Root(strict = false)
public class Stop {

    @Attribute
    String title;
    @Attribute(required = false)
    String shortTitle;
    @Attribute
    double lat;
    @Attribute
    double lon;
    @Attribute
    String tag;

    @Override
    public String toString() {
        return tag +" "+title;
    }

    public LatLng getMarkerPoint(){
        return new LatLng(lat, lon);
    }

    public String getMarkerTitle(){
        if(shortTitle != null){
            return this.shortTitle;
        }else{
            return this.title;
        }
    }

    public String getTag() {
        return tag;
    }


    private Double distance;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
