package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.Set;

@Entity
@RestResource(exported = false)
@Table(name = "\"users\"")
public class UserEntity {
    @Id
    @Column(name = "\"user_id\"")
    @GeneratedValue(
            generator = "UUID"
    )
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    @Column(name = "\"full_name\"")
    private String fullName;

    @Column(name = "\"email\"")
    private String email;

    @JsonIgnore
    @Column(name = "\"password\"")
    private String password;

    @Column(name = "\"gender\"")
    private String gender;

    @Column(name = "\"phone\"")
    private String phone;

    @Column(name = "\"status\"")
    private boolean status;

    private boolean active;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinTable(name = "\"user_like_stay\"", joinColumns = @JoinColumn(name = "\"user_id\""), inverseJoinColumns = @JoinColumn(name = "\"stay_id\""))
    private Set<StayEntity> stayLiked;

    public Set<StayEntity> getStayLiked() {
        return stayLiked;
    }

    public void setStayLiked(Set<StayEntity> stayLiked) {
        this.stayLiked = stayLiked;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "\"user_role\"", joinColumns = @JoinColumn(name = "\"user_id\""), inverseJoinColumns = @JoinColumn(name = "\"role_id\""))
    private Set<RoleEntity> roles;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "userReview")
    @JsonIgnore
    private  Set<ReviewEntity> listReview;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "userRating")
    @JsonIgnore
    private  Set<StayRatingEntity> listRating;

    public Set<StayRatingEntity> getListRating() {
        return listRating;
    }

    public void setListRating(Set<StayRatingEntity> listRating) {
        this.listRating = listRating;
    }

    public UserEntity(String password, String phone) {
        this.password = password;
        this.phone = phone;
    }
    @OneToMany(mappedBy = "host",targetEntity = StayEntity.class,cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<StayEntity> stayOwner;




    public Set<StayEntity> getStayOwner() {
        return stayOwner;
    }

    public void setStayOwner(Set<StayEntity> stayOwner) {
        this.stayOwner = stayOwner;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleEntity> roles) {
        this.roles = roles;
    }


    public UserEntity() {
    }
}
