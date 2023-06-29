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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthenticateHandler authenticateHandler;
    static String E401 = "Unauthorized";
    static String E404 = "Not Found";
    static String E400 = "Bad Request";


    @GetMapping("/room/{roomId}")
    @ApiOperation("Find all by room id")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public DataResponse getAllRoomService(@PathVariable("roomId") String roomId) {
        List<RoomServiceEntity> roomServiceEntities = roomServiceService.getAllRoomSerivceByRoomID(roomId);
        return new DataResponse(roomServiceEntities);
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
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> addRoomService(HttpServletRequest req, @Valid @RequestBody AddNewRoomServiceRequest addNewRoomServiceRequest) {
        try {
            RoomServiceEntity roomServiceEntity = RoomServiceMapping.addRoomServicetoEntity(addNewRoomServiceRequest);
            roomServiceEntity = roomServiceService.addRoomService(roomServiceEntity);
            return new ResponseEntity<>(roomServiceEntity, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PatchMapping("/{id}")
    @ApiOperation("Update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> updateRoomService(@Valid @RequestBody UpdateRoomServiceRequest updateRoomServiceRequest, BindingResult result, HttpServletRequest httpServletRequest, @PathVariable("id") String id) throws Exception {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null, result);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            RoomServiceEntity roomService = roomServiceService.findRoomServiceById(id);
            if (roomService == null) {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "ROOM_SERVICE_ID_NOT_FOUND", "Room service id not found");
                return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
            }
            roomService = RoomServiceMapping.updateRoomServicetoEntity(updateRoomServiceRequest, roomService);
            roomService = roomServiceService.addRoomService(roomService);
            Map<String, Object> map = new HashMap<>();
            map.put("content", roomService);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else throw new BadCredentialsException("access token is missing");
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
    @PostMapping(value = "/image/{id}",consumes = {"multipart/form-data"})
    @ApiOperation("Add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> addRoomServiceImage(@PathVariable String id, @RequestPart MultipartFile file)
    {
        RoomServiceEntity roomService =  roomServiceService.findRoomServiceById(id);
        if (roomService==null)
        {
            MessageResponse messageResponse = new MessageResponse("Not found", "ROOM_SERVICE_ID_NOT_FOUND", "Room service id not found");
            return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
        }
        try
        {
            roomService=roomServiceService.addImage(file,roomService);
            return new ResponseEntity<>(roomService,HttpStatus.OK);
        } catch (FileNotImageException fileNotImageException)
        {
            return new ResponseEntity<>(new ErrorResponse("Unsupported Media Type","FILE_NOT_IMAGE",fileNotImageException.getMessage()),HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        catch (RuntimeException runtimeException)
        {
            return new ResponseEntity<>(new ErrorResponse("Bad request","FAIL_TO_UPLOAD",runtimeException.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/image/{provinceid}")
    @ApiOperation("Delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteImage(@PathVariable("roomServiceId") String id)
    {
        RoomServiceEntity roomService =  roomServiceService.findRoomServiceById(id);
        if (roomService==null)
        {
            MessageResponse messageResponse = new MessageResponse("Not found", "ROOM_SERVICE_ID_NOT_FOUND", "Room service id not found");
            return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
        }
        roomService.setImgLink(null);
        roomService = roomServiceService.addRoomService(roomService);
        return new ResponseEntity<>(roomService,HttpStatus.OK);
    }

}

