package com.hcmute.hotel.model.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
public class DataResponse {
    private Object content;


    public DataResponse(Object data) {
        this.content = data;
    }


}
