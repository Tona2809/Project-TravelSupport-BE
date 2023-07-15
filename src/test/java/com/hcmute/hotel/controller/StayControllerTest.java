package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.model.entity.*;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StayControllerTest {

    @InjectMocks
    private StayController stayController;

    @Mock
    private AuthenticateHandler authenticateHandler;

    @Mock
    private BookingService bookingService;

    @Mock
    private StayService stayService;

    @Mock
    private AmenitiesService amenitiesService;

    @Mock
    private UserService userService;

    @Mock
    private ProvinceService provinceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddStay_ValidRequest_ReturnsSuccessResponse() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserEntity user = new UserEntity();
        StayEntity stay = new StayEntity();
        ProvinceEntity province = new ProvinceEntity();
        MockMultipartFile[] files = new MockMultipartFile[0];
        List<StayImageEntity> listImage = new ArrayList<>();
        String[] amenities = new String[0];

        when(authenticateHandler.authenticateUser(request)).thenReturn(user);
        when(stayService.saveStay(any(StayEntity.class))).thenReturn(stay);
        when(provinceService.getProvinceById(anyString())).thenReturn(province);
        when(amenitiesService.getAmenitiesById(anyString())).thenReturn(new AmenitiesEntity());
        when(stayService.addStayImg(any(), any(StayEntity.class))).thenReturn(listImage);
        when(stayService.saveStay(any(StayEntity.class))).thenReturn(stay);

        ResponseEntity<Object> response = stayController.addStay(
                request, "Stay Name", "Address Description", files, "Type",
                "Stay Description", "Province ID", "Checkin Time",
                "Checkout Time", "0", "0", amenities
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(stay, response.getBody());
    }

    @Test
    void testAddStay_InvalidProvinceId_ReturnsErrorResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        UserEntity user = new UserEntity();
        MockMultipartFile[] files = new MockMultipartFile[0];
        String[] amenities = new String[0];

        when(authenticateHandler.authenticateUser(any(HttpServletRequest.class))).thenReturn(user);
        when(provinceService.getProvinceById(anyString())).thenReturn(null);

        ResponseEntity<Object> response = stayController.addStay(
                request, "Stay Name", "Address Description", files, "Type",
                "Stay Description", "Province ID", "Checkin Time",
                "Checkout Time", "0", "0", amenities
        );


        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllStay_ReturnsAllStays() {
        List<StayEntity> stayList = new ArrayList<>();
        stayList.add(new StayEntity());
        stayList.add(new StayEntity());

        when(stayService.getAllStay()).thenReturn(stayList);

        ResponseEntity<Object> response = stayController.getAllStay();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("content", stayList);
        assertEquals(expectedMap, response.getBody());
    }

    @Test
    void testGetStayByUid_ExistingId_ReturnsStayEntity() {
        String stayId = "123";
        StayEntity expectedStay = new StayEntity();
        expectedStay.setId(stayId);

        when(stayService.getStayById(stayId)).thenReturn(expectedStay);

        
        ResponseEntity<Object> response = stayController.getStayByUid(stayId);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedStay, response.getBody());
    }

    @Test
    void testGetStayByUid_NonExistingId_ReturnsErrorResponse() {
        String stayId = "123";

        when(stayService.getStayById(stayId)).thenReturn(null);

        
        ResponseEntity<Object> response = stayController.getStayByUid(stayId);

        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


}