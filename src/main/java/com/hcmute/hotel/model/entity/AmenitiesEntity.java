package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.Set;

@Entity
@RestResource(exported = false)
@Table(name = "\"amenities\"")
public class AmenitiesEntity {
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
    @Column(name = "\"icons\"")
    private String icons;

    @ManyToMany(mappedBy = "amenities")
    @JsonIgnore
    Set<StayEntity> stays;

    public String getIcons() {
        return icons;
    }

    public void setIcons(String icons) {
        this.icons = icons;
    }

    public Set<StayEntity> getStays() {
        return stays;
    }

    public void setStays(Set<StayEntity> stays) {
        this.stays = stays;
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

    public AmenitiesEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public AmenitiesEntity() {
    }
}
