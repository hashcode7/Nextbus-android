package com.example.nandayemparala.myapplication.model;

import android.graphics.Color;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Nanda Yemparala on 8/26/16.
 */
@Root(strict = false)
public class Route {


    @ElementList(inline = true, name = "path")
    public List<Path> paths;

    @ElementList(inline = true)
    public List<Stop> stops;

    @Attribute
    public String title;

    @Attribute
    String color;

    public int getColor() {
        return Color.parseColor("#"+color);
    }
}
