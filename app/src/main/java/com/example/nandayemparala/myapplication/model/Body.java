package com.example.nandayemparala.myapplication.model;

import org.simpleframework.xml.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Nanda Yemparala on 8/30/16.
 */
@Root(name = "body", strict = false)
public class Body {

    @Attribute
    String copyright;

    @ElementList(inline = true)
    ArrayList<Predictions> predictions;

    public ArrayList<Predictions> getPredictions(){
//        if(predictions.direction != null){
//            return predictions.direction.predictions;
//        }
//        return null;
        return predictions;
    }

    @Root(strict = false)
    public static class Predictions {
        @Attribute
        public String routeTitle;
        @Attribute
        public String routeTag;
        @Attribute
        public String stopTitle;
        @Attribute
        public String stopTag;

        @Element(required = false)
        public Direction direction;
    }

    @Root(strict = false)
    public static class Direction{

        @ElementList(inline = true, name = "prediction")
        public List<Prediction> predictions;

        public int getNoOfVehicles(){
            if(predictions != null){

                Set<Integer> vehicles = new HashSet<>();
                for(Prediction p: predictions){
                    if(!vehicles.contains(p.vehicle)){
                        vehicles.add(p.getVehicle());
                    }
                }
                return vehicles.size();
            }

            return 0;
        }

        public List<Prediction> getPredictions() {
            if(predictions == null){
                return new ArrayList<>();
            }
            return predictions;
        }
    }

    @Root(strict = false)
    public static class Prediction {

        @Attribute(required = false)
        int seconds;
        @Attribute(required = false)
        int minutes;
        @Attribute(required = false)
        int vehicle;


        public int getSeconds() {
            return seconds;
        }

        public int getMinutes() {
            return minutes;
        }

        public int getVehicle() {
            return vehicle;
        }
    }

}


