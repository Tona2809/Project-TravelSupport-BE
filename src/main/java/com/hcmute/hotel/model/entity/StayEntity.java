package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import net.bytebuddy.asm.Advice;
import org.apache.catalina.User;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@RestResource(exported = false)
@Entity
@Table(name = "\"stays\"")
public class StayEntity {
    @Id
    @Column(name = "\"id\"")
    @GeneratedValue(
            generator = "UUID"
    )
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;
    @Column(name = "\"name\"")
    private String name;
    @Column(name = "\"address_description\"")
    private String addressDescription;
    @Column(name = "\"stay_description\"")
    private String stayDescription;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    @Column(name = "\"time_open\"")
    private LocalDateTime timeOpen;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    @Column(name = "\"time_close\"")
    private LocalDateTime timeClose;
    @ManyToOne()
    @JoinColumn(name = "\"host\"")
    private UserEntity host;
    @Column(name = "\"status\"")
    private int status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    @Column(name = "\"created_at\"")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    @Column(name = "\"latest_update_at\"")
    private LocalDateTime latestUpdateAt;
    @Column(name = "\"type\"")
    private String type;
    @ManyToMany(mappedBy = "stayLiked",targetEntity = UserEntity.class)
    Set<UserEntity> userLiked;
    @ManyToOne()
    @JoinColumn(name = "\"province\"")
    private ProvinceEntity province;
    @Column(name = "\"hidden\"")
    private boolean hidden;

    @Column(name = "\"checkinTime\"")
    private String checkinTime;

    @Column(name = "\"checkoutTime\"")
    private String checkoutTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "\"stay_amenities\"", joinColumns = @JoinColumn(name = "\"stay_id\""), inverseJoinColumns = @JoinColumn(name = "\"amenities_id\""))
    private Set<AmenitiesEntity> amenities;
    @OneToMany(mappedBy = "stay",targetEntity = StayRatingEntity.class,cascade = CascadeType.ALL)
    private Set<StayRatingEntity> stayRating;
    @OneToMany(mappedBy = "stay",targetEntity = StayImageEntity.class,cascade = CascadeType.ALL)
    private Set<StayImageEntity> stayImage;

    @OneToMany(mappedBy = "stay",targetEntity = RoomEntity.class,cascade = CascadeType.ALL)
    private Set<RoomEntity> room;


    @OneToMany(mappedBy = "stay",targetEntity = VoucherEntity.class,cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<VoucherEntity> voucher;

    @OneToMany(mappedBy = "stay",targetEntity = BookingEntity.class)
    @JsonIgnore
    private Set<BookingEntity> booking;

    private int minPrice;

    private int maxPeople;

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPeople() {
        return maxPeople;
    }
    public Set<BookingEntity> getBooking() {
        return booking;
    }

    public void setBooking(Set<BookingEntity> booking) {
        this.booking = booking;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    public Set<RoomEntity> getRoom() {
        return room;
    }

    public void setRoom(Set<RoomEntity> room) {
        this.room = room;
    }

    public Set<StayImageEntity> getStayImage() {
        return stayImage;
    }

    public void setStayImage(Set<StayImageEntity> stayImage) {
        this.stayImage = stayImage;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }



    public Set<StayRatingEntity> getStayRating() {
        return stayRating;
    }

    public void setStayRating(Set<StayRatingEntity> stayRating) {
        this.stayRating = stayRating;
    }

    public ProvinceEntity getProvince() {
        return province;
    }

    public void setProvince(ProvinceEntity province) {
        this.province = province;
    }

    public Set<UserEntity> getUserLiked() {
        return userLiked;
    }

    public void setUserLiked(Set<UserEntity> userLiked) {
        this.userLiked = userLiked;
    }


    public Set<AmenitiesEntity> getAmenities() {
        return amenities;
    }

    public void setAmenities(Set<AmenitiesEntity> amenities) {
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getAddressDescription() {
        return addressDescription;
    }

    public void setAddressDescription(String addressDescription) {
        this.addressDescription = addressDescription;
    }

    public String getStayDescription() {
        return stayDescription;
    }

    public void setStayDescription(String stayDescription) {
        this.stayDescription = stayDescription;
    }

    public LocalDateTime getTimeOpen() {
        return timeOpen;
    }

    public void setTimeOpen(LocalDateTime timeOpen) {
        this.timeOpen = timeOpen;
    }

    public LocalDateTime getTimeClose() {
        return timeClose;
    }

    public void setTimeClose(LocalDateTime timeClose) {
        this.timeClose = timeClose;
    }

    public UserEntity getHost() {
        return host;
    }

    public void setHost(UserEntity host) {
        this.host = host;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLatestUpdateAt() {
        return latestUpdateAt;
    }

    public void setLatestUpdateAt(LocalDateTime latestUpdateAt) {
        this.latestUpdateAt = latestUpdateAt;
    }

    public Set<VoucherEntity> getVoucher() {
        return voucher;
    }

    public void setVoucher(Set<VoucherEntity> voucher) {
        this.voucher = voucher;
    }

    public StayEntity() {
    }
}
