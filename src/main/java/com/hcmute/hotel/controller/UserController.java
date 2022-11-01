package com.hcmute.hotel.controller;

import com.hcmute.hotel.common.AppUserRole;
import com.hcmute.hotel.common.UserStatus;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.response.PagingResponse;
import com.hcmute.hotel.security.JWT.JwtUtils;
import com.hcmute.hotel.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ComponentScan
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/search")
    @ApiOperation("Search by Criteria")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> search(@RequestParam(defaultValue = "") String keyword, @RequestParam(name = "status", required = false) UserStatus userstatus, @RequestParam(name = "role", required = false) AppUserRole userRole, @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "5") int size) {
        List<UserEntity> userEntities = userService.search(keyword, userstatus, userRole, page, size);
        int totalElements = userEntities.size();
        int totalPage = totalElements % size == 0 ? totalElements / size : totalElements / size + 1;
        PagingResponse pagingResponse = new PagingResponse();
        Map<String, Object> map = new HashMap<>();
        List<Object> Result = Arrays.asList(userEntities.toArray());
        pagingResponse.setTotalPages(totalPage);
        pagingResponse.setEmpty(userEntities.size() == 0);
        pagingResponse.setFirst(page == 0);
        pagingResponse.setLast(page == totalPage - 1);
        pagingResponse.getPageable().put("pageNumber", page);
        pagingResponse.getPageable().put("pageSize", size);
        pagingResponse.setSize(size);
        pagingResponse.setNumberOfElements(userEntities.size());
        pagingResponse.setTotalElements(totalElements);
        pagingResponse.setContent(Result);
        return new ResponseEntity<>(pagingResponse, HttpStatus.OK);
    }
}
