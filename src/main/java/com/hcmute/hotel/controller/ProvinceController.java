package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.MethodArgumentNotValidException;
import com.hcmute.hotel.mapping.ProvinceMapping;
import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.payload.SuccessResponse;
import com.hcmute.hotel.model.payload.request.Province.AddNewProvinceRequest;
import com.hcmute.hotel.model.payload.request.Province.UpdateProvinceRequest;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

@ComponentScan
@RestController
@RequestMapping("/api/province")
@RequiredArgsConstructor
public class ProvinceController {
    private final ProvinceService provinceService;

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/getall")
    public ResponseEntity<SuccessResponse> getAllProvinces() {
        List<ProvinceEntity> provinceEntityList = provinceService.getAllProvinces();
        if (provinceEntityList.size() == 0) {
            SuccessResponse response = new SuccessResponse();
            response.setStatus(HttpStatus.FOUND.value());
            response.setMessage("List province is empty");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.FOUND);
        }
        SuccessResponse response = new SuccessResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Query Successfully");
        response.setSuccess(true);
        response.getData().put("List province:", provinceEntityList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse> getProvinceById(@PathVariable int id) {
        ProvinceEntity provinceEntity = provinceService.getProvinceById(id);
        if (provinceEntity == null) {
            SuccessResponse response = new SuccessResponse();
            response.setStatus(HttpStatus.FOUND.value());
            response.setMessage("province is not found");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.FOUND);
        }
        SuccessResponse response = new SuccessResponse();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Query Successfully");
        response.setSuccess(true);
        response.getData().put("province:", provinceEntity);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<SuccessResponse> addProvince(@RequestBody @Valid AddNewProvinceRequest addNewProvinceRequest, BindingResult result, HttpServletRequest httpServletRequest) throws Exception {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(result);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        SuccessResponse response = new SuccessResponse();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            ProvinceEntity province = ProvinceMapping.addProvinceToEntity(addNewProvinceRequest);
            boolean isExisted = provinceService.findByName(addNewProvinceRequest.getName());
            if(isExisted == false) {
                provinceService.saveProvince(province);
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Save province successfully");
                response.setSuccess(true);
                response.getData().put("province", province);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setMessage("province name is existed");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } else throw new BadCredentialsException("access token is missing");
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<SuccessResponse> updateProvince(@Valid @RequestBody UpdateProvinceRequest updateProvinceRequest, BindingResult result, HttpServletRequest httpServletRequest, @PathVariable("id") int id) throws Exception {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(result);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        SuccessResponse response = new SuccessResponse();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            ProvinceEntity province = provinceService.getProvinceById(id);
            if (province == null) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Can't find province with id " + id);
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            boolean isExisted = provinceService.findByNameAndId(updateProvinceRequest.getName(), id);
            if(!isExisted) {
                province = ProvinceMapping.updateProvinceToEntity(updateProvinceRequest, province);
                provinceService.saveProvince(province);
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("Update province successfully");
                response.setSuccess(true);
                response.getData().put("province", province);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setMessage("province name is existed");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } else throw new BadCredentialsException("access token is missing");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<SuccessResponse> deleteProvinceById(@PathVariable("id") int id, HttpServletRequest httpServletRequest) {
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        ProvinceEntity province=provinceService.getProvinceById(id);
        SuccessResponse response = new SuccessResponse();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());
            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            for (StayEntity stay : province.getStay())
            {
                stay.setProvince(null);
            }
            provinceService.deleteById(id);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Delete province successfully");
            response.setSuccess(true);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else throw new BadCredentialsException("access token is missing");
    }
}
