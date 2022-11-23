package com.hcmute.hotel.model.payload.request.User;

import com.hcmute.hotel.common.GenderEnum;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Setter
@Getter
public class AddUserInfoRequest {
    @NotEmpty(message = "Full name cannot be empty")
    String fullName;
    @NotEmpty(message = "Phone cannot be empty")
    String phone;
}
