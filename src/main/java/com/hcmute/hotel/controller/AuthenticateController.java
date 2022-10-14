package com.hcmute.hotel.controller;

import com.hcmute.hotel.mapping.UserMapping;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.model.payload.request.Authenticate.AddNewUserRequest;
import com.hcmute.hotel.model.payload.request.Authenticate.PhoneLoginRequest;
import com.hcmute.hotel.model.payload.request.Authenticate.RefreshTokenRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponseMap;
import com.hcmute.hotel.security.DTO.AppUserDetail;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@ComponentScan
@RestController
@RequestMapping("/api/authenticate")
@RequiredArgsConstructor
public class AuthenticateController {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final UserService userService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> registerAccount(@RequestBody @Valid AddNewUserRequest request) {
        UserEntity user= UserMapping.registerToEntity(request);
        SuccessResponse response=new SuccessResponse();
        if(userService.findByPhone(user.getPhone())!=null){
            response.setStatus(HttpStatus.CONFLICT.value());
            response.setMessage("Phone number has been used");
            response.setSuccess(false);
            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
        }

        try{
            user=userService.register(user,"USER");
            if (user==null)
            {
                response.setStatus(HttpStatus.CONFLICT.value());
                response.setMessage("add user false");
                response.setSuccess(false);
                return new ResponseEntity<>(response,HttpStatus.CONFLICT);
            }
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("add user successful");
            response.setSuccess(true);
            response.getData().put("phone",user.getPhone());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse> login(@RequestBody @Valid PhoneLoginRequest user, BindingResult errors, HttpServletResponse resp) {
        if(errors.hasErrors()) {
            return null;
        }
        if(userService.findByPhone(user.getPhone())==null) {
            return SendErrorValid("Phone", user.getPhone()+"not found","No account found" );
        }

        UserEntity loginUser=userService.findByPhone(user.getPhone());
        if(!passwordEncoder.matches(user.getPassword(),loginUser.getPassword())) {
            return SendErrorValid("password", user.getPassword()+"not found","Wrong password" );
        }
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUser.getId().toString(),user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUserDetail userDetail= (AppUserDetail) authentication.getPrincipal();

        String accessToken = jwtUtils.generateJwtToken(userDetail);
        String refreshToken=jwtUtils.generateRefreshJwtToken(userDetail);

        System.out.println(jwtUtils.getUserNameFromJwtToken(accessToken));
        SuccessResponse response = new SuccessResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Login successful");
        response.setSuccess(true);

        Cookie cookieAccessToken = new Cookie("accessToken", accessToken);
        Cookie cookieRefreshToken = new Cookie("refreshToken", refreshToken);

        resp.setHeader("Set-Cookie", "test=value; Path=/");
        resp.addCookie(cookieAccessToken);
        resp.addCookie(cookieRefreshToken);

        response.getData().put("accessToken",accessToken);
        response.getData().put("refreshToken",refreshToken);
        response.getData().put("user",loginUser);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<SuccessResponse> refreshToken(@RequestBody RefreshTokenRequest refreshToken,
                                                        HttpServletRequest request, HttpServletResponse resp){
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if(!jwtUtils.validateExpiredToken(accessToken)){
                throw new BadCredentialsException("access token is not expired");
            }

            if(jwtUtils.validateExpiredToken(refreshToken.getRefreshToken())){
                throw new BadCredentialsException("refresh token is expired");
            }

            if(refreshToken == null){
                throw new BadCredentialsException("refresh token is missing");
            }

            if(!jwtUtils.getUserNameFromJwtToken(refreshToken
                    .getRefreshToken()).equals(jwtUtils.getUserNameFromJwtToken(refreshToken.getRefreshToken()))){
                throw new BadCredentialsException("two token are not a pair");
            }


            AppUserDetail userDetails =  AppUserDetail.build(userService
                    .findById(jwtUtils.getUserNameFromJwtToken(refreshToken.getRefreshToken())));

            accessToken = jwtUtils.generateJwtToken(userDetails);

            SuccessResponse response = new SuccessResponse();
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Login successful");
            response.setSuccess(true);

            Cookie cookieAccessToken = new Cookie("accessToken", accessToken);

            resp.setHeader("Set-Cookie", "test=value; Path=/");
            resp.addCookie(cookieAccessToken);

            response.getData().put("accessToken",accessToken);
            response.getData().put("refreshToken",refreshToken.getRefreshToken());


            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else
        {
            throw new BadCredentialsException("access token is missing");
        }
    }
    private ResponseEntity SendErrorValid(String field, String message,String title){
        ErrorResponseMap errorResponseMap = new ErrorResponseMap();
        Map<String,String> temp =new HashMap<>();
        errorResponseMap.setMessage(title);
        temp.put(field,message);
        errorResponseMap.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseMap.setDetails(temp);
        return ResponseEntity
                .badRequest()
                .body(errorResponseMap);
    }
}
