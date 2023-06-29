package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.mapping.RoomMapping;
import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.Room.AddNewRoomRequest;
import com.hcmute.hotel.model.payload.request.Room.UpdateRoomRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.RoomService;
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
import java.util.List;

@ComponentScan
@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    private final StayService stayService;

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

}
