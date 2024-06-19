package com.test.geolocation.googlemap.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.*;
import com.google.maps.model.*;
import com.google.maps.errors.ApiException;
import com.test.geolocation.googlemap.domain.DistanceBetweenRequest;
import com.test.geolocation.googlemap.domain.NearbyPlacesRequestForLatLang;
import com.test.geolocation.googlemap.domain.NearbyPlacesRequestForLocation;
import com.test.geolocation.googlemap.utilities.LocalTimeAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalTime;

@Service
public class GoogleMapService {

    @Value("${google-map-api-key}")
    private String apiKey;

    private final GeoApiContext context;
    private GeocodingResult[] results;
    private final Gson gson;

    public GoogleMapService() {
        this.context = new GeoApiContext.Builder()
                .apiKey("API_KEY")
                .build();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .create();
    }

    public String fetchGeoLocation(String location) throws IOException, InterruptedException, ApiException {
        this.results = GeocodingApi.geocode(context, location).await();
        return getGeoLocation(results);
    }

    public String fetchGeoLocation(double lat, double lng) throws IOException, InterruptedException, ApiException {
        LatLng latLng = new LatLng(lat, lng);
        this.results = GeocodingApi.reverseGeocode(context, latLng).await();
        return getGeoLocation(results);
    }

    private String getGeoLocation( GeocodingResult[] results ) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(results);
    }

    public String getDistanceBetweenLocations(DistanceBetweenRequest request) throws InterruptedException, ApiException, IOException {
        LatLng origin = new LatLng(Double.parseDouble(request.lat1()), Double.parseDouble(request.lon1()));
        LatLng destination = new LatLng(Double.parseDouble(request.lat2()), Double.parseDouble(request.lon2()));

        DistanceMatrix result = DistanceMatrixApi.newRequest(context)
                .origins(origin)
                .destinations(destination)
                .mode(TravelMode.valueOf(request.travelMode()))
                .await();

        if (result.rows[0].elements[0].distance == null) {
            return "No route found for the specified travel mode.";
        }

        return "Distance: " + result.rows[0].elements[0].distance.humanReadable + " Time: " + result.rows[0].elements[0].duration.humanReadable;
    }

    public String searchNearbyPlaces(NearbyPlacesRequestForLatLang request) throws InterruptedException, ApiException, IOException {
        LatLng location = new LatLng(Double.parseDouble(request.lat()), Double.parseDouble(request.lon()));
        GeocodingResult[] results = GeocodingApi.reverseGeocode(context, location).await();
        LatLng latLng = results[0].geometry.location;
        return getNearbyPlacesWithCoordinates(latLng, request.type(), Integer.parseInt(request.radius()));
    }

    private String getNearbyPlacesWithCoordinates(LatLng latLng, String type, int radius) throws InterruptedException, ApiException, IOException {
        PlacesSearchResponse response = PlacesApi.nearbySearchQuery(context, latLng)
                .radius(radius)
                .type(PlaceType.valueOf(type))
                .await();

        PlacesSearchResult[] results = response.results;
        return gson.toJson(results);
    }

    public String searchNearbyPlacesByLocation(NearbyPlacesRequestForLocation request) throws InterruptedException, ApiException, IOException {
        return getNearbyPlacesWithLocation(request.location(), request.type(), Integer.parseInt(request.radius()));
    }

    private String getNearbyPlacesWithLocation(String location, String type, int radius) throws InterruptedException, ApiException, IOException {
        GeocodingResult[] results = GeocodingApi.geocode(context, location).await();
        if (results.length == 0) {
            return "No results found for the specified location.";
        }
        LatLng latLng = results[0].geometry.location;
        return getNearbyPlacesWithCoordinates(latLng, type, radius);
    }

    public String getSuggestedPlacesWithQuery(String query)
            throws InterruptedException, ApiException, IOException { // Can Add Location (LatLng) as a parameter
        QueryAutocompleteRequest response = PlacesApi.queryAutocomplete(context, query);
        AutocompletePrediction[] results = response.await();
        return gson.toJson(results);
    }
}