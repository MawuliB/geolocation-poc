package com.test.geolocation.googlemap.domain;

public record DistanceBetweenRequest (
        String lat1,
        String lon1,
        String lat2,
        String lon2,
        String travelMode
){}
