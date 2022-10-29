package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.SendFailedException;
@Service
@Transactional
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender emailSender;
    @Override
    public void sendSimpleMessage(String to, String subject, String text)  {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@travel.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);



    }
}
