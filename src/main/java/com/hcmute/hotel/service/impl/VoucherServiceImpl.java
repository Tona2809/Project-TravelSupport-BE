package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.VoucherEntity;
import com.hcmute.hotel.repository.VoucherRepository;
import com.hcmute.hotel.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
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
    public List<VoucherEntity> getAllVouchersByStay(String stayid) {
        List<VoucherEntity> voucherEntityList = voucherRepository.getAllVoucherByStay(stayid);
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
    public boolean findByNameAndId(String name, String id) {
        List<VoucherEntity> voucherEntityList = voucherRepository.findByNameAndId(name, id);
        if (voucherEntityList.size() == 0)
            return false;
        else return true;
    }
}
