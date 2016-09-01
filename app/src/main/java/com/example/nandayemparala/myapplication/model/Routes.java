package com.example.nandayemparala.myapplication.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Nanda Yemparala on 8/26/16.
 */
@Root(name = "body", strict = false)
public class Routes {

    @ElementList(inline=true)
    public List<Route> routes;
}
