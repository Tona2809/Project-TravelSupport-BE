package com.hcmute.hotel.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@ComponentScan
@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlacesController {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<Object> getNearbyPlaces(@RequestParam("lat") double latitude,
                                             @RequestParam("lng") double longitude) {
        String url = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=30000&type=tourist_attraction&key=%s",
                latitude, longitude, apiKey);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        return new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
    }
}
