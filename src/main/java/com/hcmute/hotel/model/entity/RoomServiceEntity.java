package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.Set;

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

    @ManyToMany(mappedBy = "roomService")
    @JsonIgnore
    Set<RoomEntity> room;

    public Set<RoomEntity> getRoom() {
        return room;
    }

    public void setRoom(Set<RoomEntity> room) {
        this.room = room;
    }

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
