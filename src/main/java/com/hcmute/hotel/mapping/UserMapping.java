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
        return new UserEntity( registerRequest.getPassword(),registerRequest.getEmail());
    }
    public static UserEntity registerOwnerToEntity(UserEntity user,AddNewOwnerRequest registerRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setFullName(registerRequest.getFullName());
        user.setPhone(registerRequest.getPhone());
        user.setGender(registerRequest.getGender());
        user.setEmail(registerRequest.getEmail());
        registerRequest.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPassword(registerRequest.getPassword());
        return user;
    }
}
