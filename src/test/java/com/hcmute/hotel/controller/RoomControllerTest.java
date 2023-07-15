package com.hcmute.hotel.controller;

import com.hcmute.hotel.handler.AuthenticateHandler;
import com.hcmute.hotel.mapping.RoomMapping;
import com.hcmute.hotel.model.entity.RoomEntity;
import com.hcmute.hotel.model.entity.StayEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.Room.AddNewRoomRequest;
import com.hcmute.hotel.model.payload.response.ErrorResponse;
import com.hcmute.hotel.service.RoomService;
import com.hcmute.hotel.service.RoomServiceService;
import com.hcmute.hotel.service.StayService;
import com.hcmute.hotel.security.JWT.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoomControllerTest {

    @InjectMocks
    private RoomController roomController;

    @Mock
    private RoomService roomService;

    @Mock
    private StayService stayService;

    @Mock
    private RoomServiceService roomServiceService;

    @Mock
    private AuthenticateHandler authenticateHandler;

    @Mock
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddRoom_ValidRequest_ReturnsSuccessResponse() {
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        AddNewRoomRequest addNewRoomRequest = new AddNewRoomRequest();
        addNewRoomRequest.setStayId("stay123");
        RoomEntity room =  mock(RoomEntity.class);
        StayEntity stay = mock(StayEntity.class);
        stay.setId("stay123");
        UserEntity user = mock(UserEntity.class);

        when(authenticateHandler.authenticateUser(request)).thenReturn(user);
        when(stayService.getStayById(anyString())).thenReturn(stay);
        when(roomService.addRoom(any(RoomEntity.class))).thenReturn(room);

        ResponseEntity<Object> response = roomController.addRoom(request, addNewRoomRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(room, response.getBody());
    }

    @Test
    void testAddRoom_InvalidStayId_ReturnsErrorResponse() {
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        AddNewRoomRequest addNewRoomRequest = new AddNewRoomRequest();
        addNewRoomRequest.setStayId("stay123");

        when(authenticateHandler.authenticateUser(request)).thenThrow(new BadCredentialsException("Unauthorized"));
        when(stayService.getStayById(anyString())).thenReturn(null);

        
        ResponseEntity<Object> response = roomController.addRoom(request, addNewRoomRequest);

        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testGetRoomById_ValidRequest_ReturnsSuccessResponse() {
        
        List<String> roomIds = new ArrayList<>();
        roomIds.add("room123");
        List<RoomEntity> rooms = new ArrayList<>();
        rooms.add(new RoomEntity());

        when(roomService.findRoomByListId(anyList())).thenReturn(rooms);

        
        ResponseEntity<Object> response = roomController.getRoomById(roomIds);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rooms, response.getBody());
    }

    @Test
    void testGetRoomById_NoRoomsFound_ReturnsErrorResponse() {
        
        List<String> roomIds = new ArrayList<>();
        roomIds.add("room123");

        when(roomService.findRoomByListId(anyList())).thenReturn(null);

        
        ResponseEntity<Object> response = roomController.getRoomById(roomIds);

        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
