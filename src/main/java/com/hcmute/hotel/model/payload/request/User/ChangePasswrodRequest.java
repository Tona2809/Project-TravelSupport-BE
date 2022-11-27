package com.hcmute.hotel.model.payload.request.User;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@Setter
@Getter
public class ChangePasswrodRequest {
    private String oldPassword;
    @NotEmpty(message = "PASSWORD_CAN_NOT_BE_NULL")
    @Size(min = 8, max = 32, message = "PASSWORD_HAS_TO_FROM_8_TO_32_CHARACTERS_INCLUDE_WORD_AND_NUMBER")
    private String newPassword;
    private String confirmPassword;

}
