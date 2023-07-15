package com.hcmute.hotel.controller;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.payload.request.Province.AddNewProvinceRequest;
import com.hcmute.hotel.model.payload.response.DataResponse;
import com.hcmute.hotel.model.payload.response.MessageResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.ProvinceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProvinceControllerTest {
    @Mock
    private ProvinceService provinceService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private ProvinceController provinceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProvinces_ReturnsListOfProvinces() {
        
        List<ProvinceEntity> provinces = new ArrayList<>();
        provinces.add(new ProvinceEntity());
        provinces.add(new ProvinceEntity());

        when(provinceService.getAllProvinces()).thenReturn(provinces);

        
        DataResponse response = provinceController.getAllProvinces();

        
        assertEquals(provinces, response.getContent());
        verify(provinceService, times(1)).getAllProvinces();
    }

    @Test
    void testGetProvinceById_ExistingProvince_ReturnsSuccessResponse() {
        
        String provinceId = "1";
        ProvinceEntity province = new ProvinceEntity();
        province.setId(provinceId);

        when(provinceService.getProvinceById(anyString())).thenReturn(province);

        
        ResponseEntity<Object> response = provinceController.getProvinceById(provinceId);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("content", province);
        assertEquals(expectedMap, response.getBody());
        verify(provinceService, times(1)).getProvinceById(provinceId);
    }

    @Test
    void testGetProvinceById_NonExistingProvince_ReturnsNotFoundResponse() {
        
        String provinceId = "1";

        when(provinceService.getProvinceById(anyString())).thenReturn(null);

        
        ResponseEntity<Object> response = provinceController.getProvinceById(provinceId);

        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(provinceService, times(1)).getProvinceById(provinceId);
    }

    @Test
    void testAddProvince_ValidRequest_ReturnsSuccessResponse() throws Exception {

        String provinceName = "Test Province";
        AddNewProvinceRequest addNewProvinceRequest = new AddNewProvinceRequest();
        addNewProvinceRequest.setName(provinceName);
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        String authorizationHeader = "Bearer validToken";
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(jwtUtils.validateExpiredToken(anyString())).thenReturn(false);
        ProvinceEntity province = new ProvinceEntity();
        province.setName(provinceName);

        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "Test Image".getBytes());

        when(provinceService.findByName(anyString())).thenReturn(false);
        when(provinceService.addImage(any(), any())).thenReturn(province);


        ResponseEntity<Object> response = provinceController.addProvince(provinceName, result, httpServletRequest, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("content", province);
        assertEquals(expectedMap, response.getBody());
        verify(provinceService, times(1)).findByName(provinceName);
        verify(provinceService, times(1)).addImage(eq(file), any(ProvinceEntity.class));
    }

    @Test
    void testAddProvince_DuplicateProvinceName_ReturnsBadRequestResponse() throws Exception {
        
        String provinceName = "Test Province";
        AddNewProvinceRequest addNewProvinceRequest = new AddNewProvinceRequest();
        addNewProvinceRequest.setName(provinceName);
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        String authorizationHeader = "Bearer validToken";
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(jwtUtils.validateExpiredToken(anyString())).thenReturn(false);
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "Test Image".getBytes());

        when(provinceService.findByName(anyString())).thenReturn(true);

        
        ResponseEntity<Object> response = provinceController.addProvince(provinceName, result, httpServletRequest, file);

        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(provinceService, times(1)).findByName(provinceName);
        verify(provinceService, never()).addImage(eq(file), any(ProvinceEntity.class));
    }
}
