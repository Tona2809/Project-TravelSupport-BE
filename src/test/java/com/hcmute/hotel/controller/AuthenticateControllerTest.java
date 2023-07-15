package com.hcmute.hotel.controller;

import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.Authenticate.EmailLoginRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.security.DTO.AppUserDetail;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.EmailService;
import com.hcmute.hotel.service.UserService;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticateControllerTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticateController authenticateController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        authenticateController.setJwtUtils(jwtUtils);
        authenticateController.setAuthenticationManager(authenticationManager);
    }

    @Test
    void login_Success() {
        EmailLoginRequest emailLoginReq = mock(EmailLoginRequest.class);
        when(emailLoginReq.getEmail()).thenReturn("bang@gmail.com");
        when(emailLoginReq.getPassword()).thenReturn("12345678");
        UserEntity user = mock(UserEntity.class);
        when(user.getPassword()).thenReturn("$2a$10$v4rKp9uOJ8lllO9r/qJyWO9byNkwQoyL06eWZgoPAmfrdbPIoCEx6");
        when(user.isEnabled()).thenReturn(true);
        when(user.isStatus()).thenReturn(true);
        when(user.getId()).thenReturn("userId");
        Authentication authentication = mock(Authentication.class);
        AppUserDetail appUserDetail = mock(AppUserDetail.class);
        when(authentication.getPrincipal()).thenReturn(appUserDetail);
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        BindingResult errors = mock(BindingResult.class);
        String accessToken = "some dummy token";
        String refreshToken = "some dummy refresh token";


        when(errors.hasErrors()).thenReturn(false);
        when(userService.findByEmail(emailLoginReq.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(any(CharSequence.class), any(String.class))).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(appUserDetail)).thenReturn(accessToken);
        when(jwtUtils.generateRefreshJwtToken(appUserDetail)).thenReturn(refreshToken);

        ResponseEntity<Object> response = authenticateController.login(emailLoginReq, errors, servletResponse);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void login_InvalidCredentials_InvalidPasswordError() {
        // Mock request parameters
        EmailLoginRequest emailLoginReq = new EmailLoginRequest();
        emailLoginReq.setEmail("bang@gmail.com");
        emailLoginReq.setPassword("1234567");

        // Mock user entity
        UserEntity user = new UserEntity();
        user.setPassword("hashed_password");

        // Mock BindingResult
        BindingResult errors = mock(BindingResult.class);
        when(errors.hasErrors()).thenReturn(false);

        // Mock userService
        when(userService.findByEmail(emailLoginReq.getEmail())).thenReturn(user);

        // Mock password encoder
        when(passwordEncoder.matches(emailLoginReq.getPassword(), user.getPassword())).thenReturn(false);

        // Perform the test
        ResponseEntity<Object> response = authenticateController.login(emailLoginReq, errors, null);

        // Verify the result
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("INVALID_PASSWORD", errorResponse.getMessage());
        assertEquals("Wrong Password", errorResponse.getMessageDescription());
    }

    @Test
    void login_UserNotFound_UserNotFoundError() {
        // Mock request parameters
        EmailLoginRequest emailLoginReq = new EmailLoginRequest();
        emailLoginReq.setEmail("bang@gmail.com");

        // Mock BindingResult
        BindingResult errors = mock(BindingResult.class);
        when(errors.hasErrors()).thenReturn(false);

        // Mock userService
        when(userService.findByEmail(emailLoginReq.getEmail())).thenReturn(null);

        // Perform the test
        ResponseEntity<Object> response = authenticateController.login(emailLoginReq, errors, null);

        // Verify the result
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("USER_NOT_FOUND", errorResponse.getMessage());
        assertEquals("User not found", errorResponse.getMessageDescription());
    }
}
