package com.example.nandayemparala.myapplication.model;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Nanda Yemparala on 8/26/16.
 */
@DatabaseTable
@Root(strict = false)
public class Stop {

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;

    @DatabaseField
    @Attribute
    String title;

    @DatabaseField
    @Attribute(required = false)
    String shortTitle;

    @DatabaseField
    @Attribute
    double lat;

    @DatabaseField
    @Attribute
    double lon;

    @DatabaseField(uniqueCombo = true)
    @Attribute
    String tag;

    @DatabaseField(foreign = true, uniqueCombo = true)
    private Route route;

    @DatabaseField(columnName = "stop_sequence")
    private int stopNumber;

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

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public int getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(int stopNumber) {
        this.stopNumber = stopNumber;
    }
}
