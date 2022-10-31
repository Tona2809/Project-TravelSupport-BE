package com.hcmute.hotel.security.DTO;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;
import java.util.stream.Collectors;


public class AppUserDetail implements UserDetails {
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    private String id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private UserEntity user;
    private Collection<? extends GrantedAuthority> authorities;
    private Collection<String> roles;

    public AppUserDetail(String id, String username, String email, String password,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static AppUserDetail build(UserEntity user) {

        Set<String> roleNames = new HashSet<>();

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());


        return new AppUserDetail(
                user.getId(),
                user.getPhone(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return  authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public String getId() {
        return id;
    }
}

