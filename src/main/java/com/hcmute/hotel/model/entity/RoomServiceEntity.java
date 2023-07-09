package com.hcmute.hotel.model.entity;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;

@RestResource(exported = false)
@Entity
@Table(name = "\"room_service\"")
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


    public RoomServiceEntity() {
    }



    public RoomServiceEntity(String roomServiceName) {
        this.roomServiceName = roomServiceName;
    }
}
