package com.elegion.tracktor.util;

public class DistanceConverter {

    public static double fromMetersToFeet(double meters){
        return meters * 3.2808;
    }

    public static double fromKilometersToMiles(double kilometers){
        return kilometers/1.609344;
    }

}
