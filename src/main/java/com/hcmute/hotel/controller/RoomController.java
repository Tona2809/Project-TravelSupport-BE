package com.hcmute.hotel.controller;

import com.google.common.net.HttpHeaders;
import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.mapping.RoomMapping;
import com.hcmute.hotel.mapping.StayMapping;
import com.hcmute.hotel.model.entity.*;
import com.hcmute.hotel.model.payload.request.Review.AddNewReviewRequest;
import com.hcmute.hotel.model.payload.request.Room.AddNewRoomRequest;
import com.hcmute.hotel.model.payload.request.Room.UpdateRoomRequest;
import com.hcmute.hotel.model.payload.request.Stay.UpdateStayRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.model.payload.response.MessageResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.RoomService;
import com.hcmute.hotel.service.StayService;
import com.hcmute.hotel.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ComponentScan
@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {
    @Autowired
    AuthenticateHandler authenticateHandler;
    private final UserService userService;

    private final RoomService roomService;

    private final StayService stayService;
    @Autowired
    JwtUtils jwtUtils;
    static String E401="Unauthorized";
    static String E404="Not Found";
    static String E400="Bad Request";

    @PostMapping("/add")
    @ApiOperation("Create")
    public ResponseEntity<Object> addRoom(@RequestBody @Valid AddNewRoomRequest addNewRoomRequest, BindingResult errors, HttpServletRequest httpServletRequest) throws Exception {
        if (errors.hasErrors()) {
            throw new MethodArgumentNotValidException(null, errors);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                return new ResponseEntity<>("access token is expired", HttpStatus.UNAUTHORIZED);
            }
            StayEntity stay = stayService.getStayById(addNewRoomRequest.getStayid());
            if (stay == null) {
                return new ResponseEntity<>("Stay not found", HttpStatus.NOT_FOUND);
            }
            RoomEntity room = RoomMapping.addRoomtoEntity(addNewRoomRequest);
            room.setStay(stay);
            room = roomService.addRoom(room);
            return new ResponseEntity<>(room, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("access token is missing", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/stay/{stayid}")
    @ApiOperation("Get all by stay id")
    public ResponseEntity<Object> getAllRoom(@PathVariable("stayid") String stayid) {
        StayEntity stay = stayService.getStayById(stayid);
        if(stay == null) {
            return new ResponseEntity<>(new ErrorResponse(E404,"STAY_NOT_FOUND","Can't find Stay with id provided"),HttpStatus.NOT_FOUND);
        }
        else {
            List<RoomEntity> roomEntityList = roomService.getAllRoomByStayID(stayid);
            Map<String,Object> map = new HashMap<>();
            map.put("content",roomEntityList);
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("Get by id")
    public ResponseEntity<Object> getRoomByUid(@PathVariable("id") String id) {
        RoomEntity room = roomService.findRoomById(id);
        if(room == null) {
            return new ResponseEntity<>(new ErrorResponse(E404, "ROOM_NOT_FOUND", "Can't find room with id provided"), HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
    }

    @PatchMapping("{id}")
    @ApiOperation("Update")
    public ResponseEntity<Object> updateRoomInfo(@PathVariable("id") String id, @Valid @RequestBody UpdateRoomRequest updateRoomRequest, HttpServletRequest req)
    {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            RoomEntity room = roomService.findRoomById(id);

            if (room==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E404,"ROOM_NOT_FOUND","Can't find room with id provided"),HttpStatus.NOT_FOUND);
            }
            else
            {
                room= RoomMapping.updateRoomtoEntity(updateRoomRequest,room);
                room=roomService.addRoom(room);
                return new ResponseEntity<>(room,HttpStatus.OK);
            }
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401,"UNAUTHORIZED","Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);

        }
    }
    @DeleteMapping("/{id}")
    @ApiOperation("Delete")
    public ResponseEntity<Object> deleteRoomById(@PathVariable("id") String id, HttpServletRequest httpServletRequest) {
        String authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            RoomEntity room = roomService.findRoomById(id);
            if (room == null) {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "ROOM_ID_NOT_FOUND", "Room id not found");
                return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
            }
            else {

                    roomService.deleteById(room.getId());
                    return new ResponseEntity<>(HttpStatus.OK);
            }
        } else throw new BadCredentialsException("access token is missing");
    }

}
