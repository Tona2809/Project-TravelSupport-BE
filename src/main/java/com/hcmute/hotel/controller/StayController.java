package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.mapping.StayMapping;
import com.hcmute.hotel.model.entity.AmenitiesEntity;
import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.model.payload.request.Stay.AddNewStayRequest;
import com.hcmute.hotel.model.payload.request.Stay.UpdateStayRequest;
import com.hcmute.hotel.service.AmenitiesService;
import com.hcmute.hotel.service.ProvinceService;
import com.hcmute.hotel.service.StayService;
import com.hcmute.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

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
    @PostMapping("/addStay")
    public ResponseEntity<SuccessResponse> addStay(HttpServletRequest req, @Valid @RequestBody AddNewStayRequest addNewStayRequest)
    {
        SuccessResponse response = new SuccessResponse();
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = StayMapping.addReqToEntity(addNewStayRequest, user);
            stay.setCreatedAt(LocalDateTime.now());
            ProvinceEntity province = provinceService.getProvinceById(addNewStayRequest.getProvinceId());
            if (province==null)
            {
                response.setMessage("Can't find Province with id" + addNewStayRequest.getProvinceId());
                response.setStatus(HttpStatus.FOUND.value());
                response.setSuccess(false);
                return new ResponseEntity<>(response,HttpStatus.FOUND);
            }
            stay.setProvince(province);
            stay = stayService.saveStay(stay);
            response.setStatus(HttpStatus.OK.value());
            response.setSuccess(true);
            response.getData().put("stayInfo", stay);
            response.setMessage("save Stay success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Unauthorized, please login again");
        response.setSuccess(false);
        return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
    }
    }
    @GetMapping("/getAllStay")
    public ResponseEntity<SuccessResponse> getAllStay()
    {
        List<StayEntity> listStay = stayService.getAllStay();
        SuccessResponse response = new SuccessResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setSuccess(true);
        response.getData().put("listStay",listStay);
        response.setMessage(listStay.isEmpty() ? "List stay is empty" : "Get list stay success");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @GetMapping("/getAllStayByid/{id}")
    public ResponseEntity<SuccessResponse> getAllStayByUid(@PathVariable("id") String id)
    {
        StayEntity stay=stayService.getStayById(id);
        SuccessResponse response = new SuccessResponse();
        if (stay==null)
        {
            response.setMessage("Can't find Stay with id" + id);
            response.setStatus(HttpStatus.FOUND.value());
            response.setSuccess(false);
            return new ResponseEntity<>(response,HttpStatus.FOUND);
        }
        else {
            response.setStatus(HttpStatus.OK.value());
            response.setSuccess(true);
            response.getData().put("stayInfo",stay);
            response.setMessage("Get Stay success");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }
    @PatchMapping("/updateStayInfo/{id}")
    public ResponseEntity<SuccessResponse> updateStayInfo(@PathVariable("id") String id,@Valid @RequestBody UpdateStayRequest updateStayRequest,HttpServletRequest req)
    {
        UserEntity user;
        SuccessResponse response= new SuccessResponse();
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(id);
            if (stay==null)
            {
                response.setSuccess(false);
                response.setStatus(HttpStatus.FOUND.value());
                response.setMessage("Can't find Stay with id:"+id);
                return new ResponseEntity<>(response,HttpStatus.FOUND);
            }
            else if (user!=stay.getHost())
            {
                response.setSuccess(false);
                response.setStatus(HttpStatus.FOUND.value());
                response.setMessage("You are not stay owner");
                return new ResponseEntity<>(response,HttpStatus.FOUND);
            }
            else
            {
                stay=StayMapping.updateReqToEntity(updateStayRequest,stay);
                stay.setLatestUpdateAt(LocalDateTime.now());
                stay=stayService.saveStay(stay);
                response.setStatus(HttpStatus.OK.value());
                response.setSuccess(true);
                response.getData().put("stayInfo",stay);
                response.setMessage("Update stay success");
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
        } catch (BadCredentialsException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Unauthorized, please login again");
            response.setSuccess(false);
            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
        }
    }
    @DeleteMapping("/deleteStay/{id}")
    public ResponseEntity<SuccessResponse> deleteStay(@PathVariable("id") String id,HttpServletRequest req) {
        UserEntity user;
        SuccessResponse response = new SuccessResponse();
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(id);
            if (stay == null) {
                response.setSuccess(false);
                response.setStatus(HttpStatus.FOUND.value());
                response.setMessage("Can't find Stay with id:" + id);
                return new ResponseEntity<>(response, HttpStatus.FOUND);
            }
            if (user != stay.getHost()) {
                response.setSuccess(false);
                response.setStatus(HttpStatus.FOUND.value());
                response.setMessage("You are not stay owner");
                return new ResponseEntity<>(response, HttpStatus.FOUND);
            } else {
                for (UserEntity tempUser : stay.getUserLiked())
                {
                    tempUser.getStayLiked().remove(stay);
                }
                ProvinceEntity province = provinceService.getProvinceById(stay.getProvince().getId());
                if (province!=null)
                province.getStay().remove(province);
                stayService.deleteStay(id);
                response.setStatus(HttpStatus.OK.value());
                response.setSuccess(true);
                response.setMessage("Delete stay success");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (BadCredentialsException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Unauthorized, please login again");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/addStayAmenities/{id}")
    public ResponseEntity<SuccessResponse> addStayAmenities(@PathVariable("id") String id,HttpServletRequest req,@RequestParam String amenitiesId)
    {
        UserEntity user;
        SuccessResponse response = new SuccessResponse();
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(id);
            if (stay==null)
            {
                response.setSuccess(false);
                response.setStatus(HttpStatus.FOUND.value());
                response.setMessage("Can't find Stay with id:" + id);
                return new ResponseEntity<>(response, HttpStatus.FOUND);
            }
            if (user != stay.getHost()) {
                response.setSuccess(false);
                response.setStatus(HttpStatus.FOUND.value());
                response.setMessage("You are not stay owner");
                return new ResponseEntity<>(response, HttpStatus.FOUND);
            }
            AmenitiesEntity amenities = amenitiesService.getAmenitiesById(amenitiesId);
            if (amenities==null)
            {
                response.setSuccess(false);
                response.setStatus(HttpStatus.FOUND.value());
                response.setMessage("amenities not found with id:" + id);
                return new ResponseEntity<>(response, HttpStatus.FOUND);
            }
            if (stay.getAmenities().add(amenities)==false)
            {
                response.setSuccess(false);
                response.setStatus(HttpStatus.FOUND.value());
                response.setMessage("This stay have that amenities");
                return new ResponseEntity<>(response, HttpStatus.FOUND);
            }
            else
            {
                stayService.saveStay(stay);
                response.setStatus(HttpStatus.OK.value());
                response.setSuccess(true);
                response.getData().put("stayInfo", stay);
                response.setMessage("save Stay amenities success");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        catch (BadCredentialsException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Unauthorized, please login again");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/getUserStay")
    public ResponseEntity<SuccessResponse> getUserStay(HttpServletRequest req)
    {
        UserEntity user;
        SuccessResponse response = new SuccessResponse();
        try {
            user = authenticateHandler.authenticateUser(req);
            List<StayEntity> listStay = stayService.getStayByUser(user);
            response.setStatus(HttpStatus.OK.value());
            response.setSuccess(true);
            response.getData().put("listStay",listStay);
            response.setMessage(listStay.isEmpty() ? "List stay is empty" : "Get list stay success");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Unauthorized, please login again");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("addStayToLikedlist/{id}")
    public ResponseEntity<SuccessResponse> addStayToLikeList(@PathVariable("id") String id,HttpServletRequest req)
    {
        UserEntity user;
        SuccessResponse response = new SuccessResponse();
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(id);
            if (stay==null || stay.getHost()==user)
            {
                response.setSuccess(false);
                response.setStatus(HttpStatus.FOUND.value());
                response.setMessage("Can't find Stay or you are owner of that stay");
                return new ResponseEntity<>(response, HttpStatus.FOUND);
            }
            if (!user.getStayLiked().add(stay))
            {
                user.getStayLiked().remove(stay);
                userService.save(user);
                response.setSuccess(true);
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Remove Stay from like list Success");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else
            {
                userService.save(user);
                response.setSuccess(true);
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Add Stay to like list Success");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
        catch (BadCredentialsException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Unauthorized, please login again");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/getUserLikeList")
    public ResponseEntity<SuccessResponse> getUserLikeList(HttpServletRequest req)
    {
        UserEntity user;
        SuccessResponse response = new SuccessResponse();
        try {
            user = authenticateHandler.authenticateUser(req);
            response.setStatus(HttpStatus.OK.value());
            response.setSuccess(true);
            response.getData().put("stayLiked", user.getStayLiked());
            response.setMessage(user.getStayLiked().isEmpty() ? "List liked stay is empty" : "Get list liked stay success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("Unauthorized, please login again");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }


}
