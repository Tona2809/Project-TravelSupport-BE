package com.hcmute.hotel.controller;

import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.StayRatingEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.model.payload.request.StayRating.AddNewStayRatingRequest;
import com.hcmute.hotel.model.payload.request.StayRating.UpdateStayRatingRequest;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.StayRatingService;
import com.hcmute.hotel.service.StayService;
import com.hcmute.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final UserService userService;
    private final StayRatingService stayRatingService;
    private final StayService stayService;
    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("")
    public ResponseEntity<Object> addStayRating(@RequestBody @Valid AddNewStayRatingRequest addNewStayRatingRequest, BindingResult errors, HttpServletRequest httpServletRequest) throws Exception {
        if (errors.hasErrors()) {
            throw new MethodArgumentNotValidException(null,errors);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                return new ResponseEntity<>("access token is expired", HttpStatus.UNAUTHORIZED);
            }
            UserEntity user = userService.findById(UUID.fromString(jwtUtils.getUserNameFromJwtToken(accessToken)).toString());
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            else {
                StayEntity stay = stayService.getStayById(addNewStayRatingRequest.getStayid());
                if (stay.getHost()==user)
                {
                    return new ResponseEntity<>("You can't vote for your own stay", HttpStatus.FOUND);
                }
                StayRatingEntity hotelRating= stayRatingService.saveStayRating(addNewStayRatingRequest,user);
                return new ResponseEntity<>(hotelRating, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("access token is missing", HttpStatus.UNAUTHORIZED);
        }
    }
    @PatchMapping("")
    public ResponseEntity<Object> updateStayRating(@Valid @RequestBody UpdateStayRatingRequest updateStayRatingRequest, BindingResult errors, HttpServletRequest httpServletRequest) throws Exception {
        if (errors.hasErrors()) {
            throw new MethodArgumentNotValidException(null,errors);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                return new ResponseEntity<>("access token is expired", HttpStatus.UNAUTHORIZED);
            }
            UserEntity user = userService.findById(UUID.fromString(jwtUtils.getUserNameFromJwtToken(accessToken)).toString());
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            StayRatingEntity foundStayRating = stayRatingService.getStayRatingById(updateStayRatingRequest.getId());
            if(foundStayRating==null){
                return new ResponseEntity<>("Can't found StayRating with id", HttpStatus.NOT_FOUND);
            }

           StayRatingEntity stayRating = stayRatingService.updateStayRating(updateStayRatingRequest);
            return new ResponseEntity<>(stayRating, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("access token is missing", HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object>getStayRatingById(@PathVariable("id")String id){
        StayRatingEntity stayRating= stayRatingService.getStayRatingById(id);
        if(stayRating==null)
        {
            return new ResponseEntity<>("{}",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(stayRating,HttpStatus.OK);
    }
    @GetMapping("")
    public ResponseEntity<Object> getAllStayRating() {
        List<StayRatingEntity> listStayRating = stayRatingService.getAllStayRating();
        Map<String,Object> map = new HashMap<>();
        map.put("content",listStayRating);
        return new ResponseEntity<>(listStayRating.isEmpty() ? "{}" : map,HttpStatus.OK);
    }
    @GetMapping("/get_by_stay/{stay_id}")
    public ResponseEntity<Object> getStayRatingByStayId(@PathVariable("stay_id")String id) {
        StayEntity foundStay=stayService.getStayById(id);
        if(foundStay==null){
            return new ResponseEntity<>("Id not correct",HttpStatus.NOT_FOUND);
        }
        List<StayRatingEntity> listStayRating = stayRatingService.getStayRatingByStayId(id);
        Map<String,Object> map = new HashMap<>();
        map.put("content",listStayRating);
        return new ResponseEntity<>(listStayRating.isEmpty() ? "{}" : map,HttpStatus.OK);
    }
    @DeleteMapping("")
    public ResponseEntity<Object>deleteHotelRating(@RequestBody List<String> listHotelRatingId,HttpServletRequest httpServletRequest){
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                return new ResponseEntity<>("access token is expired", HttpStatus.UNAUTHORIZED);
            }
            stayRatingService.deleteById(listHotelRatingId);
            return new ResponseEntity<>("{}",HttpStatus.OK);
        }else {
            return new ResponseEntity<>("access token is missing", HttpStatus.UNAUTHORIZED);
        }
    }

}
