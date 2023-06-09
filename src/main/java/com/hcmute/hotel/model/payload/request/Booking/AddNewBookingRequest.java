package com.hcmute.hotel.model.payload.request.Booking;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Setter
@Getter
public class AddNewBookingRequest {
    @NotEmpty(message = "Stay id can't be empty")
    private String stayId;
    @NotEmpty(message = "Voucher id can't be emtpy")
    private String voucherId;
    private LocalDateTime checkinDate;
    private LocalDateTime checkoutDate;
    @NotNull(message = "Total people can't be null")
    private int totalPeople;
}
