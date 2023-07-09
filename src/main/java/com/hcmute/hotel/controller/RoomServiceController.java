package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.mapping.RoomServiceMapping;
import com.hcmute.hotel.model.entity.*;
import com.hcmute.hotel.model.payload.request.RoomService.AddNewRoomServiceRequest;
import com.hcmute.hotel.model.payload.request.RoomService.UpdateRoomServiceRequest;
import com.hcmute.hotel.model.payload.response.DataResponse;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.model.payload.response.MessageResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.RoomServiceService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@ComponentScan
@RestController
@RequestMapping("/api/roomservice")
@RequiredArgsConstructor
public class RoomServiceController {
    private final RoomServiceService roomServiceService;
    private final AuthenticateHandler authenticateHandler;
    @Autowired
    JwtUtils jwtUtils;
    static String E401 = "Unauthorized";
    static String E404 = "Not Found";
    static String E400 = "Bad Request";


    @GetMapping("/room/{roomId}")
    @ApiOperation("Find all by room id")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> getAllRoomServiceByRoom(@PathVariable("roomId") String roomId) {
        List<RoomServiceEntity> roomServiceEntities = roomServiceService.getAllRoomSerivceByRoomID(roomId);
        return new ResponseEntity<>(roomServiceEntities, HttpStatus.OK);
    }

    @GetMapping("/all")
    @ApiOperation("Get all")
    @PreAuthorize("hasRole('ROLE_ADMIN') || hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> getAllRoomService(HttpServletRequest req)
    {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(req);
            List<RoomServiceEntity> list = roomServiceService.getAllRoomService();
            return new ResponseEntity<>(list,HttpStatus.OK);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("Find by id")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> getRoomServiceById(@PathVariable String id) {
        RoomServiceEntity roomServiceEntity = roomServiceService.findRoomServiceById(id);
        if (roomServiceEntity == null) {
            MessageResponse messageResponse = new MessageResponse("Bad Request", "ROOM_SERVICE_ID_NOT_FOUND", "Room service id not found");
            return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("content", roomServiceEntity);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("")
    @ApiOperation("Add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> addRoomService(HttpServletRequest req, @Valid @RequestParam String[] serviceName) {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            List<RoomServiceEntity> list = new ArrayList<>();
            for (String name : serviceName)
            {
                if (roomServiceService.findRoomServiceByName(name)==null) {
                    RoomServiceEntity roomService = new RoomServiceEntity();
                    roomService.setRoomServiceName(name);
                    list.add(roomService);
                }
                else
                {
                    return new ResponseEntity<>(new ErrorResponse(E400, "SERVICE_ALREADY_EXISTS", "There an service exist with name" + name), HttpStatus.BAD_REQUEST);
                }
            }
            if (!list.isEmpty()) {
                list = roomServiceService.addRoomService(list);
            }
            else
            {
                return new ResponseEntity<>(new ErrorResponse(E400, "LIST_SERVICE_IS_EMPTY", "List service is empty" ), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PatchMapping("/{id}")
    @ApiOperation("Update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> updateRoomService(@Valid @RequestBody UpdateRoomServiceRequest updateRoomServiceRequest, HttpServletRequest req, @PathVariable("id") String id) {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(req);
            RoomServiceEntity roomService = roomServiceService.findRoomServiceById(id);
            if (roomService == null) {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "ROOM_SERVICE_ID_NOT_FOUND", "Room service id not found");
                return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
            }
            roomService = RoomServiceMapping.updateRoomServicetoEntity(updateRoomServiceRequest, roomService);
            roomService = roomServiceService.saveRoomService(roomService);
            Map<String, Object> map = new HashMap<>();
            map.put("content", roomService);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
    @DeleteMapping("/{id}")
    @ApiOperation("Delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteRoomSerivceById(@PathVariable("id") String id, HttpServletRequest httpServletRequest) {
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        RoomServiceEntity roomService = roomServiceService.findRoomServiceById(id);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            if (roomService == null) {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "ROOM_SERVICE_ID_NOT_FOUND", "Room service id not found");
                return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
            }
            else {
                    roomServiceService.deleteById(roomService.getId());
                    return new ResponseEntity<>(HttpStatus.OK);
                }
        } else throw new BadCredentialsException("access token is missing");
    }

}

