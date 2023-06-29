package com.hcmute.hotel.model.payload.response;

import com.hcmute.hotel.model.entity.StayEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaySearchResponse {
   private StayEntity stay;
   private int price;
   private int maxPeople;

    public StaySearchResponse() {
    }
}
