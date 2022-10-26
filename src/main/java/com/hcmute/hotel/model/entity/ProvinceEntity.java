package com.hcmute.hotel.model.entity;

import org.springframework.data.rest.core.annotation.RestResource;
import javax.persistence.*;
import java.time.LocalDateTime;

@RestResource(exported = false)
@Entity
@Table(name = "\"province\"")
public class ProvinceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "place_count")
    private int placeCount;

    @Column(name = "is_hidden")
    private boolean isHidden;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlaceCount() {
        return placeCount;
    }

    public void setPlaceCount(int placeCount) {
        this.placeCount = placeCount;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public ProvinceEntity(int id, String name, int placeCount, boolean isHidden, LocalDateTime createAt, LocalDateTime updateAt) {
        this.id = id;
        this.name = name;
        this.placeCount = placeCount;
        this.isHidden = isHidden;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public ProvinceEntity() {
    }
}
