package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.MethodArgumentNotValidException;
import com.hcmute.hotel.model.entity.ReviewEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.model.payload.request.Authenticate.AddNewUserRequest;
import com.hcmute.hotel.model.payload.request.Review.AddNewReviewRequest;
import com.hcmute.hotel.model.payload.request.Review.UpdateReviewRequest;
import com.hcmute.hotel.security.JWT.JwtUtils;
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
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final UserService userService;
    private final ReviewService reviewService;
    @Autowired
    JwtUtils jwtUtils;
    @PostMapping("/add")
    public ResponseEntity<SuccessResponse>addReview(@RequestBody @Valid AddNewReviewRequest addNewReviewRequest , BindingResult errors, HttpServletRequest httpServletRequest) throws Exception {
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
                ReviewEntity review =reviewService.saveReview(addNewReviewRequest,user);
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Add Review successfully");
                response.setSuccess(true);
                response.getData().put("Review",review);
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
    public ResponseEntity<SuccessResponse> updateLecturer(@Valid @RequestBody UpdateReviewRequest updateReviewRequest, BindingResult errors, HttpServletRequest httpServletRequest) throws Exception {
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
            ReviewEntity foundReview =reviewService.getReviewById(updateReviewRequest.getId());
            if(foundReview==null){
                response.setStatus(HttpStatus.NOT_FOUND.value());
                response.setMessage("Can't found Review with id:"+updateReviewRequest.getId());
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            ReviewEntity review =reviewService.updateReview(updateReviewRequest);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Update Review successfully");
            response.setSuccess(true);
            response.getData().put("Review",review);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("access token is missing");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse>getReviewById(@PathVariable("id")String id){
        ReviewEntity review= reviewService.getReviewById(id);
        SuccessResponse response = new SuccessResponse();
        if(review==null)
        {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Can't found Review with id:"+id);
            response.setSuccess(false);
            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
        }
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Get Review successfully");
        response.setSuccess(true);
        response.getData().put("ReviewInfo",review);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @GetMapping("/getall")
    public ResponseEntity<SuccessResponse> getAllReview() {
        List<ReviewEntity> listReview = reviewService.getAllReview();
        SuccessResponse response = new SuccessResponse();
        if (listReview.size() == 0) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("List Review is empty");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Get all Review successfully");
        response.setSuccess(true);
        response.getData().put("ListReviewInfo", listReview);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @DeleteMapping("/delete")
    public ResponseEntity<SuccessResponse>deleteReview(@RequestBody List<String> listReviewId,HttpServletRequest httpServletRequest){
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
            reviewService.deleteById(listReviewId);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Delete Review successfully");
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
