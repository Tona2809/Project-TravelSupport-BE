package com.hcmute.hotel.controller;

import com.hcmute.hotel.controller.AmenitiesController;
import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.model.entity.AmenitiesEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.service.AmenitiesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AmenitiesControllerTest {

    @Mock
    private AmenitiesService amenitiesService;

    @Mock
    private AuthenticateHandler authenticateHandler;

    @InjectMocks
    private AmenitiesController amenitiesController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addNewAmenities_ValidData_Success() {
        // Mock request parameters
        String name = "Test Amenities";
        MultipartFile file = mock(MultipartFile.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        UserEntity user = mock(UserEntity.class);


        when(authenticateHandler.authenticateUser(any(HttpServletRequest.class))).thenReturn(user);

        amenitiesController.setAuthenticateHandler(authenticateHandler);

        when(amenitiesService.getAmenitiesByName(name)).thenReturn(null);

        String imageUrl = "http://example.com/image.jpg";
        when(amenitiesService.addAmenitiesIcon(eq(file), any(AmenitiesEntity.class))).thenReturn(imageUrl);

        AmenitiesEntity savedAmenities = new AmenitiesEntity();
        when(amenitiesService.addAmenities(any(AmenitiesEntity.class))).thenReturn(savedAmenities);

        ResponseEntity<Object> response = amenitiesController.addNewAmenities(name, req, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedAmenities, response.getBody());
    }


    @Test
    void addNewAmenities_AmenitiesNameAlreadyExists_BadRequest() {
        // Mock request parameters
        String name = "Test Amenities";
        MultipartFile file = mock(MultipartFile.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        UserEntity user = mock(UserEntity.class);

        // Mock authenticateUser method
        when(authenticateHandler.authenticateUser(any(HttpServletRequest.class))).thenReturn(user);

        amenitiesController.setAuthenticateHandler(authenticateHandler);

        // Mock getAmenitiesByName method
        AmenitiesEntity existingAmenities = new AmenitiesEntity();
        when(amenitiesService.getAmenitiesByName(name)).thenReturn(existingAmenities);

        // Perform the test
        ResponseEntity<Object> response = amenitiesController.addNewAmenities(name, req, file);

        // Verify the result
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorResponse.class, response.getBody().getClass());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("AMENITIES_NAME_ALREADY_EXISTS", errorResponse.getMessage());
    }

    @Test
    void updateAmenities_ValidData_Success()
    {
        String name = "Test Amenities";
        String id = "randomId";
        AmenitiesEntity amenities = mock(AmenitiesEntity.class);
        when(amenitiesService.getAmenitiesById(id)).thenReturn(amenities);
        when(amenitiesService.addAmenities(any(AmenitiesEntity.class))).thenReturn(amenities);

        ResponseEntity<Object> response = amenitiesController.updateAmenities(id,name);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(amenities, response.getBody());
    }

    @Test
    void updateAmenities_InvalidData_AmenitiesNotFound()
    {
        String name = "Test Amenities";
        String id = "randomId";
        AmenitiesEntity amenities = mock(AmenitiesEntity.class);
        when(amenitiesService.getAmenitiesById(id)).thenReturn(null);

        ResponseEntity<Object> response = amenitiesController.updateAmenities(id,name);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ErrorResponse.class, response.getBody().getClass());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("AMENITIES_NOT_FOUND_OR_EXISTS", errorResponse.getMessage());
    }

    @Test
    void deleteAmenities_ValidData_Success()
    {
        String id = "randomId";
        AmenitiesEntity amenities = mock(AmenitiesEntity.class);
        when(amenitiesService.getAmenitiesById(id)).thenReturn(amenities);
        doNothing().when(amenitiesService).deleteAmenities(id);
        ResponseEntity<Object> response = amenitiesController.deleteAmenities(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteAmenities_InvalidData_AmenitiesNotFound()
    {
        String id = "randomId";
        when(amenitiesService.getAmenitiesById(id)).thenReturn(null);

        ResponseEntity<Object> response = amenitiesController.deleteAmenities(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ErrorResponse.class, response.getBody().getClass());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("AMENITIES_NOT_FOUND", errorResponse.getMessage());
    }

    @Test
    void getAllAmenities_Success()
    {
        List<AmenitiesEntity> mockAmenitiesList = new ArrayList<>();
        AmenitiesEntity mockAmenities1 = mock(AmenitiesEntity.class);
        mockAmenitiesList.add(mockAmenities1);
        when(amenitiesService.getAllAmenities()).thenReturn(mockAmenitiesList);
        ResponseEntity<Object> response = amenitiesController.getAllAmenities();
        Map<String, Object> map = (Map<String, Object>) response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockAmenitiesList, map.get("content"));
    }


}
