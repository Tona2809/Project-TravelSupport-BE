package com.hcmute.hotel.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.*;
import java.util.Set;

@RestResource(exported = false)
@Entity
@Table(name = "\"rooms\"")
@NoArgsConstructor
public class RoomEntity {
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

    @Column(name = "\"room_name\"")
    private String roomName;

    @Column(name = "\"guest_number\"")
    private int guestNumber;

    @Column(name = "\"number_of_room\"")
    private int numberOfRoom;

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "\"stay_id\"")
    private StayEntity stay;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "\"service_for_room\"", joinColumns = @JoinColumn(name = "\"room_id\""), inverseJoinColumns = @JoinColumn(name = "\"service_id\""))
    private Set<RoomServiceEntity> roomService;

    @OneToMany(mappedBy = "room", targetEntity = BookingRoomEntity.class,cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<BookingRoomEntity> bookingRoom;

    @OneToMany(mappedBy = "room",targetEntity = VoucherEntity.class,cascade = CascadeType.ALL)
    private Set<VoucherEntity> voucher;

    @Column(name = "\"price\"")
    private int price;

    @Column(name= "\"is_hidden\"")
    private boolean isHidden;

    public Set<RoomServiceEntity> getRoomService() {
        return roomService;
    }

    public void setRoomService(Set<RoomServiceEntity> roomService) {
        this.roomService = roomService;
    }

    public Set<VoucherEntity> getVoucher() {
        return voucher;
    }

    public void setVoucher(Set<VoucherEntity> voucher) {
        this.voucher = voucher;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<BookingRoomEntity> getBookingRoom() {
        return bookingRoom;
    }

    public void setBookingRoom(Set<BookingRoomEntity> bookingRoom) {
        this.bookingRoom = bookingRoom;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(int guestNumber) {
        this.guestNumber = guestNumber;
    }

    public int getNumberOfRoom() {
        return numberOfRoom;
    }

    public void setNumberOfRoom(int numberOfRoom) {
        this.numberOfRoom = numberOfRoom;
    }

    public StayEntity getStay() {
        return stay;
    }

    public void setStay(StayEntity stay) {
        this.stay = stay;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
