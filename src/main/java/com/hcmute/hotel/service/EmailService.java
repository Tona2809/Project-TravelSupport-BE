package com.hcmute.hotel.service;

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

    void sendOwnerConfirmEmail(UserEntity user, String SiteURL)throws MessagingException, UnsupportedEncodingException;
}
