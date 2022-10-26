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
    @Column(name ="\"max_people\"")
    private int maxPeople;
    @Column(name = "\"price\"")
    private int price;
    @Column(name = "\"room_number\"")
    private int roomNumber;
    @Column(name = "\"bath_number\"")
    private int bathNumber;
    @Column(name = "\"bedroom_number\"")
    private int bedroomNumber;
    @Column(name = "\"bed_number\"")
    private int bedNumber;
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
    @JsonIgnore
    @ManyToMany(mappedBy = "stayLiked",targetEntity = UserEntity.class)
    Set<UserEntity> userLiked;
    @ManyToOne()
    @JoinColumn(name = "\"province\"")
    private ProvinceEntity province;
    @Column(name = "\"hidden\"")
    private boolean hidden;

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @OneToMany(mappedBy = "stay",targetEntity = StayRatingEntity.class,cascade = CascadeType.ALL)

    private Set<StayRatingEntity> stayRating;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "\"stay_amenities\"", joinColumns = @JoinColumn(name = "\"stay_id\""), inverseJoinColumns = @JoinColumn(name = "\"amenities_id\""))
    private Set<AmenitiesEntity> amenities;

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

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getBathNumber() {
        return bathNumber;
    }

    public void setBathNumber(int bathNumber) {
        this.bathNumber = bathNumber;
    }

    public int getBedroomNumber() {
        return bedroomNumber;
    }

    public void setBedroomNumber(int bedroomNumber) {
        this.bedroomNumber = bedroomNumber;
    }

    public int getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(int bedNumber) {
        this.bedNumber = bedNumber;
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

    public StayEntity() {
    }
}
