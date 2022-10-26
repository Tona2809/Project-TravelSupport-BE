package com.hcmute.hotel.model.entity;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.time.LocalDateTime;

@RestResource(exported = false)
@Entity
@Table(name = "\"review\"")
@NoArgsConstructor
public class ReviewEntity {
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
    @Column(name = "\"title\"")
    private String title;
    @Column(name = "\"content\"")
    private String content;
    @Column(name = "\"like_count\"")
    private int likeCount;
    @Column(name = "\"visited_time\"")
    private LocalDateTime visitedTime;
    @Column(name = "\"is_hidden\"")
    private boolean isHidden;
    @Column(name = "\"created_at\"")
    private LocalDateTime createdAt;
    @Column(name = "\"updated_at\"")
    private  LocalDateTime updatedAt;
    @ManyToOne()
    @JoinColumn(name = "\"user\"")
    private UserEntity userReview;
    //sua lai sau khi co place entity
    @Column(name = "\"place\"")
    private String place;


    public ReviewEntity(String id, String title, String content, int likeCount, LocalDateTime visitedTime, boolean isHidden, LocalDateTime createdAt, LocalDateTime updatedAt, UserEntity userReview, String place) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.visitedTime = visitedTime;
        this.isHidden = isHidden;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userReview = userReview;
        this.place = place;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public LocalDateTime getVisitedTime() {
        return visitedTime;
    }

    public void setVisitedTime(LocalDateTime visitedTime) {
        this.visitedTime = visitedTime;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserEntity getUserReview() {
        return userReview;
    }

    public void setUserReview(UserEntity userReview) {
        this.userReview = userReview;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
