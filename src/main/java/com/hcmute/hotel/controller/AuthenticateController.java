package com.hcmute.hotel.controller;

import com.hcmute.hotel.mapping.UserMapping;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.model.payload.request.Authenticate.AddNewCustomerRequest;
import com.hcmute.hotel.model.payload.request.Authenticate.AddNewOwnerRequest;
import com.hcmute.hotel.model.payload.request.Authenticate.EmailLoginRequest;
import com.hcmute.hotel.model.payload.request.Authenticate.RefreshTokenRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.model.payload.response.ErrorResponseMap;
import com.hcmute.hotel.security.DTO.AppUserDetail;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.EmailService;
import com.hcmute.hotel.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Email;
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
    private final EmailService emailService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    static String E404="Not Found";
    static String E400="Bad Request";
    @PostMapping("create/customer")
    @ApiOperation("Create Account Customer")
    public ResponseEntity<Object> registerAccountCustomer(@RequestBody @Valid AddNewCustomerRequest request,HttpServletRequest req)  {
        UserEntity user= UserMapping.registerCustomerToEntity(request);
        if(userService.findByEmail(user.getEmail())!=null){
            return new ResponseEntity<>(new ErrorResponse(E400,"EMAIL_EXISTS","Email has been used"),HttpStatus.BAD_REQUEST);
        }

        try{
            user.setEnabled(false);
            user.setVerificationCode(RandomString.make(64));
            user=userService.register(user,"USER");
            if (user==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"USER_NOT_CREATED","Add user false"),HttpStatus.NOT_FOUND);
            }
            emailService.sendConfirmCustomerEmail(user,req.getHeader("origin"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/owner")
    @ApiOperation("Create Account Owner")
    public ResponseEntity<Object> registerAccountOwner(@RequestBody @Valid AddNewOwnerRequest request,@RequestParam String verificationCode)  {
        UserEntity user = userService.findByVerificationCode(verificationCode);
        if (user==null)
        {
            return new ResponseEntity<>(new ErrorResponse( E404,"USER_HAVE_ALREADY_SIGN","User have been fill the form"),HttpStatus.NOT_FOUND);

        }
        if(userService.findByPhone(request.getPhone())!=null){
            return new ResponseEntity<>(new ErrorResponse(E400,"PHONE_NUMBER_EXISTS","Phone number has been used"),HttpStatus.BAD_REQUEST);
        }
        user= UserMapping.registerOwnerToEntity(user,request);
        try{
            user.setVerificationCode(null);
            user=userService.register(user,"OWNER");
            if (user==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"USER_NOT_CREATED","Add user false"),HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(user.getPhone(), HttpStatus.OK);
    }
    @PostMapping("/sendOwnerRegister")
    @ApiOperation("Send register email")
    public ResponseEntity<Object> SendOwnerRegisterEmail(@RequestParam @Email String email,HttpServletRequest req)
    {
        if(userService.findByEmail(email)!=null){
            return new ResponseEntity<>(new ErrorResponse(E400,"EMAIL_ALREADY_EXISTS","Email has been used"),HttpStatus.BAD_REQUEST);
        }
        try {
            UserEntity user = new UserEntity();
            user.setId(UUID.randomUUID().toString());
            user.setEmail(email);
            user.setVerificationCode(RandomString.make(64));
            user.setEnabled(false);
            emailService.sendOwnerConfirmEmail(user, req.getHeader("origin"));
            userService.save(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid EmailLoginRequest user, BindingResult errors, HttpServletResponse resp) {
        if(errors.hasErrors()) {
            return null;
        }
        if(userService.findByEmail(user.getEmail())==null) {
            return new ResponseEntity<>(new ErrorResponse( E404,"USER_NOT_FOUND","User not found"),HttpStatus.NOT_FOUND);
        }

        UserEntity loginUser=userService.findByEmail(user.getEmail());
        if(!passwordEncoder.matches(user.getPassword(),loginUser.getPassword())) {
            return new ResponseEntity<>(new ErrorResponse(E404,"INVALID_PASSWORD","Wrong Password"),HttpStatus.NOT_FOUND);
        }
        if (!loginUser.isEnabled())
        {
            return new ResponseEntity<>(new ErrorResponse(E400,"ACCOUNT_NOT_VERIFY","Account not verify"),HttpStatus.BAD_REQUEST);

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
    @GetMapping("/verify/{code}")
    public ResponseEntity<Object> verifyRegister(@PathVariable("code") String code)
    {
        UserEntity user = userService.findByVerificationCode(code);
        if (user==null || user.isEnabled())
        {
            return new ResponseEntity<>(new ErrorResponse(E400,"ACCOUNT_HAVE_BEEN_ACTIVE","Account has been active"),HttpStatus.BAD_REQUEST);
        }
        else
        {
            user.setEnabled(true);
            user.setVerificationCode(null);
            userService.save(user);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
