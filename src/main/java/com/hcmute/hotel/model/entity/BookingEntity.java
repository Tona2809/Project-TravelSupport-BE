package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@RestResource(exported = false)
@Table(name = "\"bookings\"")
public class BookingEntity {
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
    @Column(name = "\"checkin_date\"")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    private LocalDateTime checkinDate;
    @Column(name = "\"checkout_date\"")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    private LocalDateTime checkoutDate;
    @Column(name = "\"create_at\"")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    private LocalDateTime createAt;
    @ManyToOne()
    @JoinColumn(name = "\"user_id\"")
    private UserEntity user;

    @OneToMany(mappedBy = "booking", targetEntity = BookingRoomEntity.class)
    private Set<BookingRoomEntity> bookingRoom;

    public Set<BookingRoomEntity> getBookingRoom() {
        return bookingRoom;
    }

    public void setBookingRoom(Set<BookingRoomEntity> bookingRoom) {
        this.bookingRoom = bookingRoom;
    }

    @Column(name = "\"total_price\"")
    private int totalPrice;
    @Column(name = "\"total_people\"")
    private int totalPeople;

    @Column(name = "\"total_room\"")
    private int totalRoom;
    @Column(name = "\"status\"")
    private int status;

    @ManyToOne()
    @JoinColumn(name ="\"stay_id\"")
    private StayEntity stay;

    @Column(name = "\"payment_id\"")
    private String paymentId;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public StayEntity getStay() {
        return stay;
    }

    public void setStay(StayEntity stay) {
        this.stay = stay;
    }

    public BookingEntity() {
    }
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public int getTotalRoom() {
        return totalRoom;
    }

    public void setTotalRoom(int totalRoom) {
        this.totalRoom = totalRoom;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCheckinDate() {
        return checkinDate;
    }

    public void setCheckinDate(LocalDateTime checkinDate) {
        this.checkinDate = checkinDate;
    }

    public LocalDateTime getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDateTime checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalPeople() {
        return totalPeople;
    }

    public void setTotalPeople(int totalPeople) {
        this.totalPeople = totalPeople;
    }
}
