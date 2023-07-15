package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.User.AddUserInfoRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.EmailService;
import com.hcmute.hotel.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserControllerTest {
    @InjectMocks

    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticateHandler authenticateHandler;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddUserInfo_ValidRequestWithImage_ReturnsUserEntity() throws FileNotImageException {
        
        UserEntity authenticatedUser = new UserEntity();
        authenticatedUser.setId(String.valueOf(1L));
        authenticatedUser.setEmail("testuser@Gmail.com");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        AddUserInfoRequest addUserInfoRequest = new AddUserInfoRequest();
        addUserInfoRequest.setFullName("John Doe");
        addUserInfoRequest.setPhone("123456789");

        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "Test Image".getBytes());

        when(authenticateHandler.authenticateUser(request)).thenReturn(authenticatedUser);
        when(userService.addUserImage(file, authenticatedUser)).thenReturn(authenticatedUser);

        
        ResponseEntity<Object> response = userController.addUserInfo(addUserInfoRequest, "Male", file, request);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticatedUser, response.getBody());
    }

    @Test
    void testAddUserInfo_ValidRequestWithoutImage_ReturnsUserEntity() throws FileNotImageException {
        
        UserEntity authenticatedUser = new UserEntity();
        authenticatedUser.setId(String.valueOf(1L));
        authenticatedUser.setEmail("testuser@gmail.com");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        AddUserInfoRequest addUserInfoRequest = new AddUserInfoRequest();
        addUserInfoRequest.setFullName("John Doe");
        addUserInfoRequest.setPhone("123456789");

        when(authenticateHandler.authenticateUser(request)).thenReturn(authenticatedUser);

        
        ResponseEntity<Object> response = userController.addUserInfo(addUserInfoRequest, "Male", null, request);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authenticatedUser, response.getBody());
    }

    @Test
    void testAddUserInfo_UnauthorizedRequest_ReturnsErrorResponse() throws FileNotImageException {
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");

        when(authenticateHandler.authenticateUser(request)).thenThrow(new BadCredentialsException("Unauthorized"));

        AddUserInfoRequest addUserInfoRequest = new AddUserInfoRequest();
        addUserInfoRequest.setFullName("John Doe");
        addUserInfoRequest.setPhone("123456789");

        
        ResponseEntity<Object> response = userController.addUserInfo(addUserInfoRequest, "Male", null, request);

        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ErrorResponse expectedErrorResponse = new ErrorResponse("Unauthorized", "UNAUTHORIZED", "Unauthorized, please login again");
        assertEquals(expectedErrorResponse, response.getBody());
    }

}
