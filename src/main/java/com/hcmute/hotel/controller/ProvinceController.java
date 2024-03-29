package com.hcmute.hotel.controller;

import com.hcmute.hotel.constants.ApplicationConstants;
import com.hcmute.hotel.handler.FileNotImageException;
import com.hcmute.hotel.mapping.ProvinceMapping;
import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.payload.request.Province.AddNewProvinceRequest;
import com.hcmute.hotel.model.payload.request.Province.UpdateProvinceRequest;
import com.hcmute.hotel.model.payload.request.User.AddUserInfoRequest;
import com.hcmute.hotel.model.payload.response.DataResponse;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.model.payload.response.MessageResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.ProvinceService;
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
@RequestMapping("/api/province")
@RequiredArgsConstructor
public class ProvinceController {
    private final ProvinceService provinceService;


    private final JwtUtils jwtUtils;

    @GetMapping("")
    @ApiOperation("Find all")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public DataResponse getAllProvinces() {
        List<ProvinceEntity> provinceEntityList = provinceService.getAllProvinces();
        return new DataResponse(provinceEntityList);
    }

    @GetMapping("/{id}")
    @ApiOperation("Find by id")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> getProvinceById(@PathVariable String id) {
        ProvinceEntity provinceEntity = provinceService.getProvinceById(id);
        if (provinceEntity == null) {
            MessageResponse messageResponse = new MessageResponse("Bad Request", "PROVINCE_ID_NOT_FOUND", "Province id not found");
            return new ResponseEntity<>(messageResponse, HttpStatus.NOT_FOUND);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("content", provinceEntity);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping(value = "",consumes = {"multipart/form-data"})
    @ApiOperation("Create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> addProvince(@RequestPart String name, BindingResult result, HttpServletRequest httpServletRequest, @RequestPart MultipartFile file) throws Exception {
        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null, result);
        }
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String accessToken = authorizationHeader.substring("Bearer ".length());

            if (jwtUtils.validateExpiredToken(accessToken) == true) {
                throw new BadCredentialsException("access token is  expired");
            }
            AddNewProvinceRequest addNewProvinceRequest = new AddNewProvinceRequest();
            addNewProvinceRequest.setName(new String(name.getBytes("ISO-8859-1"), "UTF-8"));
            ProvinceEntity province = ProvinceMapping.addProvinceToEntity(addNewProvinceRequest);
            boolean isExisted = provinceService.findByName(addNewProvinceRequest.getName());
            if (isExisted == false) {
                province=provinceService.addImage(file,province);
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> updateProvince(@Valid @RequestBody UpdateProvinceRequest updateProvinceRequest, BindingResult result, HttpServletRequest httpServletRequest, @PathVariable("id") String id) throws Exception {
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteProvinceById(@PathVariable("id") String id, HttpServletRequest httpServletRequest) {
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
                if (province.getStay().size()!=0)
                {
                    MessageResponse messageResponse = new MessageResponse("Bad Request", "PROVINCE_STAY_IS_NOT_EMPTY", "Province still have stays");
                    return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
                }
                else
                {
                    provinceService.deleteById(province.getId());
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            }
        } else throw new BadCredentialsException("access token is missing");
    }
    @PostMapping(value = "/image/{id}",consumes = {"multipart/form-data"})
    @ApiOperation("Add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> addProviceImage(@PathVariable String id, @RequestPart MultipartFile file)
    {
        ProvinceEntity province =  provinceService.getProvinceById(id);
        if (province==null)
        {
            MessageResponse messageResponse = new MessageResponse("Bad request", "PROVINCE_ID_NOT_FOUND", "Province id not found");
            return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
        }
        try
        {
            province=provinceService.addImage(file,province);
            return new ResponseEntity<>(province,HttpStatus.OK);
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
    public ResponseEntity<Object> deleteImage(@PathVariable("provinceid") String id)
    {
        ProvinceEntity province =  provinceService.getProvinceById(id);
        if (province==null)
        {
            MessageResponse messageResponse = new MessageResponse("Bad request", "PROVINCE_ID_NOT_FOUND", "Province id not found");
            return new ResponseEntity<>(messageResponse, HttpStatus.BAD_REQUEST);
        }
        province.setImgLink(null);
        provinceService.saveProvince(province);
        return new ResponseEntity<>(province,HttpStatus.OK);
    }
}
