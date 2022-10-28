package com.hcmute.hotel.controller;

import com.hcmute.hotel.constants.ApplicationConstants;
import com.hcmute.hotel.mapping.ProvinceMapping;
import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.payload.request.Province.AddNewProvinceRequest;
import com.hcmute.hotel.model.payload.request.Province.UpdateProvinceRequest;
import com.hcmute.hotel.model.payload.response.DataResponse;
import com.hcmute.hotel.model.payload.response.MessageResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.ProvinceService;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.websocket.OnClose;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@ComponentScan
@RestController
@RequestMapping("/api/province")
@RequiredArgsConstructor
public class ProvinceController {
    private final ProvinceService provinceService;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("")
    @ApiOperation("Find all")
    public DataResponse getAllProvinces() {
        List<ProvinceEntity> provinceEntityList = provinceService.getAllProvinces();
        return new DataResponse(provinceEntityList);
    }

    @GetMapping("/{id}")
    @ApiOperation("Find by id")
    public ResponseEntity<Object> getProvinceById(@PathVariable int id) {
        ProvinceEntity provinceEntity = provinceService.getProvinceById(id);
        if (provinceEntity == null) {
            MessageResponse messageResponse = new MessageResponse("Bad Request", "PROVINCE_ID_NOT_FOUND", "Province id not found");
            return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("content", provinceEntity);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("")
    @ApiOperation("Create")
    public ResponseEntity<Object> addProvince(@RequestBody @Valid AddNewProvinceRequest addNewProvinceRequest, BindingResult result, HttpServletRequest httpServletRequest) throws Exception {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null, result);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            ProvinceEntity province = ProvinceMapping.addProvinceToEntity(addNewProvinceRequest);
            boolean isExisted = provinceService.findByName(addNewProvinceRequest.getName());
            if (isExisted == false) {
                province = provinceService.saveProvince(province);
                Map<String, Object> map = new HashMap<>();
                map.put("content", province);
                return new ResponseEntity<>(map, HttpStatus.OK);
            }
            else {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "PROVINCE_NAME_EXISTED", "Province name has been used");
                return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
                }
            } else throw new BadCredentialsException("access token is missing");
        }



    @PatchMapping("/{id}")
    @ApiOperation("Update")
    public ResponseEntity<Object> updateProvince(@Valid @RequestBody UpdateProvinceRequest updateProvinceRequest, BindingResult result, HttpServletRequest httpServletRequest, @PathVariable("id") int id) throws Exception {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null, result);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            ProvinceEntity province = provinceService.getProvinceById(id);
            if (province == null) {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "PROVINCE_ID_NOT_FOUND", "Province id not found");
                return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
            }
            boolean isExisted = provinceService.findByNameAndId(updateProvinceRequest.getName(), id);
            if (!isExisted) {
                province = ProvinceMapping.updateProvinceToEntity(updateProvinceRequest, province);
                province = provinceService.saveProvince(province);
                Map<String, Object> map = new HashMap<>();
                map.put("content", province);
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "PROVINCE_NAME_EXISTED", "Province name has been used");
                return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
            }
        } else throw new BadCredentialsException("access token is missing");
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete")
    public ResponseEntity<Object> deleteProvinceById(@PathVariable("id") int id, HttpServletRequest httpServletRequest) {
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        ProvinceEntity province = provinceService.getProvinceById(id);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            ProvinceEntity provinceEntity = provinceService.getProvinceById(id);
            if (provinceEntity == null) {
                MessageResponse messageResponse = new MessageResponse("Bad Request", "PROVINCE_ID_NOT_FOUND", "Province id not found");
                return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
            }
            else {
                for (StayEntity stay : province.getStay()) {
                    stay.setProvince(null);
                }
                provinceService.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } else throw new BadCredentialsException("access token is missing");
    }
}
