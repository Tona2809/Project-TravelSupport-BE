package com.hcmute.hotel.model.entity;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.time.LocalDateTime;

@RestResource(exported = false)
@Entity
@Table(name = "\"hotel_rating\"")
@NoArgsConstructor
public class HotelRatingEntity {
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
    //sua lai sau khi co hotel
    @Column(name = "\"hotel\"")
    private String hotel;
    @Column(name = "\"created_at\"")
    private LocalDateTime created_at;

    public HotelRatingEntity(String id, int rate, UserEntity userRating, String hotel, LocalDateTime created_at,String message) {
        this.id = id;
        this.rate = rate;
        this.userRating = userRating;
        this.hotel = hotel;
        this.created_at = created_at;
        this.message=message;
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

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
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
