package com.example.nandayemparala.myapplication.model;

import org.simpleframework.xml.*;
import org.simpleframework.xml.Path;

import java.util.List;

/**
 * Created by Nanda Yemparala on 8/26/16.
 */
@Path(value = "body")
@Root(name = "body", strict = false)
public class Routes {

    @ElementList(inline=true)
    public List<Route> routes;
}
