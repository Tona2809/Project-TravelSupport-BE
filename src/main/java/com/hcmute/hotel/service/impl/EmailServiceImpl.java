package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.UserEntity;
import com.hcmute.hotel.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

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
    @Override
    public void sendConfirmCustomerEmail(UserEntity user,String siteURL)
            throws MessagingException, UnsupportedEncodingException
    {
        String toAddress =user.getEmail();
        String senderName="UTETravel";
        String subject = "Please verify your registration";
        String content = "Thank you for register at UTETravel,<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "UTETravel.";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("dilawabms900@gmail.com",senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        String verifyURL = "http://travel.eba-diweyejm.us-east-1.elasticbeanstalk.com" + "/api/authenticate/verify/" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        emailSender.send(message);

    }

    @Override
    public void sendOwnerConfirmEmail(UserEntity user, String SiteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress =user.getEmail();
        String senderName="UTETravel";
        String subject = "Please continue to complete your request";
        String content = "Thank you for register as an owner at UTETravel,<br>"
                + "Please click the link below to continue your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Your verification code is:[[code]]<br>"
                + "Thank you,<br>"
                + "UTETravel.";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("dilawabms900@gmail.com",senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        String verifyURL = SiteURL + "/api/authenticate/owner";
        content = content.replace("[[URL]]", verifyURL);
        content = content.replace("[[code]]", user.getVerificationCode());
        helper.setText(content, true);
        emailSender.send(message);
    }

}
