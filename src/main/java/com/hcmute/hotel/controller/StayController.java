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
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
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
    @PostMapping("")
    @ApiOperation("Add")
    @PreAuthorize("hasRole('ROLE_OWNER')")
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
            province.setPlaceCount(province.getPlaceCount()+1);
            stay = stayService.saveStay(stay);
            provinceService.saveProvince(province);
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
    @DeleteMapping("/{id}")
    @ApiOperation("Delete")
    @PreAuthorize("hasRole('ROLE_OWNER')")
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
                for (BookingEntity booking : stay.getBooking())
                {
                    booking.setStatus(3);
                    bookingService.addBooking(booking);
                }
                ProvinceEntity province = provinceService.getProvinceById(stay.getProvince().getId());
                if (province!=null)
                province.getStay().remove(province);
                province.setPlaceCount(province.getPlaceCount()-1);
                province.getStay().remove(stay);
                stayService.deleteStay(id);
                provinceService.saveProvince(province);
                return new ResponseEntity<>( HttpStatus.OK);
            }
        } catch (BadCredentialsException e) {
                    return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

        }
    }
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
            @RequestParam(defaultValue = "5") int maxPeople,
            @RequestParam(defaultValue = "1000") int maxPrice,
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

}
