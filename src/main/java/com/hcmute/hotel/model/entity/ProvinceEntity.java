package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.rest.core.annotation.RestResource;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@RestResource(exported = false)
@Entity
@Table(name = "\"province\"")
public class ProvinceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private int id;

    @Column(name = "\"name\"")
    private String name;

    @Column(name = "\"place_count\"")
    private int placeCount;

    @Column(name = "\"is_hidden\"")
    private boolean isHidden;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    @Column(name = "\"create_at\"")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    @Column(name = "\"update_at\"")
    private LocalDateTime updateAt;
    @OneToMany(mappedBy = "province",targetEntity = StayEntity.class)
    @JsonIgnore
    private Set<StayEntity> stay;

    public Set<StayEntity> getStay() {
        return stay;
    }

    public void setStay(Set<StayEntity> stay) {
        this.stay = stay;
    }

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
