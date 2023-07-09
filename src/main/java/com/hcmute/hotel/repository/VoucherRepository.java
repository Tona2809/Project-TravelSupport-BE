package com.hcmute.hotel.repository;

import com.hcmute.hotel.model.entity.ProvinceEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.entity.VoucherEntity;
import com.hcmute.hotel.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.LocalDateTime;
import java.util.List;

@EnableJpaRepositories
public interface VoucherRepository extends JpaRepository<VoucherEntity, String> {
    List<VoucherEntity> findByName(String name);
    @Query(value = "select * from voucher where name like %?%  and id <> ?", nativeQuery = true)
    List<VoucherEntity> findByNameAndId(String name, String id);

    @Query(value ="select * from voucher where room_id = ?",nativeQuery = true)
    List<VoucherEntity> getAllVoucherByRoom(String stayId);

    @Query(value = "select v.* from voucher v inner join user_voucher uv on v.id = uv.voucher_id where v.room_id = ?2 and uv.user_id = ?1 and v.expire_at > ?3", nativeQuery = true)
    List<VoucherEntity> getAllVoucherByUser(String userId, String roomId, LocalDateTime localDateTime);

    @Query(value = "Select voucher.* from voucher inner join rooms on voucher.room_id = rooms.id where rooms.stay_id= ?1 and voucher.is_hidden = false and expire_at > ?2", nativeQuery = true)
    List<VoucherEntity> getAllVoucherByStay(String stayId, LocalDateTime localDateTime);

}
