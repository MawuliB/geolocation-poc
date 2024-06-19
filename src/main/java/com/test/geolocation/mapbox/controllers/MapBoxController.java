package com.test.geolocation.mapbox.controllers;

import com.test.geolocation.googlemap.domain.DistanceBetweenRequest;
import com.test.geolocation.googlemap.domain.NearbyPlacesRequestForLatLang;
import com.test.geolocation.googlemap.domain.NearbyPlacesRequestForLocation;
import com.test.geolocation.mapbox.services.MapBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mapbox")
public class MapBoxController {

    private final MapBoxService mapBoxService;

    @GetMapping("/get-geolocation/{location}")
    public ResponseEntity<?> getGeolocation(
            @PathVariable String location
    ) {
        return ResponseEntity.ok(mapBoxService.fetchGeoLocation(location));
    }

    @GetMapping("/get-geolocation/{lat}/{lng}")
    public ResponseEntity<?> getGeolocation(
            @PathVariable double lat,
            @PathVariable double lng
    ) {
        return ResponseEntity.ok(mapBoxService.fetchGeoLocation(lat, lng));
    }

    @GetMapping("/get-distance")
    public ResponseEntity<?> getDistanceBetweenLocations(
            @RequestBody DistanceBetweenRequest request
    ) {
        return ResponseEntity.ok(mapBoxService.getDistanceBetweenLocations(request));
    }

    @GetMapping("/get-nearby-places")
    public ResponseEntity<?> getNearbyPlaces(
            @RequestBody NearbyPlacesRequestForLatLang request
    ) {
        return ResponseEntity.ok(mapBoxService.searchNearbyPlacesWithCoordinates(request));
    }

    @GetMapping("/get-nearby-places-by-location")
    public ResponseEntity<?> getNearbyPlacesByLocation(
            @RequestBody NearbyPlacesRequestForLocation request
    ) {
        return ResponseEntity.ok(mapBoxService.searchNearbyPlacesByLocation(request));
    }

    @GetMapping("/get-suggested-places")
    public ResponseEntity<?> getSuggestedPlaces(
            @RequestParam String query
    ) {
        return ResponseEntity.ok(mapBoxService.getSuggestedPlaces(query));
    }
}
