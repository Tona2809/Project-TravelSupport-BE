package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.model.entity.*;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.service.PlaceService;
import com.hcmute.hotel.service.ProvinceService;
import com.hcmute.hotel.service.StayService;
import com.hcmute.hotel.service.distanceCaculator.DistanceCalculator;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

@ComponentScan
@RestController
@RequestMapping("/api/place")
@RequiredArgsConstructor
public class PlaceController {

    @Value("${google.api.key}")
    private String apiKey;

    private final PlaceService placeService;

    private final ProvinceService provinceService;

    private final AuthenticateHandler authenticateHandler;

    private final StayService stayService;

    static String E401="Unauthorized";
    static String E404="Not Found";
    static String E400="Bad Request";

    @PostMapping(value = "", consumes = {"multipart/form-data"})
    @ApiOperation("Add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Object> addPlace(HttpServletRequest req,
                                           @NotEmpty @RequestParam("name") String placeName,
                                           @RequestPart("placeImage") MultipartFile[] files,
                                           @NotEmpty @RequestParam("provinceId") String provinceId,
                                           @NotEmpty @RequestParam("description") String description,
                                           @NotEmpty @RequestParam("addressDescription") String addressDescription,
                                           @NotEmpty @RequestParam("longitude") String longitude,
                                           @NotEmpty @RequestParam("latitude") String latitude,
                                           @NotEmpty @RequestParam("timeClose") String timeClose,
                                           @NotEmpty @RequestParam("timeOpen") String timeOpen,
                                           @NotEmpty @RequestParam("type") String type,
                                           @NotEmpty @RequestParam("minPrice") String minPrice,
                                           @NotEmpty @RequestParam("maxPrice") String maxPrice,
                                           @NotEmpty @RequestParam("recommendTime") String recommendTime)
    {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(req);
            PlaceEntity place = new PlaceEntity();
            place.setName(placeName);
            place.setHidden(false);
            place.setDescription(description);
            place.setAddressDescription(addressDescription);
            place.setLatitude(Double.valueOf(latitude));
            place.setLongitude(Double.valueOf(longitude));
            place.setTimeClose(timeClose);
            place.setTimeOpen(timeOpen);
            place.setType(type);
            place.setAuthor(user);
            place.setMinPrice(Integer.parseInt(minPrice));
            place.setMaxPrice(Integer.parseInt(maxPrice));
            place.setRecommendTime(recommendTime);
            ProvinceEntity province = provinceService.getProvinceById(provinceId);
            if (province==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"PROVINCE_NOT_FOUND","Can't find Province with id provided"), HttpStatus.NOT_FOUND);
            }
            place.setProvince(province);
            place = placeService.addPlace(place);
            try {
                placeService.addPlaceImg(files,place);
            } catch (RuntimeException e)
            {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                System.out.println(e.getMessage());
                return new ResponseEntity<>(new ErrorResponse(E400,"CANNOT_ADD_IMAGE","Can't add image"),HttpStatus.BAD_REQUEST);
            }
            province.getPlace().add(place);
            place = placeService.addPlace(place);
            return new ResponseEntity<>(place, HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PatchMapping(value = "", consumes = {"multipart/form-data"})
    @ApiOperation("Update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Object> patchPlace(HttpServletRequest req,
                                            @NotEmpty @RequestParam("id") String placeId,
                                            @NotEmpty @RequestParam("name") String placeName,
                                            @RequestParam(value = "removedImage", required = false) String[] removedImage,
                                            @RequestPart(value = "newImage",required = false) MultipartFile[] files,
                                            @NotEmpty @RequestParam("description") String description,
                                            @NotEmpty @RequestParam("addressDescription") String addressDescription,
                                            @NotEmpty @RequestParam("longitude") String longitude,
                                            @NotEmpty @RequestParam("latitude") String latitude,
                                            @NotEmpty @RequestParam("timeClose") String timeClose,
                                            @NotEmpty @RequestParam("timeOpen") String timeOpen,
                                            @NotEmpty @RequestParam("minPrice") String minPrice,
                                            @NotEmpty @RequestParam("maxPrice") String maxPrice,
                                            @NotEmpty @RequestParam("recommendTime") String recommendTime)
    {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(req);
            PlaceEntity place = placeService.getPlaceById(placeId);
            if (place==null || place.getAuthor()!= user)
            {
                return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_PLACE_OR_USER", "Địa điểm không tồn tại hoặc bạn không có quyền sửa"), HttpStatus.BAD_REQUEST);
            }
            place.setName(placeName);
            place.setDescription(description);
            place.setAddressDescription(addressDescription);
            place.setLongitude(Double.valueOf(longitude));
            place.setLatitude(Double.valueOf(latitude));
            place.setTimeOpen(timeOpen);
            place.setTimeClose(timeClose);
            place.setMaxPrice(Integer.parseInt(maxPrice));
            place.setMinPrice(Integer.parseInt(minPrice));
            place.setRecommendTime(recommendTime);
            try{
                placeService.addPlaceImg(files,place);
            }
            catch (RuntimeException e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                System.out.println(e.getMessage());
                return new ResponseEntity<>(new ErrorResponse(E400, "CANNOT_ADD_IMAGE", "Can't add image"), HttpStatus.BAD_REQUEST);
            }
            if (removedImage!= null) {
                for (String link : removedImage)
                {
                    PlaceImageEntity placeImage = placeService.getPlaceImageByLink(link);
                    if (placeImage!=null)
                    {
                        placeService.deletePlaceImage(placeImage);
                        place.getPlaceImage().remove(placeImage);
                    }
                }
            }
            place = placeService.addPlace(place);
            return new ResponseEntity<>(place, HttpStatus.OK);
        }catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/search")
    @ApiOperation("Search Place")
    public ResponseEntity<Object> searchPlace(@RequestParam(required = false, value = "searchKey") String searchKey,
                                              @RequestParam(required = false, value = "latitude") double latitude,
                                              @RequestParam(required = false, value = "longitude") double longitude,
                                              @RequestParam(required = false, value = "provinceId") String provinceId)
    {
        List<PlaceEntity> list = placeService.searchPlace(searchKey, latitude, longitude, provinceId);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/buildTrip")
    @ApiOperation("Build a trip")
    public ResponseEntity<Object> getNearByList(
            @RequestParam("stayId") String stayId,
            @RequestParam(required = false, value = "searchKey") String searchKey)
    {
        StayEntity stay = stayService.getStayById(stayId);
        List<PlaceEntity> list = placeService.searchPlace(searchKey,stay.getLatitude(),stay.getLongitude(), null);
        PlaceEntity target = new PlaceEntity();
        target.setLatitude(stay.getLatitude());
        target.setLongitude(stay.getLongitude());
        int count = 4;
        Map<String,Double> map = new LinkedHashMap<>();
        while (!list.isEmpty() && count>0)
        {
            double max = Double.MAX_VALUE;
            int index=0;
            for (int i=0;i<list.size();i++) {
                double distance = DistanceCalculator.calculateDistance(target.getLatitude(), target.getLongitude(), list.get(i).getLatitude(), list.get(i).getLongitude());
                if (distance<max)
                {
                    max=distance;
                    index = i;
                }
            }
            double rootDistance = DistanceCalculator.calculateDistance(list.get(index).getLatitude(),list.get(index).getLongitude(),stay.getLatitude(),stay.getLongitude());
            String formattedRootDistance = String.format("%.2f", rootDistance);
            map.put(list.get(index).getId(),Double.parseDouble(formattedRootDistance));
            target = list.get(index);
            list.remove(index);
            count--;
        }
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
    @GetMapping("/getDistance")
    @ApiOperation("Caculate the distance")
    public ResponseEntity<Object> getDistance(@RequestParam("lat1") double lat1,
                                              @RequestParam("lng1") double lng1,
                                              @RequestParam("lat2") double lat2,
                                              @RequestParam("lng2") double lng2)
    {
        double result = DistanceCalculator.calculateDistance(lat1,lng1,lat2,lng2);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @GetMapping("/province/{provinceId}")
    @ApiOperation("Get All Place By Provice")
    public ResponseEntity<Object> getAllByProvince(@PathVariable("provinceId") String provinceId)
    {
        ProvinceEntity province = provinceService.getProvinceById(provinceId);
        if (province==null)
        {
            return new ResponseEntity<>(new ErrorResponse(E404,"PROVINCE_NOT_FOUND","Can't find Province with id provided"), HttpStatus.NOT_FOUND);
        }
        List<PlaceEntity> list = placeService.getAllByProvince(provinceId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    @GetMapping("/buildNewRoute")
    @ApiOperation("Build new route")
    public ResponseEntity<Object> buildNewTrip(@RequestParam("stayId") String stayId,
                                               @RequestParam("placeList") String[] placeList)
    {
        StayEntity stay = stayService.getStayById(stayId);
        List<PlaceEntity> list = new ArrayList<>();
        for (String place : placeList) {
            PlaceEntity placeEntity = placeService.getPlaceById(place);
            if (placeEntity == null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"PLACE_NOT_FOUND","Can't find Place with id provided"), HttpStatus.NOT_FOUND);
            }
            list.add(placeEntity);
        }
        PlaceEntity target = new PlaceEntity();
        target.setLatitude(stay.getLatitude());
        target.setLongitude(stay.getLongitude());
        Map<String,Double> result = new LinkedHashMap<>();
        if (list.size()>0) {
            while (list.size() > 1) {
                double max = Double.MAX_VALUE;
                int index = 0;
                for (int i = 0; i < list.size(); i++) {
                    double distance = DistanceCalculator.calculateDistance(target.getLatitude(), target.getLongitude(), list.get(i).getLatitude(), list.get(i).getLongitude());
                    if (distance < max) {
                        max = distance;
                        index = i;
                    }
                }
                double rootDistance = DistanceCalculator.calculateDistance(list.get(index).getLatitude(), list.get(index).getLongitude(), stay.getLatitude(), stay.getLongitude());
                String formattedRootDistance = String.format("%.2f", rootDistance);
                result.put(list.get(index).getId(), Double.parseDouble(formattedRootDistance));
                target = list.get(index);
                list.remove(index);
            }
            double rootDistance = DistanceCalculator.calculateDistance(list.get(0).getLatitude(), list.get(0).getLongitude(), stay.getLatitude(), stay.getLongitude());
            String formattedRootDistance = String.format("%.2f", rootDistance);
            result.put(list.get(0).getId(),Double.parseDouble(formattedRootDistance));
        }
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @GetMapping("/{placeId}")
    @ApiOperation("Get Place By id")
    public ResponseEntity<Object> getPlaceById(@PathVariable("placeId") String placeId)
    {
        PlaceEntity place = placeService.getPlaceById(placeId);
        if (place == null)
        {
            return new ResponseEntity<>(new ErrorResponse(E404,"PLACE_NOT_FOUND","Không tìm thấy địa điểm"), HttpStatus.OK);
        }
        return new ResponseEntity<>(place,HttpStatus.OK);
    }
}
