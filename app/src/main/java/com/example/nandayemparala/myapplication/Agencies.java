package com.example.nandayemparala.myapplication;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Nanda Yemparala on 8/25/16.
 */
@Root(name = "body")
public class Agencies {

    @Attribute
    public String copyright;

    @ElementList(inline=true)
    List<Agency> agencyList;
}
