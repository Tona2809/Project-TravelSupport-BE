package com.hcmute.hotel.model.entity;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;

@RestResource(exported = false)
@Entity
@Table(name = "\"roomservice\"")
public class RoomServiceEntity {
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
    @Column(name = "\"room_service_name\"")
    private String roomServiceName;

    @Column(name ="\"image_link\"")
    private String imgLink;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomServiceName() {
        return roomServiceName;
    }

    public void setRoomServiceName(String roomServiceName) {
        this.roomServiceName = roomServiceName;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public RoomServiceEntity() {
    }

    public RoomServiceEntity(String roomServiceName, String imgLink) {
        this.roomServiceName = roomServiceName;
        this.imgLink = imgLink;
    }
}
