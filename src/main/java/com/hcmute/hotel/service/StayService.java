package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.StayImageEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Service
public interface StayService {
    StayEntity saveStay(StayEntity stay);
    List<StayEntity> getAllStay();
    StayEntity getStayById(String id);
    void deleteStay(String id);

    List<StayEntity> getStayByUser(UserEntity user);
    List<StayImageEntity> addStayImg(MultipartFile[] files, StayEntity stay);
    StayImageEntity findImgById(String id);
    void DeleteImg(String id);
    List<StayEntity> pagingByProvince(String provinceId,int pageNo,int pageSize);
    List<StayEntity> findAllStayByProvince(ProvinceEntity province);

    Page<StayEntity> searchByCriteria(String provinceId, int minPrice, int maxPrice, LocalDateTime checkinDate, LocalDateTime checkoutDate, int maxPeople, int pageNo, int pageSize, String sort, String orderBy);
}
