package com.hcmute.hotel.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public interface EmailService {
    void sendSimpleMessage(String to,String subject,String text);

}
