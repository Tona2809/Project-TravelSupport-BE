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
@Table(name = "\"voucher\"")
public class VoucherEntity {
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
    @Column(name ="\"name\"")
    private String name;
    @Column(name = "\"discount\"")
    private int discount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    @Column(name = "\"create_at\"")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    @Column(name = "\"update_at\"")
    private LocalDateTime updateAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @ApiModelProperty(required = true, example = "2021-08-20T00:00:00")
    @Column(name = "\"expire_at\"")
    private LocalDateTime expirationDate;

    @Column(name = "\"is_hidden\"")
    private boolean isHidden;

    @ManyToOne()
    @JoinColumn(name = "\"room_id\"")
    @JsonIgnore
    private RoomEntity room;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinTable(name = "\"user_voucher\"", joinColumns = @JoinColumn(name = "\"voucher_id\""), inverseJoinColumns = @JoinColumn(name = "\"user_id\""))
    private Set<UserEntity> users;
    @Column(name ="\"quantity\"")
    private int quantity;

    @Column(name="\"remainingQuatity\"")
    private int remainingQuantity;

    @OneToMany(mappedBy = "voucher", targetEntity = BookingRoomEntity.class)
    @JsonIgnore
    private Set<BookingRoomEntity> bookingRoom;

    public Set<BookingRoomEntity> getBookingRoom() {
        return bookingRoom;
    }

    public void setBookingRoom(Set<BookingRoomEntity> bookingRoom) {
        this.bookingRoom = bookingRoom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
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

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public RoomEntity getRoom() {
        return room;
    }

    public void setRoom(RoomEntity room) {
        this.room = room;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public VoucherEntity() {
    }

    public VoucherEntity(int discount, LocalDateTime createAt, LocalDateTime updateAt, LocalDateTime expirationDate, boolean isHidden, RoomEntity room, int quantity, int remainingQuantity) {
        this.discount = discount;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.expirationDate = expirationDate;
        this.isHidden = isHidden;
        this.room = room;
        this.quantity = quantity;
        this.remainingQuantity = remainingQuantity;
    }

}
