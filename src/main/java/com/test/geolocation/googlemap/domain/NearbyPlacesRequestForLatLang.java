package com.test.geolocation.googlemap.domain;

public record NearbyPlacesRequestForLatLang(
        String lat,
        String lon,
        String radius,
        String type
){
}
