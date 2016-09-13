package com.example.nandayemparala.myapplication.model;

import android.graphics.Color;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by Nanda Yemparala on 8/26/16.
 */
@DatabaseTable
@Root(strict = false)
public class Route{

//    @DatabaseField(generatedId = true, columnName = "_id")
//    private int id;

    @ElementList(inline = true, name = "path")
    public List<Path> paths;

    @ForeignCollectionField(eager = true, orderColumnName = "stop_sequence", orderAscending = true)
    @ElementList(inline = true)
    private Collection<Stop> stops;

    @DatabaseField
    @Attribute
    public String title;

    @DatabaseField(unique = true, id = true)
    @Attribute
    private String tag;

    @DatabaseField
    @Attribute
    String color;

    public int getColor() {
        return Color.parseColor("#"+color);
    }

    public String getTag() {
        return tag;
    }

    public void setStops(Collection<Stop> children) {
        this.stops = children;
    }

    public Collection<Stop> getStops() {
        return stops;
    }
}
