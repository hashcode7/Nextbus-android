package com.example.nandayemparala.myapplication.model;

import com.google.android.gms.maps.model.LatLng;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Nanda Yemparala on 8/26/16.
 */
@Root
public class Point {

    @Attribute(name = "lat")
    double latitute;

    @Attribute(name = "lon")
    double longitude;

    public LatLng getLatLng(){
        return new LatLng(latitute, longitude);
    }
}
