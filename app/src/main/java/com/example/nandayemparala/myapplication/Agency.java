package com.example.nandayemparala.myapplication;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Nanda Yemparala on 8/25/16.
 */

@Root(name = "agency", strict = false)
public class Agency {

    @Attribute
    public String tag;

    @Attribute
    public String title;

    @Attribute(required = false)
    public String regionTitle;

    @Attribute(required = false)
    public String shortTitle;

}
