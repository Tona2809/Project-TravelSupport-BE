package com.hcmute.hotel.controller;

import com.hcmute.hotel.common.*;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.response.PagingResponse;
import com.hcmute.hotel.service.StayService;
import com.hcmute.hotel.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ComponentScan
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final StayService stayService;
    @GetMapping("/searchUser")
    @ApiOperation("Search user by Criteria")
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
    @GetMapping("/searchStay")
    @ApiOperation("Search Stay by Criteria")
    public ResponseEntity<Object> searchByCriteria(
            @RequestParam(required = false) String provinceId,
            @RequestParam(defaultValue = "0",name = "pageIndex") int page,
            @RequestParam(defaultValue = "5",name = "pageSize") int size,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false,defaultValue = "") LocalDateTime checkInDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false,defaultValue = "") LocalDateTime checkOutDate,
            @RequestParam(defaultValue = "5") int maxPeople,
            @RequestParam(defaultValue = "1000") int maxPrice,
            @RequestParam(defaultValue = "0") int minPrice,
            @RequestParam(required = false) String[] amenitiesId,
            @RequestParam(defaultValue = "PRICE") StaySortEnum sort,
            @RequestParam(defaultValue = "DESCENDING") OrderByEnum order,
            @RequestParam(defaultValue = "NULL") StayStatus status,
            @RequestParam(required = false)boolean hidden)
    {
        Page<StayEntity> stayPage = stayService.searchByCriteria(provinceId,minPrice,maxPrice,checkInDate,checkOutDate,status.getName(),hidden,maxPeople,page,size,sort.getName(),order.getName());
        List<StayEntity> listStay = stayPage.toList();
        PagingResponse pagingResponse = new PagingResponse();
        Map<String,Object> map = new HashMap<>();
        List<Object> Result = Arrays.asList(listStay.toArray());
        pagingResponse.setTotalPages(stayPage.getTotalPages());
        pagingResponse.setEmpty(listStay.size()==0);
        pagingResponse.setFirst(page==0);
        pagingResponse.setLast(page == stayPage.getTotalPages()-1);
        pagingResponse.getPageable().put("pageNumber",page);
        pagingResponse.getPageable().put("pageSize",size);
        pagingResponse.setSize(size);
        pagingResponse.setNumberOfElements(listStay.size());
        pagingResponse.setTotalElements((int) stayPage.getTotalElements());
        pagingResponse.setContent(Result);
        return new ResponseEntity<>(pagingResponse ,HttpStatus.OK);
    }
    @GetMapping("/getReportMonthly")
    @ApiOperation("Get Sale Report Monthly")
    public ResponseEntity<Object> getSaleReportMonthly(@RequestParam(defaultValue = "0",name = "pageIndex") int page,
                                                       @RequestParam(defaultValue = "5",name = "pageSize") int size,
                                                       @RequestParam(defaultValue = "1") String month)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/getReportByDate")
    @ApiOperation("Get Sale Report by Date")
    public ResponseEntity<Object> getSaleReportByDate(@RequestParam(defaultValue = "0",name = "pageIndex") int page,
                                                       @RequestParam(defaultValue = "5",name = "pageSize") int size,
                                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                          @RequestParam(required = false,defaultValue = "") LocalDateTime startDate,
                                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                          @RequestParam(required = false,defaultValue = "") LocalDateTime endDate)
    {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
