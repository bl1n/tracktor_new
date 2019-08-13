package com.elegion.tracktor.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DistanceConverterTest {



    @Test
    public void fromMetersToFeet() {
        assertEquals(3.2808, DistanceConverter.fromMetersToFeet(1), 0);
    }

    @Test
    public void fromKilometersToMiles() {
        assertEquals(0.621, DistanceConverter.fromKilometersToMiles(1), 0.001);

    }
}