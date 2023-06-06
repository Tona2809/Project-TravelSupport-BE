package com.hcmute.hotel.model.payload.request.Voucher;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@Setter
@Getter
public class UpdateVoucherRequest {
    final String EMPTY_MESSAGE ="cannot be empty";
    @NotEmpty(message = "name"+EMPTY_MESSAGE)
    String name;
    @NotNull(message = "Voucher discount"+EMPTY_MESSAGE)
    int discount;
    LocalDateTime expiredDate;
    @NotEmpty(message ="stay id" +EMPTY_MESSAGE)
    String stayId;
    @NotNull(message ="quantity"+EMPTY_MESSAGE)
    int quantity;
    private boolean isHidden;

}
