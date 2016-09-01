package com.example.nandayemparala.myapplication.model;

import com.google.android.gms.maps.model.LatLng;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Nanda Yemparala on 8/26/16.
 */
@Root
public class Path {

    @ElementList(inline = true)
    public List<Point> points;
}
