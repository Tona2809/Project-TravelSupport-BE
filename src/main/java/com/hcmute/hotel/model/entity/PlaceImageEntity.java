package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.UUID;

@Entity
@RestResource(exported = false)
@Table(name = "\"place_images\"")
public class PlaceImageEntity {
    @Id
    @Column(name = "\"id\"")
    @GeneratedValue(
            generator = "UUID"
    )
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String imgId;
    @Column(name = "\"img_link\"")
    private String imgLink;
    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "\"place_id\"")
    private PlaceEntity place;

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public PlaceEntity getPlace() {
        return place;
    }

    public void setPlace(PlaceEntity placeEntity) {
        this.place = placeEntity;
    }

    public PlaceImageEntity() {
        this.imgId = String.valueOf(UUID.randomUUID());
    }
}
