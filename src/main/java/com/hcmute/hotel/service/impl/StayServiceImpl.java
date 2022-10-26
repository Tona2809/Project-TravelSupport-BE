package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.repository.StayRepository;
import com.hcmute.hotel.service.StayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StayServiceImpl implements StayService {
    private final StayRepository stayRepository;
    @Override
    public StayEntity saveStay(StayEntity stay) {
        return stayRepository.save(stay);
    }

    @Override
    public List<StayEntity> getAllStay() {
        return stayRepository.findAll();
    }

    @Override
    public StayEntity getStayById(String id) {
        Optional<StayEntity> stayEntity = stayRepository.findById(id);
        if (stayEntity.isEmpty())
            return null;
        return stayEntity.get();
    }

    @Override
    public void deleteStay(String id) {
        stayRepository.deleteById(id);
    }

    @Override
    public List<StayEntity> getStayByUser(UserEntity user) {
        return stayRepository.findAllByHost(user);
    }
}
