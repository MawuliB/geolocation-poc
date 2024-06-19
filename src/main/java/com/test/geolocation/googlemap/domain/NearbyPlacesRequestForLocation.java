package com.test.geolocation.googlemap.domain;

public record NearbyPlacesRequestForLocation(
        String location,
        String radius,
        String type
){}
