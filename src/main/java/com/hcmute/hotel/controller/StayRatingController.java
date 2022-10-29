package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.StayRatingEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.model.payload.request.StayRating.AddNewStayRatingRequest;
import com.hcmute.hotel.model.payload.request.StayRating.UpdateStayRatingRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.StayRatingService;
import com.hcmute.hotel.service.StayService;
import com.hcmute.hotel.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
@ComponentScan
@RestController
@RequestMapping("/api/stayrating")
@RequiredArgsConstructor
public class StayRatingController {
    @Autowired
    AuthenticateHandler authenticateHandler;
    private final UserService userService;
    private final StayRatingService stayRatingService;
    private final StayService stayService;
    static String E401="Unauthorized";
    static String E404="Not Found";
    static String E400="Bad Request";
    @PostMapping("")
    @ApiOperation("Add")
    public ResponseEntity<Object> addStayRating(@RequestBody @Valid AddNewStayRatingRequest addNewStayRatingRequest,BindingResult result, HttpServletRequest req) throws Exception {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null,result);
        }
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(addNewStayRatingRequest.getStayid());
            if (stay.getHost()==user || stay==null )
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND_OR_OWNER","Can't find Stay with id provided or you are stay owner"),HttpStatus.NOT_FOUND);
            }
            StayRatingEntity hotelRating= stayRatingService.saveStayRating(addNewStayRatingRequest,user);
            return new ResponseEntity<>(hotelRating, HttpStatus.OK);
        } catch (BadCredentialsException e){
            return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

        }
    }
    @PatchMapping("")
    @ApiOperation("Update")
    public ResponseEntity<Object> updateStayRating(@Valid @RequestBody UpdateStayRatingRequest updateStayRatingRequest, BindingResult errors, HttpServletRequest req) throws Exception {
        if (errors.hasErrors()) {
            throw new MethodArgumentNotValidException(null,errors);
        }
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayRatingEntity foundStayRating = stayRatingService.getStayRatingById(updateStayRatingRequest.getId());
            if(foundStayRating==null){
                return new ResponseEntity<>(new ErrorResponse(E404,"STAY_RATING_NOT_FOUND","Can't find StayRating with id provided"),HttpStatus.NOT_FOUND);
            }
           StayRatingEntity stayRating = stayRatingService.updateStayRating(updateStayRatingRequest);
            return new ResponseEntity<>(stayRating, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/{id}")
    @ApiOperation("Get by Id")
    public ResponseEntity<Object>getStayRatingById(@PathVariable("id")String id){
        StayRatingEntity stayRating= stayRatingService.getStayRatingById(id);
        if(stayRating==null)
        {
            return new ResponseEntity<>(new ErrorResponse(E404,"STAY_RATING_NOT_FOUND","Can't find StayRating with id provided"),HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(stayRating,HttpStatus.OK);
    }
    @GetMapping("")
    @ApiOperation("Get all")
    public ResponseEntity<Object> getAllStayRating() {
        List<StayRatingEntity> listStayRating = stayRatingService.getAllStayRating();
        Map<String,Object> map = new HashMap<>();
        map.put("content",listStayRating);
        return new ResponseEntity<>(map,HttpStatus.OK);
        //return new ResponseEntity<>(listStayRating.isEmpty() ? "{}" : map,HttpStatus.OK);
    }
    @GetMapping("/getByStay/{stayId}")
    @ApiOperation("Get by Stay")
    public ResponseEntity<Object> getStayRatingByStayId(@PathVariable("stayId")String id) {
        StayEntity foundStay=stayService.getStayById(id);
        if(foundStay==null){
            return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND","Can't find Stay with id provided"),HttpStatus.NOT_FOUND);
        }
        List<StayRatingEntity> listStayRating = stayRatingService.getStayRatingByStayId(id);
        Map<String,Object> map = new HashMap<>();
        map.put("content",listStayRating);
        return new ResponseEntity<>(map,HttpStatus.OK);
        //return new ResponseEntity<>(listStayRating.isEmpty() ? "{}" : map,HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @ApiOperation("Delete")
    public ResponseEntity<Object>deleteHotelRating(@PathVariable("id") String id,HttpServletRequest req){
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayRatingEntity stayRating =stayRatingService.getStayRatingById(id);
            if(stayRating==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"STAY_RATING_NOT_FOUND","Can't find StayRating with id provided"),HttpStatus.NOT_FOUND);
            }
            if(user!=stayRating.getUserRating())
            {
                return new ResponseEntity<>(new ErrorResponse(E400,"INVALID_STAY_RATING_OWNER","This is not your StayRating"),HttpStatus.BAD_REQUEST);
            }
            stayRatingService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

}
