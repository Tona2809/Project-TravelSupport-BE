package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.repository.RoomRepository;
import com.hcmute.hotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    @Override
    public RoomEntity addRoom(RoomEntity room) {
        return roomRepository.save(room);
    }

    @Override
    public RoomEntity findRoomById(String id) {
        Optional<RoomEntity> room = roomRepository.findById(id);
        return room.isEmpty() ? null : room.get();
    }

    @Override
    public List<RoomEntity> findRoomsByStayId(String stayId) {
        List<RoomEntity> list = roomRepository.findAllByStayId(stayId);
        return list;
    }

    @Override
    public List<RoomEntity> findRoomByListId(List<String> listId) {
        List<RoomEntity> list = roomRepository.findByListId(listId);
        return list;
    }

    @Override
    public Map<String,Integer> getAvailableRoom(LocalDateTime checkinDate, LocalDateTime checkoutDate, int guestNumber, String stayId, int flexibleNumbers) {
        List<Object> result = roomRepository.getAvailableRoom(checkinDate, checkoutDate,guestNumber,stayId, flexibleNumbers);
        return ResultToMap(result);
    }

    @Override
    public Map<String, Integer> getCurrentAvailableRoom(LocalDateTime checkinDate, LocalDateTime checkoutDate, String stayId) {
        List<Object> result = roomRepository.getCurrentAvailableRoom(checkinDate, checkoutDate,stayId);
        return ResultToMap(result);
    }

    private Map<String, Integer> ResultToMap(List<Object> result) {
        Map<String, Integer> roomMap = new HashMap<>();

        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            BigDecimal countBigDecimal = (BigDecimal) row[1];
            if (countBigDecimal == null)
            {
                countBigDecimal= BigDecimal.valueOf(0);
            }
            String roomId = (String) row[0];
            Integer count = countBigDecimal.intValue();

            roomMap.put(roomId, count);
        }
        return roomMap;
    }


}
