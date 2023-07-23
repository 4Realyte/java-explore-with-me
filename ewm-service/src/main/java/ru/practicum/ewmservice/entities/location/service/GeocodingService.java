package ru.practicum.ewmservice.entities.location.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {
    private final GeoApiContext context;

    public GeocodingService(@Value("${geocode.api.key}") String apiKey) {
        context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public String getAddress(Float lat, Float lon) {
        GeocodingResult[] result = GeocodingApi.reverseGeocode(context, new LatLng(lat, lon))
                .language("ru")
                .awaitIgnoreError();
        if (result != null) {
            return result[0].formattedAddress;
        } else {
            return "";
        }
    }
}
