package com.hcmute.hotel.service;

import com.hcmute.hotel.model.entity.BookingEntity;
import com.hcmute.hotel.model.entity.StayRatingEntity;
import com.hcmute.hotel.model.entity.UserEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
@Component
public interface EmailService {
    void sendSimpleMessage(String to,String subject,String text);
    void sendConfirmCustomerEmail(UserEntity user, String SiteURL)throws MessagingException, UnsupportedEncodingException;

    void sendOwnerConfirmEmail(String email)throws MessagingException, UnsupportedEncodingException;

    void sendOwnerRegistrationEmail(UserEntity user)throws MessagingException, UnsupportedEncodingException;

    void sendBlockedAccountEmail(UserEntity user, String reason)throws MessagingException, UnsupportedEncodingException;

    void sendUnblockedAccountEmail(UserEntity user)throws MessagingException, UnsupportedEncodingException;

    void sendOwnerBookingConfirmation(BookingEntity booking, String ownerName) throws MessagingException, UnsupportedEncodingException;

    void sendUserDeclineBookingEmail(BookingEntity booking, String reason) throws MessagingException, UnsupportedEncodingException;

    void sendForgotPasswordEmail(UserEntity user) throws MessagingException, UnsupportedEncodingException;

    void reportRating(StayRatingEntity rating) throws MessagingException, UnsupportedEncodingException;
}
