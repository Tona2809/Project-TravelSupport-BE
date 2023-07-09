package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.mapping.RoomMapping;
import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.RoomServiceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.Room.AddNewRoomRequest;
import com.hcmute.hotel.model.payload.request.Room.UpdateRoomRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.RoomService;
import com.hcmute.hotel.service.RoomServiceService;
import com.hcmute.hotel.service.StayService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.*;

@ComponentScan
@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    private final StayService stayService;

    private final RoomServiceService roomServiceService;

    private final AuthenticateHandler authenticateHandler;

    static String E401="Unauthorized";
    static String E404="Not Found";
    static String E400="Bad Request";


    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("")
    @ApiOperation("Create")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> addRoom(HttpServletRequest req, @RequestBody @Valid AddNewRoomRequest addNewRoomRequest) {
        try
        {
            UserEntity user = new UserEntity();
            user = authenticateHandler.authenticateUser(req);
            StayEntity stay = stayService.getStayById(addNewRoomRequest.getStayId());
            if (stay == null) {
                return new ResponseEntity<>(new ErrorResponse(E404, "STAY_NOT_FOUND", "Can't find Stay with id provided"), HttpStatus.NOT_FOUND);
            }
            RoomEntity room = RoomMapping.addToEntity(addNewRoomRequest);
            room.setStay(stay);
            roomService.addRoom(room);
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PatchMapping("/{roomId}")
    @ApiOperation("Update")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> updateRoom(HttpServletRequest req, @RequestBody @Valid UpdateRoomRequest updateRoomRequest, @PathVariable("roomId") String roomId) {
        try
        {
            UserEntity user = new UserEntity();
            user = authenticateHandler.authenticateUser(req);

            RoomEntity room = roomService.findRoomById(roomId);
            if (room == null )
            {
                return new ResponseEntity<>(new ErrorResponse(E404, "ROOM_NOT_FOUND", "Can't find room with id provided"), HttpStatus.NOT_FOUND);
            }
            room=RoomMapping.updateToEntity(room,updateRoomRequest);
            roomService.addRoom(room);
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/hidden/{roomId}")
    @ApiOperation("Set hidden")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> setHidden(HttpServletRequest req, @PathVariable("roomId") String roomId, @RequestParam boolean hidden)
    {
        try
        {
            UserEntity user = new UserEntity();
            user = authenticateHandler.authenticateUser(req);

            RoomEntity room = roomService.findRoomById(roomId);
            if (room == null )
            {
                return new ResponseEntity<>(new ErrorResponse(E404, "ROOM_NOT_FOUND", "Can't find room with id provided"), HttpStatus.NOT_FOUND);
            }
            room.setHidden(hidden);
            roomService.addRoom(room);
            return new ResponseEntity<>(room, HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("")
    @ApiOperation("Get room by Id")
    public ResponseEntity<Object> getRoomById(@RequestParam List<String> roomIds)
    {
        List<RoomEntity> room = roomService.findRoomByListId(roomIds);
        if (room == null )
        {
            return new ResponseEntity<>(new ErrorResponse(E404, "ROOM_NOT_FOUND", "Can't find room with id provided"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @GetMapping("/list/{stayId}")
    @ApiOperation("Get room by stay")
    public ResponseEntity<Object> getListRoomByStay(@PathVariable("stayId") String stayId)
    {
        List<RoomEntity> list = roomService.findRoomsByStayId(stayId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/roomService/{roomId}")
    @ApiOperation("Add room service")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> addRoomServiceForRoom(@PathVariable("roomId") String roomId,@RequestParam String[] roomServiceId,HttpServletRequest req)
    {
        UserEntity user;

        try
        {
            user = authenticateHandler.authenticateUser(req);
            RoomEntity room = roomService.findRoomById(roomId);
            if (room==null)
            {
                return new ResponseEntity<>(new ErrorResponse(E400, "ROOM_NOT_FOUND", "Can n't find room with ids provided"),HttpStatus.BAD_REQUEST);
            }
            Set<RoomServiceEntity> serviceEntitySet = new HashSet<>();
            for (String id : roomServiceId)
            {
                RoomServiceEntity serviceEntity = roomServiceService.findRoomServiceById(id);
                if (serviceEntity!=null)
                {
                    serviceEntitySet.add(serviceEntity);
                }
            }
            room.setRoomService(serviceEntitySet);
            room = roomService.addRoom(room);
            return new ResponseEntity<>(room,HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/searchAll")
    @ApiOperation("Filter Stay room")
    public ResponseEntity<Object> filterRoomStay(@RequestParam(value = "checkinDate",required = false) String checkinDateStr,
                                                 @RequestParam(value = "checkoutDate",required = false) String checkoutDateStr,
                                                 @RequestParam("guestAdults") int guestAdults,
                                                 @RequestParam("guestChildren") int guestChildren,
                                                 @RequestParam("guestInfants") int guestInfants,
                                                 @RequestParam("stayId") String stayId)
    {
        LocalDateTime checkinDate = null;
        LocalDateTime checkoutDate = null;

        try {
            checkinDateStr = URLDecoder.decode(checkinDateStr, "UTF-8");
            checkoutDateStr = URLDecoder.decode(checkoutDateStr, "UTF-8");

            if (checkinDateStr != null && !checkinDateStr.isEmpty()) {
                checkinDate = LocalDateTime.parse(checkinDateStr);
            }

            if (checkoutDateStr != null && !checkoutDateStr.isEmpty()) {
                checkoutDate = LocalDateTime.parse(checkoutDateStr);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        List<RoomEntity> rooms = roomService.findRoomsByStayId(stayId);
        List<RoomEntity> filterRooms = new ArrayList<>();
        int guestNumbers = guestAdults + guestChildren + guestInfants;
        int flexibleNumber = 1 + guestChildren/2 + guestInfants/3;
        Map<String, Integer> result = roomService.getAvailableRoom(checkinDate, checkoutDate, guestNumbers, stayId, flexibleNumber);
        for (RoomEntity room : rooms)
        {
            if (result.get(room.getId())!=null)
            {
                room.setNumberOfRoom(room.getNumberOfRoom()-result.get(room.getId()));
                filterRooms.add(room);
            }
        }
        return new ResponseEntity<>(filterRooms,HttpStatus.OK);
    }
}
