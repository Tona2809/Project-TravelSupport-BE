package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.UUID;

@RestResource(exported = false)
@Entity
@Table(name = "\"stay_images\"")
public class StayImageEntity {
    @Id
    @GeneratedValue(
            generator = "UUID"
    )
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "\"id\"")
    private String imgId;
    @Column(name = "\"img_link\"")
    private String imgLink;
    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "\"stay_id\"")
    private StayEntity stay;

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

    public StayEntity getStay() {
        return stay;
    }

    public void setStay(StayEntity stay) {
        this.stay = stay;
    }

    public StayImageEntity() {
        this.imgId= String.valueOf(UUID.randomUUID());
    }


}
