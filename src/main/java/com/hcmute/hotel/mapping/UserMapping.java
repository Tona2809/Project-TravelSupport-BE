package com.hcmute.hotel.mapping;

import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.model.payload.request.Authenticate.AddNewCustomerRequest;
import com.hcmute.hotel.model.payload.request.Authenticate.AddNewOwnerRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserMapping {
    public static UserEntity registerCustomerToEntity(AddNewCustomerRequest registerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return new UserEntity( registerRequest.getPassword(),registerRequest.getPhone());
    }
    public static UserEntity registerOwnerToEntity(AddNewOwnerRequest registerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return new UserEntity( registerRequest.getFullname(),registerRequest.getEmail(),registerRequest.getPassword(),registerRequest.getGender(),registerRequest.getPhone());
    }
}
