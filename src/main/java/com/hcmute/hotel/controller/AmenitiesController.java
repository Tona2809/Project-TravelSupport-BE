package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.model.entity.AmenitiesEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.service.AmenitiesService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ComponentScan
@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenitiesController {
    static String E404="Not Found";
    static String E400="Bad Request";
    static String E401="Unauthorized";
    private final AmenitiesService amenitiesService;
    @Autowired
    AuthenticateHandler authenticateHandler;

    public void setAuthenticateHandler(AuthenticateHandler authenticateHandler) {
        this.authenticateHandler = authenticateHandler;
    }

    @PostMapping(value = "",consumes = {"multipart/form-data"})
    @ApiOperation("Create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> addNewAmenities(@RequestParam String name, HttpServletRequest req, @RequestPart MultipartFile file)
    {
        UserEntity user;

        try
        {
            user = authenticateHandler.authenticateUser(req);
            AmenitiesEntity amenities = amenitiesService.getAmenitiesByName(name);
            if (amenities!=null)
            {
                return new ResponseEntity<>(new ErrorResponse(E400,"AMENITIES_NAME_ALREADY_EXISTS","Amenities with same name have already exists"),HttpStatus.BAD_REQUEST);
            }
            else
            {
                amenities= new AmenitiesEntity();
                String uuid = String.valueOf(UUID.randomUUID());
                amenities.setId(uuid);
                amenities.setName(name);
                String url = amenitiesService.addAmenitiesIcon(file,amenities);
                amenities.setIcons(url);
                amenities=amenitiesService.addAmenities(amenities);
                return new ResponseEntity<>(amenities,HttpStatus.OK);
            }
        }
        catch (FileNotImageException fileNotImageException)
        {
            return new ResponseEntity<>(new ErrorResponse("Unsupported Media Type","FILE_NOT_IMAGE",fileNotImageException.getMessage()),HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        catch (BadCredentialsException e) {
        return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
    }
    }
    @GetMapping("")
    @ApiOperation("Get All")
    public ResponseEntity<Object> getAllAmenities()
    {
        List<AmenitiesEntity> listAmenities = amenitiesService.getAllAmenities();
        Map<String,Object> map = new HashMap<>();
        map.put("content",listAmenities);
        return new ResponseEntity<>( map,HttpStatus.OK);
    }
    @PatchMapping("/{id}")
    @ApiOperation("Update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> updateAmenities(@PathVariable("id") String id,@RequestParam String name)
    {
        AmenitiesEntity amenities= amenitiesService.getAmenitiesById(id);
        if (amenities==null || amenitiesService.getAmenitiesByName(name)!=null)
        {
            return new ResponseEntity<>(new ErrorResponse(E400,"AMENITIES_NOT_FOUND_OR_EXISTS","Amenities not found or Amenities with same name have already exists"),HttpStatus.BAD_REQUEST);
        }
        else
        {
            amenities.setName(name);
            amenities=amenitiesService.addAmenities(amenities);
            return new ResponseEntity<>(amenities,HttpStatus.OK);
        }
    }
    @DeleteMapping("/{id}")
    @ApiOperation("Delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteAmenities(@PathVariable("id") String id)
    {
        AmenitiesEntity amenities= amenitiesService.getAmenitiesById(id);
        if (amenities==null )
        {
            return new ResponseEntity<>(new ErrorResponse(E404,"AMENITIES_NOT_FOUND","Amenities not found"),HttpStatus.NOT_FOUND);
        }
        for (StayEntity stay : amenities.getStays())
        {
            stay.getAmenities().remove(amenities);
        }
        amenitiesService.deleteAmenities(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
