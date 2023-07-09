package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestResource(exported = false)
@Entity
@Table(name = "\"stay_rating\"")
@NoArgsConstructor
public class StayRatingEntity {
    @Id
    @GeneratedValue(
            generator = "UUID"
    )
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "\"id\"")
    private String id;
    @Column(name = "\"rate\"")
    private int rate;
    @ManyToOne()
    @JoinColumn(name = "\"user\"")
    private UserEntity userRating;
    @Column(name = "\"message\"")
    private String message;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "\"stay_id\"")
    private StayEntity stay;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    @Column(name = "\"created_at\"")
    private LocalDateTime created_at;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20")
    @Column(name = "\"checkin_date\"")
    private LocalDate checkinDate;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20")
    @Column(name = "\"checkout_date\"")
    private LocalDate checkoutDate;


    public StayRatingEntity(String id, int rate, UserEntity userRating, StayEntity hotel, LocalDateTime created_at, String message) {
        this.id = id;
        this.rate = rate;
        this.userRating = userRating;
        this.stay = hotel;
        this.created_at = created_at;
        this.message=message;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public UserEntity getUserRating() {
        return userRating;
    }

    public void setUserRating(UserEntity userRating) {
        this.userRating = userRating;
    }

    public StayEntity getStay() {
        return stay;
    }

    public void setStay(StayEntity stay) {
        this.stay = stay;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
