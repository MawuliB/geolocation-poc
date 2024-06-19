package com.test.geolocation.mapbox.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.geolocation.googlemap.domain.DistanceBetweenRequest;
import com.test.geolocation.googlemap.domain.NearbyPlacesRequestForLatLang;
import com.test.geolocation.googlemap.domain.NearbyPlacesRequestForLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MapBoxService {

    @Value("${mapbox-api-key}")
    private final String MAPBOX_API_KEY = "";

    private final String MAPBOX_API_URL = "https://api.mapbox.com/";

    private final RestTemplate restTemplate;

    public MapBoxService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchGeoLocation(String location) {
        return restTemplate.getForObject(MAPBOX_API_URL + "search/geocode/v6/forward?q={location}&access_token={apiKey}", String.class, location, MAPBOX_API_KEY);
    }

    public String fetchGeoLocation(double lat, double lng) {
        return restTemplate.getForObject(MAPBOX_API_URL + "search/geocode/v6/reverse?latitude={lat}&longitude={lng}&access_token={apiKey}", String.class, lat, lng, MAPBOX_API_KEY);
    }

    public String getDistanceBetweenLocations(DistanceBetweenRequest request) { // distance btw for driving
        String res = restTemplate.getForObject(MAPBOX_API_URL + "directions/v5/mapbox/{mode}/{lon1},{lat1};{lon2},{lat2}?access_token={apiKey}", String.class, request.travelMode().toLowerCase(), request.lon1(), request.lat1(), request.lon2(), request.lat2(), MAPBOX_API_KEY);
        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(res);
            JsonNode route = root.path("routes").get(0);
            double distanceMeters = route.path("distance").asDouble();
            double durationSeconds = route.path("duration").asDouble();

            // Convert meters to kilometers and seconds to minutes
            double distanceKm = distanceMeters / 1000;
            double durationMinutes = durationSeconds / 60;

            // Format the result
            return String.format("Distance: %.2f km, Time: %.2f min", distanceKm, durationMinutes);
        } catch (Exception e) {
            return "Error parsing the response";
        }
    }

    public String searchNearbyPlacesWithCoordinates(NearbyPlacesRequestForLatLang request) {
//        return restTemplate.getForObject(MAPBOX_API_URL + "search/searchbox/v1/category/{category}?origin={lon},{lat}&access_token={apiKey}", String.class, request.type().toLowerCase(), request.lon(), request.lat(), MAPBOX_API_KEY);
        return restTemplate.getForObject(MAPBOX_API_URL + "geocoding/v5/mapbox.places/{query}.json?proximity={lon},{lat}&limit=10&types=poi&access_token={apiKey}", String.class, request.type().toLowerCase(), request.lon(), request.lat(), MAPBOX_API_KEY);
    }

    public String searchNearbyPlacesByLocation(NearbyPlacesRequestForLocation request) {
        // TODO: Convert location to lat, lng
        return restTemplate.getForObject(MAPBOX_API_URL + "search/searchbox/v1/category/{category}?access_token={apiKey}", String.class, request.type(), MAPBOX_API_KEY);
    }

    public String getSuggestedPlaces(String query) {
//        return restTemplate.getForObject(MAPBOX_API_URL + "geocoding/v5/mapbox.places/{query}.json?access_token={apiKey}&country=GH", String.class, query, MAPBOX_API_KEY);
        return restTemplate.getForObject(MAPBOX_API_URL + "search/geocode/v6/forward?q={query}&access_token={apiKey}&country=GH", String.class, query, MAPBOX_API_KEY);
    }
}
