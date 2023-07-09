package com.hcmute.hotel.service.distanceCaculator;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DistanceCalculator {

    private static final String API_KEY = "";
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json";

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        String url = String.format("%s?origins=%f,%f&destinations=%f,%f&key=%s",
                BASE_URL, lat1, lon1, lat2, lon2, API_KEY);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DistanceMatrixApiResponse> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, null, DistanceMatrixApiResponse.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            DistanceMatrixApiResponse response = responseEntity.getBody();
            if (response != null && response.getRows().length > 0 && response.getRows()[0].getElements().length > 0) {
                DistanceElement element = response.getRows()[0].getElements()[0];
                if (element.getStatus().equals("OK")) {
                    return element.getDistance().getValue() / 1000.0; // Convert meters to kilometers
                }
            }
        }

        throw new RuntimeException("Failed to calculate distance using Google Maps API");
    }

    private static class DistanceMatrixApiResponse {
        private DistanceMatrixRow[] rows;

        public DistanceMatrixRow[] getRows() {
            return rows;
        }

        public void setRows(DistanceMatrixRow[] rows) {
            this.rows = rows;
        }
    }

    private static class DistanceMatrixRow {
        private DistanceElement[] elements;

        public DistanceElement[] getElements() {
            return elements;
        }

        public void setElements(DistanceElement[] elements) {
            this.elements = elements;
        }
    }

    private static class DistanceElement {
        private String status;
        private Distance distance;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Distance getDistance() {
            return distance;
        }

        public void setDistance(Distance distance) {
            this.distance = distance;
        }
    }

    private static class Distance {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}