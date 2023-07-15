package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.model.entity.*;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.service.PlaceService;
import com.hcmute.hotel.service.ProvinceService;
import com.hcmute.hotel.service.StayService;
import com.hcmute.hotel.service.distanceCaculator.DistanceCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PlaceControllerTest {
    @Mock
    private PlaceService placeService;

    @Mock
    private ProvinceService provinceService;

    @Mock
    private AuthenticateHandler authenticateHandler;

    @Mock
    private StayService stayService;

    @InjectMocks
    private PlaceController placeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addPlace_ValidData_Success()
    {
        UserEntity user = mock(UserEntity.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        List<PlaceImageEntity> listImage = new ArrayList<>();
        PlaceImageEntity placeImage = mock(PlaceImageEntity.class);
        listImage.add(placeImage);
        String name = "place1";
        String provinceId = "province1";
        String description = "abc";
        String addressDescription = "xyz";
        String longitude ="0";
        String latitude = "0";
        String timeClose = "01:00";
        String timeOpen = "02:00";
        String type = "abc";
        String minPrice = "5000";
        String maxPrice = "6000";
        String recommendTime = "1";
        MultipartFile[] files = new MultipartFile[0];
        ProvinceEntity province = mock(ProvinceEntity.class);
        PlaceEntity place = mock(PlaceEntity.class);
        when(authenticateHandler.authenticateUser(any(HttpServletRequest.class))).thenReturn(user);
        when(provinceService.getProvinceById(provinceId)).thenReturn(province);
        when(placeService.addPlace(any(PlaceEntity.class))).thenReturn(place);
        when(placeService.addPlaceImg(any(MultipartFile[].class),any(PlaceEntity.class))).thenReturn(listImage);
        ResponseEntity<Object> response = placeController.addPlace(req,name,files,provinceId,description,addressDescription,longitude,latitude,timeClose,timeOpen,type,minPrice,maxPrice,recommendTime);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(place, response.getBody());
    }

    @Test
    void addPlace_InvalidData()
    {
        UserEntity user = mock(UserEntity.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        List<PlaceImageEntity> listImage = new ArrayList<>();
        PlaceImageEntity placeImage = mock(PlaceImageEntity.class);
        listImage.add(placeImage);
        String name = "place1";
        String provinceId = "province1";
        String description = "abc";
        String addressDescription = "xyz";
        String longitude ="0";
        String latitude = "0";
        String timeClose = "01:00";
        String timeOpen = "02:00";
        String type = "abc";
        String minPrice = "5000";
        String maxPrice = "6000";
        String recommendTime = "1";
        MultipartFile[] files = new MultipartFile[0];
        ProvinceEntity province = mock(ProvinceEntity.class);
        PlaceEntity place = mock(PlaceEntity.class);
        when(authenticateHandler.authenticateUser(any(HttpServletRequest.class))).thenReturn(user);
        when(provinceService.getProvinceById(provinceId)).thenReturn(null);
        ResponseEntity<Object> response = placeController.addPlace(req,name,files,provinceId,description,addressDescription,longitude,latitude,timeClose,timeOpen,type,minPrice,maxPrice,recommendTime);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ErrorResponse.class, response.getBody().getClass());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("PROVINCE_NOT_FOUND", errorResponse.getMessage());
    }
    @Test
    void testGetPlaceById_ExistingPlace_ReturnsSuccessResponse() {
        String placeId = "1";
        PlaceEntity place = new PlaceEntity();
        place.setId(placeId);
        place.setName("Test Place");

        when(placeService.getPlaceById(anyString())).thenReturn(place);

        ResponseEntity<Object> response = placeController.getPlaceById(placeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(place, response.getBody());
        verify(placeService, times(1)).getPlaceById(placeId);
    }

    @Test
    void testGetPlaceById_NonExistingPlace_ReturnsNotFoundResponse() {
        String placeId = "1";

        when(placeService.getPlaceById(anyString())).thenReturn(null);
        ResponseEntity<Object> response = placeController.getPlaceById(placeId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ErrorResponse.class, response.getBody().getClass());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("PLACE_NOT_FOUND", errorResponse.getMessage());
        verify(placeService, times(1)).getPlaceById(placeId);
    }

}
