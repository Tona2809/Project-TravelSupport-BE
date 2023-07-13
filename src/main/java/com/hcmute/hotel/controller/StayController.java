package com.hcmute.hotel.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hcmute.hotel.common.OrderByEnum;
import com.hcmute.hotel.common.StaySortEnum;
import com.hcmute.hotel.common.StayStatus;
import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.mapping.StayMapping;
import com.hcmute.hotel.model.entity.*;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.model.payload.request.Stay.AddNewStayRequest;
import com.hcmute.hotel.model.payload.request.Stay.UpdateStayRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.model.payload.response.PagingResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.aspectj.util.Reflection;
import org.hibernate.query.criteria.internal.path.MapKeyHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
@ComponentScan
@RestController
@RequestMapping("/api/stay")
@RequiredArgsConstructor
@EnableWebSecurity
public class StayController {
    @Autowired
    AuthenticateHandler authenticateHandler;
    private final BookingService bookingService;
    private final StayService stayService;
    private final AmenitiesService amenitiesService;
    private final UserService userService;
    private final ProvinceService provinceService;
    private final ObjectMapper objectMapper;
    static String E401="Unauthorized";
    static String E404="Not Found";
    static String E400="Bad Request";
    @PostMapping(value = "", consumes = {"multipart/form-data"})
    @ApiOperation("Add")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    @Transactional
    public ResponseEntity<Object> addStay(HttpServletRequest req,
                                          @NotEmpty @RequestParam("name")  String stayName,
                                          @NotEmpty @RequestParam("addressDescription") String addressDescription,
                                          @RequestPart("stayImage") MultipartFile[] files,
                                          @NotEmpty @RequestParam("type") String type,
                                          @NotEmpty @RequestParam("stayDescription") String stayDescription,
                                          @NotEmpty @RequestParam("provinceId") String provinceId,
                                          @NotEmpty @RequestParam("checkinTime") String checkinTime,
                                          @NotEmpty @RequestParam("checkoutTime") String checkoutTime,
                                          @NotNull @RequestParam("longitude") String longitude,
                                          @NotNull @RequestParam("latitude") String latitude,
                                          @RequestParam("amenities") String[] amenities

    )
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = new StayEntity();
            stay.setName(stayName);
            stay.setStayDescription(stayDescription);
            stay.setAddressDescription(addressDescription);
            stay.setType(type);
            stay.setCheckinTime(checkinTime);
            stay.setCheckoutTime(checkoutTime);
            stay.setCreatedAt(LocalDateTime.now());
            stay.setTimeOpen(LocalDateTime.now().minus(1, ChronoUnit.DAYS));
            stay.setTimeClose(LocalDateTime.now().plus(100,ChronoUnit.YEARS));
            stay.setHost(user);
            stay.setHidden(false);
            stay.setStatus(1);
            stay.setLongitude(Double.valueOf(longitude));
            stay.setLatitude(Double.valueOf(latitude));
            ProvinceEntity province = provinceService.getProvinceById(provinceId);
            if (province==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"PROVINCE_NOT_FOUND","Can't find Province with id provided"),HttpStatus.NOT_FOUND);
            }
            stay.setProvince(province);
            stay = stayService.saveStay(stay);
            try {
                stayService.addStayImg(files,stay);
            } catch (RuntimeException e)
            {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                System.out.println(e.getMessage());
                return new ResponseEntity<>(new ErrorResponse(E400,"CANNOT_ADD_IMAGE","Can't add image"),HttpStatus.BAD_REQUEST);
            }
            stay.setAmenities(new HashSet<>());
            for (String amenityId : amenities)
            {
                AmenitiesEntity amenitiesEntity = amenitiesService.getAmenitiesById(amenityId);
                if (amenitiesEntity!=null)
                {
                    stay.getAmenities().add(amenitiesEntity);
                }
            }
            stay=stayService.saveStay(stay);
            province.setPlaceCount(province.getPlaceCount()+1);
            provinceService.saveProvince(province);
            return new ResponseEntity<>(stay,HttpStatus.OK);
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


    @PatchMapping(value = "", consumes = {"multipart/form-data"})
    @ApiOperation("Update")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    @Transactional
    public ResponseEntity<Object> patchStay(HttpServletRequest req,
                                            @NotEmpty @RequestParam("id") String stayId,
                                            @NotEmpty @RequestParam("name") String name,
                                            @NotEmpty @RequestParam("addressDescription") String addressDescription,
                                            @NotEmpty @RequestParam("stayDescription") String stayDescription,
                                            @NotEmpty @RequestParam("checkinTime") String checkinTime,
                                            @NotEmpty @RequestParam("checkoutTime") String checkoutTime,
                                            @RequestParam(value = "amenities", required = false) String[] amenities,
                                            @RequestParam(value = "removedImage", required = false) String[] removedImage,
                                            @RequestPart(value = "newImage",required = false) MultipartFile[] files,
                                            @NotNull @RequestParam("longitude") String longitude,
                                            @NotNull @RequestParam("latitude") String latitude)
    {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(stayId);
            if (stay==null || stay.getHost() != user)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND_OR_NOT_OWNER","Can't find stay with id provided"),HttpStatus.NOT_FOUND);
            }
            stay.setName(name);
            stay.setAddressDescription(addressDescription);
            stay.setStayDescription(stayDescription);
            stay.setCheckinTime(checkinTime);
            stay.setCheckoutTime(checkoutTime);
            stay.setLongitude(Double.valueOf(longitude));
            stay.setLatitude(Double.valueOf(latitude));
            Set<AmenitiesEntity> newAmenities = new HashSet<>();
            if (amenities!=null) {
                for (String amenityId : amenities) {
                    AmenitiesEntity amenitiesEntity = amenitiesService.getAmenitiesById(amenityId);
                    if (amenitiesEntity != null) {
                        newAmenities.add(amenitiesEntity);
                    }
                }
            }
            stay.setAmenities(newAmenities);

            try {
                stayService.addStayImg(files,stay);
            } catch (RuntimeException e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                System.out.println(e.getMessage());
                return new ResponseEntity<>(new ErrorResponse(E400, "CANNOT_ADD_IMAGE", "Can't add image"), HttpStatus.BAD_REQUEST);
            }
            if (removedImage !=null) {
                for (String link : removedImage) {
                    StayImageEntity stayImage = stayService.getImageByLink(link);
                    if (stayImage != null) {
                        stayService.deleteImage(stayImage);
                        stay.getStayImage().remove(stayImage);
                    }
                }
            }
            stay = stayService.saveStay(stay);
            return new ResponseEntity<>(stay, HttpStatus.OK);
        } catch (BadCredentialsException e) {
                    return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

        }

    }
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
    @PreAuthorize("hasRole('ROLE_OWNER')")
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
//    @DeleteMapping("/{id}")
//    @ApiOperation("Delete")
//    @PreAuthorize("hasRole('ROLE_OWNER')")
//    public ResponseEntity<Object> deleteStay(@PathVariable("id") String id,HttpServletRequest req) {
//        UserEntity user;
//        try {
//            user = authenticateHandler.authenticateUser(req);
//            StayEntity stay = stayService.getStayById(id);
//            if (stay == null) {
//                return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND","Can't find Stay with id provided"),HttpStatus.NOT_FOUND);
//            }
//            if (user != stay.getHost()) {
//                return new ResponseEntity<>(new ErrorResponse(E400,"INVALID_STAY_OWNER","You are not stay owner"),HttpStatus.BAD_REQUEST);
//            } else {
//                for (UserEntity tempUser : stay.getUserLiked())
//                {
//                    tempUser.getStayLiked().remove(stay);
//                }
//                for (BookingEntity booking : stay.getBooking())
//                {
//                    booking.setStatus(3);
//                    bookingService.addBooking(booking);
//                }
//                ProvinceEntity province = provinceService.getProvinceById(stay.getProvince().getId());
//                if (province!=null)
//                province.getStay().remove(province);
//                province.setPlaceCount(province.getPlaceCount()-1);
//                province.getStay().remove(stay);
//                stayService.deleteStay(id);
//                provinceService.saveProvince(province);
//                return new ResponseEntity<>( HttpStatus.OK);
//            }
//        } catch (BadCredentialsException e) {
//                    return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
//
//        }
//    }
    @PostMapping("/stayAmenities/{id}")
    @ApiOperation("Add")
    @PreAuthorize("hasRole('ROLE_OWNER')")
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
    @GetMapping("/OwnedStay")
    @ApiOperation("Get")
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
    @PostMapping("likeList/{id}")
    @ApiOperation("Create")
    @PreAuthorize("hasRole('ROLE_USER')")
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
    @GetMapping("/likeList")
    @ApiOperation("Get")
    @PreAuthorize("hasRole('ROLE_USER')")
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
    @PostMapping(value = "/image/{id}",consumes = {"multipart/form-data"})
    @ApiOperation("Create")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> addStayImg(@PathVariable("id") String id, @RequestPart MultipartFile[] multipartFile,HttpServletRequest req) {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(id);
            if (stay == null) {
                return new ResponseEntity<>(new ErrorResponse(E404, "STAY_NOT_FOUND", "Can't find Stay with id provided"), HttpStatus.NOT_FOUND);
            }
            if (user != stay.getHost()) {
                return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_STAY_OWNER", "You are not stay owner"), HttpStatus.BAD_REQUEST);
            }
            List<StayImageEntity> listImage = stayService.addStayImg(multipartFile, stay);
            return new ResponseEntity<>(listImage,HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        } catch (FileNotImageException fileNotImageException)
        {
            return new ResponseEntity<>(new ErrorResponse("Unsupported Media Type","FILE_NOT_IMAGE",fileNotImageException.getMessage()),HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        catch (RuntimeException runtimeException)
        {
            return new ResponseEntity<>(new ErrorResponse(E400,"FAIL_TO_UPLOAD",runtimeException.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/image/{id}")
    @ApiOperation("Delete")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> deleteStayImg(@PathVariable("id") String id,@RequestParam String[] imageId,HttpServletRequest req) {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(id);
            if (stay == null) {
                return new ResponseEntity<>(new ErrorResponse(E404, "STAY_NOT_FOUND", "Can't find Stay with id provided"), HttpStatus.NOT_FOUND);
            }
            if (user != stay.getHost()) {
                return new ResponseEntity<>(new ErrorResponse(E400, "INVALID_STAY_OWNER", "You are not stay owner"), HttpStatus.BAD_REQUEST);
            }
            StayImageEntity stayImage;
            for (String temp : imageId)
            {
                stayImage = stayService.findImgById(temp);
                if (stayImage!=null && stayImage.getStay()==stay)
                {
                    stay.getStayImage().remove(stayImage);
                    stayService.DeleteImg(temp);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/search")
    @ApiOperation("Search")
    public ResponseEntity<Object> searchByCriteria(
            @RequestParam(required = false) String provinceId,
            @RequestParam(required = false) String searchKey,
            @RequestParam(defaultValue = "0",name = "pageIndex") int page,
            @RequestParam(defaultValue = "5",name = "pageSize") int size,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false,defaultValue = "") LocalDateTime checkInDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false,defaultValue = "") LocalDateTime checkOutDate,
            @RequestParam(defaultValue = "1") int maxPeople,
            @RequestParam(defaultValue = "50000000") int maxPrice,
            @RequestParam(defaultValue = "0") int minPrice,
            @RequestParam(required = false) List<String> amenitiesId,
            @RequestParam(defaultValue = "PRICE") StaySortEnum sort,
            @RequestParam(defaultValue = "DESCENDING") OrderByEnum order,
            @RequestParam(defaultValue = "NULL") StayStatus status,
            @RequestParam(required = false)boolean hidden)
    {
        String isEmpty = null;
        if (amenitiesId!=null)
        {
           isEmpty = "Not null";
        }
        Page<StayEntity> stayPage = stayService.searchByCriteria(provinceId,minPrice,maxPrice,checkInDate,checkOutDate,status.getName(),hidden,maxPeople,searchKey,page,size,sort.getName(),order.getName(),isEmpty,amenitiesId);
        List<StayEntity> listStay = stayPage.toList();
        PagingResponse pagingResponse = new PagingResponse();
        List<Object> Result = Arrays.asList(listStay.toArray());
        pagingResponse.setTotalPages(stayPage.getTotalPages());
        pagingResponse.setEmpty(listStay.size()==0);
        pagingResponse.setFirst(page==0);
        pagingResponse.setLast(page == stayPage.getTotalPages()-1);
        pagingResponse.getPageable().put("pageNumber",page);
        pagingResponse.getPageable().put("pageSize",size);
        pagingResponse.setSize(size);
        pagingResponse.setNumberOfElements(listStay.size());
        pagingResponse.setTotalElements((int) stayPage.getTotalElements());
        pagingResponse.setContent(Result);
        return new ResponseEntity<>(pagingResponse ,HttpStatus.OK);
    }

    @GetMapping("/owner/static")
    @ApiOperation("Owner stay static")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> ownerStayStatic(HttpServletRequest req)
    {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(req);
            List<StayEntity> listStay = stayService.getStayByUser(user);
            Map<String, Object> result = new HashMap<>();
            result.put("totalStay", listStay.size());
            int numberStayHaveRoom = 0, totalRoom = 0, totalRating = 0;
            Map<String, Integer> provinceStayCount = new HashMap<>();
            Map<String, Integer> roomStayCount = new HashMap<>();
            Map<String, Integer> stayRatingCount = new HashMap<>();
            Map<String, Integer> stayTypeCount = new HashMap<>();
            for (StayEntity stay: listStay)
            {
                if (stay.getRoom()!= null && stay.getRoom().size()>0)
                {
                    numberStayHaveRoom+=1;
                    totalRoom+=stay.getRoom().size();
                    roomStayCount.put(stay.getName(), stay.getRoom().size());
                }
                provinceStayCount.merge(stay.getProvince().getName(), 1, Integer::sum);
                stayTypeCount.merge(stay.getType(),1,Integer::sum);
                if (stay.getStayRating()!=null && stay.getStayRating().size()>0)
                {
                    totalRating+=stay.getStayRating().size();
                    stayRatingCount.put(stay.getName(),stay.getStayRating().size());
                }
            }
            result.put("stayHaveRoom",numberStayHaveRoom);
            result.put("totalRoom", totalRoom);
            result.put("totalRating", totalRating);
            result.put("provinceStay",provinceStayCount);
            result.put("roomStay",roomStayCount);
            result.put("stayRating", stayRatingCount);
            result.put("stayType", stayTypeCount);
            return new ResponseEntity<>(result,HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
}
