package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@RestResource(exported = false)
@Table(name = "\"places\"")
public class PlaceEntity {
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
    @Column(name = "\"hidden\"")
    private boolean hidden;
    @OneToMany(mappedBy = "place", targetEntity = PlaceImageEntity.class, cascade = CascadeType.ALL)
    @Column(name = "\"image\"")
    private Set<PlaceImageEntity> placeImage;
    @ManyToOne
    @JoinColumn(name = "\"province\"")
    private ProvinceEntity province;
    @Column(name = "\"address_description\"")
    private String addressDescription;
    @Column(name = "\"description\"", columnDefinition = "TEXT")
    private String description;
    @Column(name = "\"latitude\"")
    private Double latitude;
    @Column(name = "\"longitude\"")
    private Double longitude;
    @Column(name = "\"time_open\"")
    private String timeOpen;
    @Column(name = "\"time_close\"")
    private String timeClose;
    @Column(name = "\"type\"")
    private String type;
    @Column(name = "\"min_price\"")
    private int minPrice;
    @Column(name = "\"max_price\"")
    private int maxPrice;
    @Column(name = "\"recommend_time\"")
    private String recommendTime;

    public String getAddressDescription() {
        return addressDescription;
    }

    public void setAddressDescription(String addressDescription) {
        this.addressDescription = addressDescription;
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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Set<PlaceImageEntity> getPlaceImage() {
        return placeImage;
    }

    public void setPlaceImage(Set<PlaceImageEntity> placeImage) {
        this.placeImage = placeImage;
    }

    public ProvinceEntity getProvince() {
        return province;
    }

    public void setProvince(ProvinceEntity province) {
        this.province = province;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTimeOpen() {
        return timeOpen;
    }

    public void setTimeOpen(String timeOpen) {
        this.timeOpen = timeOpen;
    }

    public String getTimeClose() {
        return timeClose;
    }

    public void setTimeClose(String timeClose) {
        this.timeClose = timeClose;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getRecommendTime() {
        return recommendTime;
    }

    public void setRecommendTime(String recommendTime) {
        this.recommendTime = recommendTime;
    }

    public PlaceEntity() {
    }
}
