package com.test.geolocation.googlemap.controllers;

import com.google.maps.errors.ApiException;
import com.test.geolocation.googlemap.domain.DistanceBetweenRequest;
import com.test.geolocation.googlemap.domain.NearbyPlacesRequestForLatLang;
import com.test.geolocation.googlemap.domain.NearbyPlacesRequestForLocation;
import com.test.geolocation.googlemap.services.GoogleMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/google-map")
public class GoogleMapController {

    private final GoogleMapService googleMapService;

    @GetMapping("/get-geolocation/{location}")
    public ResponseEntity<?> getGeolocation(
            @PathVariable String location
    ) throws IOException, InterruptedException, ApiException {
        String loc = googleMapService.fetchGeoLocation(location);
        return ResponseEntity.ok(loc);
    }

    @GetMapping("/get-geolocation/{lat}/{lng}")
    public ResponseEntity<?> getGeolocation(
            @PathVariable double lat,
            @PathVariable double lng
    ) throws IOException, InterruptedException, ApiException {
        String loc = googleMapService.fetchGeoLocation(lat, lng);
        return ResponseEntity.ok(loc);
    }

    @GetMapping("/get-distance")
    public ResponseEntity<?> getDistanceBetweenLocations(
            @RequestBody DistanceBetweenRequest request
    ) throws InterruptedException, ApiException, IOException {
        return ResponseEntity.ok(googleMapService.getDistanceBetweenLocations(request));
    }

    @GetMapping("/get-nearby-places")
    public ResponseEntity<?> getNearbyPlaces(
            @RequestBody NearbyPlacesRequestForLatLang request
    ) throws InterruptedException, ApiException, IOException {
        return ResponseEntity.ok(googleMapService.searchNearbyPlaces(request));
    }

    @GetMapping("/get-nearby-places-by-location")
    public ResponseEntity<?> getNearbyPlacesByLocation(
            @RequestBody NearbyPlacesRequestForLocation request
    ) throws InterruptedException, ApiException, IOException {
        return ResponseEntity.ok(googleMapService.searchNearbyPlacesByLocation(request));
    }

    @GetMapping("/get-suggested-places")
    public ResponseEntity<?> getSuggestedPlaces(
            @RequestParam(value = "query") String query
    ) throws InterruptedException, ApiException, IOException {
        return ResponseEntity.ok(googleMapService.getSuggestedPlacesWithQuery(query));
    }
}
