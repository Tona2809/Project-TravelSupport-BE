package com.hcmute.hotel.model.payload.request.Province;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@Setter
@Getter
public class AddNewProvinceRequest {

    @NotEmpty(message = "Province name can not be empty")
    private String name;


}
