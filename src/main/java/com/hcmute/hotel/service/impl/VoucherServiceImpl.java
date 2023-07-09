package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.*;
import com.hcmute.hotel.repository.UserRepository;
import com.hcmute.hotel.repository.VoucherRepository;
import com.hcmute.hotel.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    @Override
    public VoucherEntity addVoucher(VoucherEntity entity) {
        return voucherRepository.save(entity);
    }

    @Override
    public List<VoucherEntity> getAllVouchers() {
        List<VoucherEntity> voucherEntityList = voucherRepository.findAll();
        return voucherEntityList;
    }

    @Override
    public List<VoucherEntity> getAllVouchersByRoom(String roomId) {
        List<VoucherEntity> voucherEntityList = voucherRepository.getAllVoucherByRoom(roomId);
        return voucherEntityList;
    }

    @Override
    public VoucherEntity getVoucherById(String id) {
        Optional<VoucherEntity> voucherEntity = voucherRepository.findById(id);
        if (voucherEntity.isEmpty())
            return null;
        return voucherEntity.get();
    }

    @Override
    public void deleteById(String id) {
        voucherRepository.deleteById(id);
    }

    @Override
    public boolean findByName(String name) {
        List<VoucherEntity> voucherEntityList = voucherRepository.findByName(name);
        if (voucherEntityList.size() == 0)
            return false;
        else return true;
    }

    @Override
    public VoucherEntity userVoucher(UserEntity user, VoucherEntity voucher) {
      Set<UserEntity> users = new HashSet<>();
      users.add(user);
      voucher.setUsers(users);
      return voucherRepository.save(voucher);
    }

    @Override
    public boolean findByNameAndId(String name, String id) {
        List<VoucherEntity> voucherEntityList = voucherRepository.findByNameAndId(name, id);
        if (voucherEntityList.size() == 0)
            return false;
        else return true;
    }

    @Override
    public List<VoucherEntity> getAllVoucherByUser(String userId, String roomId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        List<VoucherEntity> voucherEntityList = voucherRepository.getAllVoucherByUser(userId, roomId, localDateTime);
        return voucherEntityList;
    }

    @Override
    public List<VoucherEntity> getAllVoucherByStay(String stayId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        List<VoucherEntity> voucherEntityList = voucherRepository.getAllVoucherByStay(stayId,localDateTime);
        return voucherEntityList;
    }
}
