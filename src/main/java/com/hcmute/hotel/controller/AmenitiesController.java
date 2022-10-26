package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.model.entity.AmenitiesEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.service.AmenitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ComponentScan
@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenitiesController {
    private final AmenitiesService amenitiesService;
    @Autowired
    AuthenticateHandler authenticateHandler;
    @PostMapping("/addAmenities")
    public ResponseEntity<SuccessResponse> addNewAmenities(@RequestParam String name, HttpServletRequest req)
    {
        SuccessResponse response = new SuccessResponse();
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(req);
            AmenitiesEntity amenities = amenitiesService.getAmenitiesByName(name);
            if (amenities!=null)
            {
                response.setStatus( HttpStatus.FOUND.value());
                response.setSuccess(false);
                response.setMessage("Amenities with name "+ name +" have already exists");
                return new ResponseEntity<>(response,HttpStatus.FOUND);
            }
            else
            {
                amenities= new AmenitiesEntity();
                amenities.setName(name);
                amenities=amenitiesService.addAmenities(amenities);
                response.setMessage("Add amenities success");
                response.setSuccess(true);
                response.setStatus(HttpStatus.OK.value());
                response.getData().put("Amenities",amenities);
                return new ResponseEntity<>(response,HttpStatus.OK);
            }
        }
        catch (BadCredentialsException e) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Unauthorized, please login again");
        response.setSuccess(false);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    }
    @GetMapping("/getAllAmenities")
    public ResponseEntity<SuccessResponse> getAllAmenities()
    {
        List<AmenitiesEntity> listAmenities = amenitiesService.getAllAmenities();
        SuccessResponse response = new SuccessResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setSuccess(true);
        response.getData().put("listAmenities",listAmenities);
        response.setMessage(listAmenities.isEmpty() ? "List stay is empty" : "Get list stay success");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PatchMapping("/updateAmenities/{id}")
    public ResponseEntity<SuccessResponse> updateAmenities(@PathVariable("id") String id,@RequestParam String name)
    {
        AmenitiesEntity amenities= amenitiesService.getAmenitiesById(id);
        SuccessResponse response = new SuccessResponse();
        if (amenities==null || amenitiesService.getAmenitiesByName(name)!=null)
        {
            response.setStatus( HttpStatus.FOUND.value());
            response.setSuccess(false);
            response.setMessage("Amenities not found or Amenities with name " + name + " have already exists");
            return new ResponseEntity<>(response,HttpStatus.FOUND);
        }
        else
        {
            amenities.setName(name);
            amenities=amenitiesService.addAmenities(amenities);
            response.setMessage("Update amenities success");
            response.setSuccess(true);
            response.setStatus(HttpStatus.OK.value());
            response.getData().put("Amenities",amenities);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponse> deleteAmenities(@PathVariable("id") String id)
    {
        AmenitiesEntity amenities= amenitiesService.getAmenitiesById(id);
        SuccessResponse response = new SuccessResponse();
        if (amenities==null )
        {
            response.setStatus( HttpStatus.FOUND.value());
            response.setSuccess(false);
            response.setMessage("Amenities not found");
            return new ResponseEntity<>(response,HttpStatus.FOUND);
        }
        for (StayEntity stay : amenities.getStays())
        {
            stay.getAmenities().remove(amenities);
        }
        amenitiesService.deleteAmenities(id);
        response.setMessage("Delete amenities success");
        response.setSuccess(true);
        response.setStatus(HttpStatus.OK.value());
        return new ResponseEntity<>(response,HttpStatus.OK);

    }
}
