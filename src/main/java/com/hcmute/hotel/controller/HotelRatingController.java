package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.MethodArgumentNotValidException;
import com.hcmute.hotel.model.entity.HotelRatingEntity;
import com.hcmute.hotel.model.entity.ReviewEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.model.payload.request.HotelRating.AddNewHotelRatingRequest;
import com.hcmute.hotel.model.payload.request.HotelRating.UpdateHotelRatingRequest;
import com.hcmute.hotel.model.payload.request.Review.AddNewReviewRequest;
import com.hcmute.hotel.model.payload.request.Review.UpdateReviewRequest;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.HotelRatingService;
import com.hcmute.hotel.service.ReviewService;
import com.hcmute.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
@ComponentScan
@RestController
@RequestMapping("/api/hotelrating")
@RequiredArgsConstructor
public class HotelRatingController {
    private final UserService userService;
    private final HotelRatingService hotelRatingService;
    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("/add")
    public ResponseEntity<SuccessResponse> addHotelRating(@RequestBody @Valid AddNewHotelRatingRequest addNewHotelRatingRequest , BindingResult errors, HttpServletRequest httpServletRequest) throws Exception {
        if (errors.hasErrors()) {
            throw new MethodArgumentNotValidException(errors);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        SuccessResponse response = new SuccessResponse();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setMessage("access token is expired");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            UserEntity user = userService.findById(UUID.fromString(jwtUtils.getUserNameFromJwtToken(accessToken)).toString());
            if (user == null) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("User not found");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            else {
                HotelRatingEntity hotelRating=hotelRatingService.saveHotelRating(addNewHotelRatingRequest,user);
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Add HotelRating successfully");
                response.setSuccess(true);
                response.getData().put("HotelRating",hotelRating);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("access token is missing");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    @PatchMapping("/update")
    public ResponseEntity<SuccessResponse> updateHotelRating(@Valid @RequestBody UpdateHotelRatingRequest updateHotelRatingRequest, BindingResult errors, HttpServletRequest httpServletRequest) throws Exception {
        if (errors.hasErrors()) {
            throw new MethodArgumentNotValidException(errors);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        SuccessResponse response = new SuccessResponse();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setMessage("access token is expired");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            UserEntity user = userService.findById(UUID.fromString(jwtUtils.getUserNameFromJwtToken(accessToken)).toString());
            if (user == null) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("User not found");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            HotelRatingEntity foundHotelRating =hotelRatingService.getHotelRatingById(updateHotelRatingRequest.getId());
            if(foundHotelRating==null){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Can't found HotelRating with id:"+updateHotelRatingRequest.getId());
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

           HotelRatingEntity hotelRating =hotelRatingService.updateHotelRating(updateHotelRatingRequest);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Update HotelRating successfully");
            response.setSuccess(true);
            response.getData().put("HotelRating",hotelRating);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("access token is missing");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse>getHotelRatingById(@PathVariable("id")String id){
        HotelRatingEntity hotelRating= hotelRatingService.getHotelRatingById(id);
        SuccessResponse response = new SuccessResponse();
        if(hotelRating==null)
        {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Can't found HotelRating with id:"+id);
            response.setSuccess(false);
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Get HotelRating successfully");
        response.setSuccess(true);
        response.getData().put("HotelRatingInfo",hotelRating);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @GetMapping("/getall")
    public ResponseEntity<SuccessResponse> getAllHotelRating() {
        List<HotelRatingEntity> listHotelRating = hotelRatingService.getAllHotelRating();
        SuccessResponse response = new SuccessResponse();
        if (listHotelRating.size() == 0) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("List HotelRating is empty");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Get all HotelRating successfully");
        response.setSuccess(true);
        response.getData().put("HotelRatingInfo", listHotelRating);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping("/delete")
    public ResponseEntity<SuccessResponse>deleteHotelRating(@RequestBody List<String> listHotelRatingId,HttpServletRequest httpServletRequest){
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        SuccessResponse response = new SuccessResponse();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setMessage("access token is expired");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            hotelRatingService.deleteById(listHotelRatingId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Delete HotelRating successfully");
            response.setSuccess(true);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("access token is missing");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

}
