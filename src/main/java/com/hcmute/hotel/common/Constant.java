package com.hcmute.hotel.common;


import com.auth0.jwt.algorithms.Algorithm;

public class Constant {
    public static final String JWT_SECRET = "secretttttt";
    public static final Long JWT_ACCESS_TOKEN_EXPIRATION = 600000L; // 10 minutes in milliseconds
    public static final Algorithm JWT_ALGORITHM = Algorithm.HMAC256(JWT_SECRET.getBytes());
}
