package com.hcmute.hotel.controller;

import com.hcmute.hotel.common.AppUserRole;
import com.hcmute.hotel.common.GenderEnum;
import com.hcmute.hotel.common.UserStatus;
import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.model.entity.RoleEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.User.AddUserInfoRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.model.payload.response.PagingResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.EmailService;
import com.hcmute.hotel.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.hcmute.hotel.controller.StayController.E400;
import static com.hcmute.hotel.controller.StayController.E401;


@ComponentScan
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticateHandler authenticateHandler;
    private final EmailService emailService;
    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/search")
    @ApiOperation("Search by Criteria")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> search(@RequestParam(defaultValue = "") String keyword, @RequestParam(name = "status", required = false) UserStatus userstatus, @RequestParam(name = "role", required = false) AppUserRole userRole, @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "5") int size) {
        List<UserEntity> userEntities = userService.search(keyword, userstatus, userRole, page, size);
        int totalElements = userEntities.size();
        int totalPage = totalElements % size == 0 ? totalElements / size : totalElements / size + 1;
        PagingResponse pagingResponse = new PagingResponse();
        Map<String, Object> map = new HashMap<>();
        List<Object> Result = Arrays.asList(userEntities.toArray());
        pagingResponse.setTotalPages(totalPage);
        pagingResponse.setEmpty(userEntities.size() == 0);
        pagingResponse.setFirst(page == 0);
        pagingResponse.setLast(page == totalPage - 1);
        pagingResponse.getPageable().put("pageNumber", page);
        pagingResponse.getPageable().put("pageSize", size);
        pagingResponse.setSize(size);
        pagingResponse.setNumberOfElements(userEntities.size());
        pagingResponse.setTotalElements(totalElements);
        pagingResponse.setContent(Result);
        return new ResponseEntity<>(pagingResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/image", consumes = {"multipart/form-data"})
    @ApiOperation("Upload User Image")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> addUserImage(@RequestPart MultipartFile file, HttpServletRequest req) {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            user = userService.addUserImage(file, user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        } catch (FileNotImageException fileNotImageException) {
            return new ResponseEntity<>(new ErrorResponse("Unsupported Media Type", "FILE_NOT_IMAGE", fileNotImageException.getMessage()), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } catch (RuntimeException runtimeException) {
            return new ResponseEntity<>(new ErrorResponse(E400, "FAIL_TO_UPLOAD", runtimeException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/userInfo", consumes = {"multipart/form-data"})
    @ApiOperation("Create User Info")
    public ResponseEntity<Object> addUserInfo(@Valid AddUserInfoRequest addUserInfoRequest, @RequestParam String gender, @RequestPart(required = false) MultipartFile file, HttpServletRequest req) {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            user.setFullName(addUserInfoRequest.getFullName());
            user.setPhone(addUserInfoRequest.getPhone());
            user.setGender(gender);
            if (file!=null) {
                user = userService.addUserImage(file, user);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        } catch (FileNotImageException fileNotImageException) {
            return new ResponseEntity<>(new ErrorResponse("Unsupported Media Type", "FILE_NOT_IMAGE", fileNotImageException.getMessage()), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } catch (RuntimeException runtimeException) {
            return new ResponseEntity<>(new ErrorResponse(E400, "FAIL_TO_UPLOAD", runtimeException.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/userInfo")
    @ApiOperation("Get User Info")
    public ResponseEntity<Object> getUserInfo(HttpServletRequest req) {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/listUserInfo")
    @ApiOperation("Get List User")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> getUserList(@RequestParam(required = false) String searchKey) {
        List<UserEntity> list = userService.searchKey(searchKey);
        List<UserEntity> filterList = new ArrayList<>();
        for (UserEntity user : list)
        {
            if (user.getRoles()!=null && user.getRoles().size()>0)
            {
                boolean flag=false;
                for (RoleEntity role: user.getRoles())
                {
                    if (role.getName().toString() == "ROLE_ADMIN")
                    {
                        flag=true;
                    }
                }
                if (!flag)
                {
                    filterList.add(user);
                }
            }
        }
        return new ResponseEntity<>(filterList,HttpStatus.OK);
    }

    @PostMapping("/banned/{userId}")
    @ApiOperation("Banned an user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ResponseEntity<Object> bannedUser(@PathVariable("userId") String userId, HttpServletRequest request, @RequestParam String reason)
    {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(request);
            UserEntity bannedUser = userService.findById(userId);
            if (bannedUser == null )
            {
                return new ResponseEntity<>(new ErrorResponse(E400, "USER_NOT_FOUND", "Can't find user with id" + userId), HttpStatus.BAD_REQUEST);
            }
            bannedUser.setStatus(false);
            userService.save(bannedUser);
            emailService.sendBlockedAccountEmail(bannedUser,reason);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/unbanned/{userId}")
    @ApiOperation("Unbanned an user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public ResponseEntity<Object> unbannedUser(@PathVariable("userId") String userId, HttpServletRequest request)
    {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(request);
            UserEntity unbannedUser = userService.findById(userId);
            if (unbannedUser == null )
            {
                return new ResponseEntity<>(new ErrorResponse(E400, "USER_NOT_FOUND", "Can't find user with id" + userId), HttpStatus.BAD_REQUEST);
            }
            unbannedUser.setStatus(true);
            userService.save(unbannedUser);
            emailService.sendUnblockedAccountEmail(unbannedUser);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


}

