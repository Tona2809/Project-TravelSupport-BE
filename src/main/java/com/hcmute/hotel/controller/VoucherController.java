package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.mapping.ProvinceMapping;
import com.hcmute.hotel.mapping.StayMapping;
import com.hcmute.hotel.mapping.VoucherMapping;
import com.hcmute.hotel.model.entity.*;
import com.hcmute.hotel.model.payload.request.Province.UpdateProvinceRequest;
import com.hcmute.hotel.model.payload.request.Stay.AddNewStayRequest;
import com.hcmute.hotel.model.payload.request.Voucher.AddVoucherRequest;
import com.hcmute.hotel.model.payload.request.Voucher.UpdateVoucherRequest;
import com.hcmute.hotel.model.payload.response.DataResponse;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.model.payload.response.MessageResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.RoomService;
import com.hcmute.hotel.service.StayService;
import com.hcmute.hotel.service.VoucherService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@ComponentScan
@RestController
@RequestMapping("/api/voucher")
@RequiredArgsConstructor
@EnableWebSecurity
public class VoucherController {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthenticateHandler authenticateHandler;
    private final VoucherService voucherService;
    private final StayService stayService;

    private final RoomService roomService;
    static String E401 = "Unauthorized";
    static String E404 = "Not Found";
    static String E400 = "Bad Request";

    @PostMapping("")
    @ApiOperation("Add")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> addVoucher(HttpServletRequest req, @Valid @RequestBody AddVoucherRequest addNewVoucherRequest) {
        UserEntity user;
        try {
            user = authenticateHandler.authenticateUser(req);
            VoucherEntity voucher = VoucherMapping.addReqToEntity(addNewVoucherRequest);
            RoomEntity room = roomService.findRoomById(addNewVoucherRequest.getRoomId());
            if (room == null) {
                return new ResponseEntity<>(new ErrorResponse(E404, "ROOM_NOT_FOUND", "Can't find Room with id provided"), HttpStatus.NOT_FOUND);
            } else {
                voucher.setRoom(room);
            }

            voucherService.addVoucher(voucher);
            return new ResponseEntity<>(voucher, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("")
    @ApiOperation("Find all")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public DataResponse getAllVouchers() {
        List<VoucherEntity> voucherEntityList = voucherService.getAllVouchers();
        return new DataResponse(voucherEntityList);
    }

    @GetMapping("/room/{roomId}")
    @ApiOperation("Find vouchers by room id")
    public ResponseEntity<Object> getVouchersByStayId(@PathVariable("roomId") String roomId) {
        RoomEntity room = roomService.findRoomById(roomId);
        if(room == null) {
            MessageResponse messageResponse = new MessageResponse("Bad Request", "STAY_ID_NOT_FOUND", "Stay id not found");
            return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
        } else {
            List<VoucherEntity> voucherEntityList = voucherService.getAllVouchersByRoom(roomId);
            return new ResponseEntity<>(voucherEntityList, HttpStatus.OK);
        }
    }
    @GetMapping("/getByIds")
    @ApiOperation("Find by id")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> getVoucherById(@RequestParam String[] ids) {
        List<VoucherEntity> list = new ArrayList<>();
        for (String id : ids) {
            VoucherEntity voucherEntity = voucherService.getVoucherById(id);
            if (voucherEntity == null) {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "VOUCHER_ID_NOT_FOUND", "Voucher id not found");
                return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
            }
            list.add(voucherEntity);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    @PatchMapping("/{id}")
    @ApiOperation("Update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> updateVoucher(@Valid @RequestBody UpdateVoucherRequest updateVoucherRequest, BindingResult result, HttpServletRequest httpServletRequest, @PathVariable("id") String id) throws Exception {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null, result);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            VoucherEntity voucher = voucherService.getVoucherById(id);
            if (voucher == null) {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "VOUCHER_ID_NOT_FOUND", "Voucher id not found");
                return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
            }

                voucher = VoucherMapping.updateVoucherToEntity(updateVoucherRequest,voucher);
                if(updateVoucherRequest.getQuantity() < voucher.getRemainingQuantity()) {
                    MessageResponse messageResponse = new MessageResponse("Bad Request", "QUANTITY_CAN_NOT_BE_SMALLER", "Quantity can not be smaller");
                    return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
                }
                if(updateVoucherRequest.getQuantity() >= voucher.getRemainingQuantity()) {
                    voucher.setQuantity(updateVoucherRequest.getQuantity());
                    voucher = voucherService.addVoucher(voucher);
                    Map<String, Object> map = new HashMap<>();
                    map.put("content", voucher);
                    return new ResponseEntity<>(map, HttpStatus.OK);
                }
                else {
                    MessageResponse messageResponse = new MessageResponse("Bad Request", "VOUCHER_QUANTITY_NOT_VALID", "Voucher quantity can not be smaller remain quantity");
                    return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
                }
        } else throw new BadCredentialsException("access token is missing");
    }
    @DeleteMapping("/{id}")
    @ApiOperation("Delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteVoucherById(@PathVariable("id") String id, HttpServletRequest httpServletRequest) {
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        VoucherEntity voucher = voucherService.getVoucherById(id);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            if (voucher == null) {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "VOUCHER_ID_NOT_FOUND", "Voucher id not found");
                return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
            }
            else {
                voucherService.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } else throw new BadCredentialsException("access token is missing");
    }
    @GetMapping("/userVoucher/{roomId}")
    @ApiOperation("Get Voucher by user and stay id")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> getVoucherByUser(HttpServletRequest req,@PathVariable("roomId") String roomId)
    {
        UserEntity user;
        RoomEntity room = roomService.findRoomById(roomId);
        if(room == null) {
            return new ResponseEntity<>(new ErrorResponse(E404, "ROOM_NOT_FOUND", "Can't find room"), HttpStatus.NOT_FOUND);
        }
        try {
            user = authenticateHandler.authenticateUser(req);
            List<VoucherEntity> voucher = voucherService.getAllVoucherByUser(user.getId(), roomId);
            return new ResponseEntity<>(voucher,HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/stay/{stayId}")
    @ApiOperation("Get Voucher by stay id")
    public ResponseEntity<Object> getVoucherByStay(HttpServletRequest req,@PathVariable("stayId") String stayId)
    {
        UserEntity user;
        StayEntity stay = stayService.getStayById(stayId);
        if(stay == null) {
            return new ResponseEntity<>(new ErrorResponse(E404, "STAY_NOT_FOUND", "Can't find stay"), HttpStatus.NOT_FOUND);
        }
            List<VoucherEntity> voucher = voucherService.getAllVoucherByStay(stayId);
            return new ResponseEntity<>(voucher,HttpStatus.OK);
    }
    @PatchMapping("/active/{voucherId}")
    @ApiOperation("Active Voucher")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public ResponseEntity<Object> activeVoucher(@PathVariable("voucherId") String voucherId, HttpServletRequest req)
    {
        UserEntity user;
        try
        {
            user = authenticateHandler.authenticateUser(req);
            VoucherEntity voucher = voucherService.getVoucherById(voucherId);
            if (voucher==null || voucher.getExpirationDate().compareTo(LocalDateTime.now())<0)
            {
                return new ResponseEntity<>(new ErrorResponse(E404, "VOUCHERS_NOT_FOUND", "Voucher not found with id or it expired"), HttpStatus.NOT_FOUND);
            }
            voucher.setHidden(false);
            voucher = voucherService.addVoucher(voucher);
            return new ResponseEntity<>(voucher,HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new ErrorResponse(E401, "UNAUTHORIZED", "Unauthorized, please login again"), HttpStatus.UNAUTHORIZED);
        }
    }

}

