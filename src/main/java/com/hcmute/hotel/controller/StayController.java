package com.hcmute.hotel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.mapping.StayMapping;
import com.hcmute.hotel.model.entity.AmenitiesEntity;
import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.model.payload.request.Stay.AddNewStayRequest;
import com.hcmute.hotel.model.payload.request.Stay.UpdateStayRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.AmenitiesService;
import com.hcmute.hotel.service.ProvinceService;
import com.hcmute.hotel.service.StayService;
import com.hcmute.hotel.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.aspectj.util.Reflection;
import org.hibernate.query.criteria.internal.path.MapKeyHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ComponentScan
@RestController
@RequestMapping("/api/stay")
@RequiredArgsConstructor
public class StayController {
    @Autowired
    AuthenticateHandler authenticateHandler;
    private final StayService stayService;
    private final AmenitiesService amenitiesService;
    private final UserService userService;
    private final ProvinceService provinceService;
    private final ObjectMapper objectMapper;
    static String E401="Unauthorized";
    static String E404="Not Found";
    static String E400="Bad Request";
    @PostMapping("")
    @ApiOperation("Add")
    public ResponseEntity<Object> addStay(HttpServletRequest req, @Valid @RequestBody AddNewStayRequest addNewStayRequest)
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = StayMapping.addReqToEntity(addNewStayRequest, user);
            stay.setCreatedAt(LocalDateTime.now());
            ProvinceEntity province = provinceService.getProvinceById(addNewStayRequest.getProvinceId());
            if (province==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"PROVINCE_NOT_FOUND","Can't find Province with id provided"),HttpStatus.NOT_FOUND);
            }
            stay.setProvince(province);
            stay = stayService.saveStay(stay);
            return new ResponseEntity<>(stay, HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
                return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

    }
    }
    @GetMapping("")
    @ApiOperation("Get All")
    public ResponseEntity<Object> getAllStay()
    {
        List<StayEntity> listStay = stayService.getAllStay();
        Map<String,Object> map = new HashMap<>();
        map.put("content",listStay);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
//    @PatchMapping(path = "/{id}")
//    @ApiOperation("Update")
//    public ResponseEntity<Object> patchStay(@PathVariable("id") String id,HttpServletRequest req,@RequestBody Map<Object,Object> maps)
//    {
//        StayEntity stay=stayService.getStayById(id);
//        UserEntity user;
//        try
//        {
//            user = authenticateHandler.authenticateUser(req);
//            if (stay==null)
//            {
//                return new ResponseEntity<>(new ErrorResponse("Can't find Stay with id" + id),HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>(stay, HttpStatus.OK);
//        } catch (BadCredentialsException e) {
//                    return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

//        }
//
//    }
    @GetMapping("/{id}")
    @ApiOperation("Get By id")
    public ResponseEntity<Object> getStayByUid(@PathVariable("id") String id)
    {
        StayEntity stay=stayService.getStayById(id);
        if (stay==null)
        {

            return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND","Can't find Stay with id provided"),HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(stay,HttpStatus.OK);
        }
    }
    @PatchMapping("/{id}")
    @ApiOperation("Update")
    public ResponseEntity<Object> updateStayInfo(@PathVariable("id") String id,@Valid @RequestBody UpdateStayRequest updateStayRequest,HttpServletRequest req)
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(id);

            if (stay==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND","Can't find Stay with id provided"),HttpStatus.NOT_FOUND);
            }
            else if (user!=stay.getHost())
            {
                return new ResponseEntity<>(new ErrorResponse(E400,"INVALID_STAY_OWNER","You are not stay owner"),HttpStatus.BAD_REQUEST);
            }
            else
            {
                stay=StayMapping.updateReqToEntity(updateStayRequest,stay);
                stay.setLatestUpdateAt(LocalDateTime.now());
                stay=stayService.saveStay(stay);
                return new ResponseEntity<>(stay,HttpStatus.OK);
            }
        } catch (BadCredentialsException e) {
                    return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

        }
    }
    @DeleteMapping("/{id}")
    @ApiOperation("Delete")
    public ResponseEntity<Object> deleteStay(@PathVariable("id") String id,HttpServletRequest req) {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(id);
            if (stay == null) {
                return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND","Can't find Stay with id provided"),HttpStatus.NOT_FOUND);
            }
            if (user != stay.getHost()) {
                return new ResponseEntity<>(new ErrorResponse(E400,"INVALID_STAY_OWNER","You are not stay owner"),HttpStatus.BAD_REQUEST);
            } else {
                for (UserEntity tempUser : stay.getUserLiked())
                {
                    tempUser.getStayLiked().remove(stay);
                }
                ProvinceEntity province = provinceService.getProvinceById(stay.getProvince().getId());
                if (province!=null)
                province.getStay().remove(province);
                stayService.deleteStay(id);
                return new ResponseEntity<>( HttpStatus.OK);
            }
        } catch (BadCredentialsException e) {
                    return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

        }
    }
    @PostMapping("/StayAmenities/{id}")
    @ApiOperation("Add Stay Amenities")
    public ResponseEntity<Object> addStayAmenities(@PathVariable("id") String id,HttpServletRequest req,@RequestParam String amenitiesId)
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(id);
            if (stay==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND","Can't find Stay with id provided"),HttpStatus.NOT_FOUND);
            }
            if (user != stay.getHost()) {
                return new ResponseEntity<>(new ErrorResponse(E400,"INVALID_STAY_OWNER","You are not stay owner"),HttpStatus.BAD_REQUEST);
            }
            AmenitiesEntity amenities = amenitiesService.getAmenitiesById(amenitiesId);
            if (amenities==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"AMENITIES_NOT_FOUND","Amenities not found"),HttpStatus.NOT_FOUND);
            }
            if (stay.getAmenities().add(amenities)==false)
            {
                return new ResponseEntity<>(new ErrorResponse(E400,"AMENITIES_EXISTED","This stay have that amenities"), HttpStatus.BAD_REQUEST);
            }
            else
            {
                stayService.saveStay(stay);
                return new ResponseEntity<>(stay, HttpStatus.OK);
            }
        }
        catch (BadCredentialsException e) {
                    return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

        }
    }
    @GetMapping("/getUser")
    @ApiOperation("Get User Owned Stay")
    public ResponseEntity<Object> getUserStay(HttpServletRequest req)
    {
        UserEntity user;
        SuccessResponse response = new SuccessResponse();
        try {
            user = authenticateHandler.authenticateUser(req);
            List<StayEntity> listStay = stayService.getStayByUser(user);
            Map<String,Object> map = new HashMap<>();
            map.put("content",listStay);
            return new ResponseEntity<>(map,HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
                    return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

        }
    }
    @PostMapping("LikeList/{id}")
    @ApiOperation("Add to liked list")
    public ResponseEntity<Object> addStayToLikeList(@PathVariable("id") String id,HttpServletRequest req)
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(id);
            if (stay==null || stay.getHost()==user)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND_OR_OWNER","Can't find Stay with id provided or you are stay owner"),HttpStatus.NOT_FOUND);
            }
            if (!user.getStayLiked().add(stay))
            {
                user.getStayLiked().remove(stay);
                userService.save(user);
                Map<String,Object> map = new HashMap<>();
                map.put("content",user.getStayLiked());
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            else
            {
                userService.save(user);
                Map<String,Object> map = new HashMap<>();
                map.put("content",user.getStayLiked());
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
        }
        catch (BadCredentialsException e) {
                    return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

        }
    }
    @GetMapping("/LikeList")
    @ApiOperation("Get user liked list")
    public ResponseEntity<Object> getUserLikeList(HttpServletRequest req)
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            Map<String,Object> map = new HashMap<>();
            map.put("content",user.getStayLiked());
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
                    return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

        }
    }


}
