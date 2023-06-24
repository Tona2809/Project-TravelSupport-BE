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
        helper.setFrom("managementsitetrack@gmail.com",senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        String verifyURL = "http://localhost:3000" + "/api/authenticate/verify/" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        emailSender.send(message);

    }

    @Override
    public void sendOwnerConfirmEmail(String email) throws MessagingException, UnsupportedEncodingException {
        String toAddress =email;
        String senderName="UTETravel";
        String subject = "We have receive your request";
        String content = "Thank you for register as an owner at UTETravel,<br>"
                + "We will contact with you as soon as possible to get more information<br>"
                + "Thank you,<br>"
                + "UTETravel.";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("managementsitetrack@gmail.com",senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);
        emailSender.send(message);
    }

    @Override
    public void sendOwnerRegistrationEmail(UserEntity user) throws MessagingException, UnsupportedEncodingException {
        String toAddress =user.getEmail();
        String senderName="UTETravel";
        String subject = "Owner Account on UTETravel";
        String content = "Thank you for your owner registration at UTETravel,<br>"
                + "We kindly inform that you has been become an owner at UTETravel:<br>"
                + "<h3>Your account info:</h3><br>"
                + "Username:[[email]]"
                + "Password:[[password]]"
                + "Thank you,<br>"
                + "UTETravel.";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("managementsitetrack@gmail.com",senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[email]]", user.getEmail());
        content = content.replace("[[password]]",user.getPassword());
        helper.setText(content, true);
        emailSender.send(message);
    }

    @Override
    public void sendBlockedAccountEmail(UserEntity user, String reason) throws MessagingException, UnsupportedEncodingException {
        String toAddress =user.getEmail();
        String senderName="UTETravel";
        String subject = "Your account is suspended on UTETravel";
        String content = "Hi [[name]], your account is currently banned from UTETravel, because of the following reason:<br>"
                + "<b>[[reason]]</b><br>"
                + "Please feel free to contact admin via managementsitetrack@gmail.com if you think this is an misunderstood.<br>"
                + "Thank you,<br>"
                + "UTETravel.";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("managementsitetrack@gmail.com",senderName);
        content = content.replace("[[name]]", user.getFullName()==null ? "" : user.getFullName());
        content = content.replace("[[reason]]", reason == null ? "" : reason);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);
        emailSender.send(message);
    }

    @Override
    public void sendUnblockedAccountEmail(UserEntity user) throws MessagingException, UnsupportedEncodingException {
        String toAddress =user.getEmail();
        String senderName="UTETravel";
        String subject = "Your account is no longer banned on UTETravel";
        String content = "Hi [[name]], your account is no longer banned from UTETravel<br>"
                + "We are sorry for the inconvenience. Thank you for being a part of UTETravel<br>"
                + "Thank you,<br>"
                + "UTETravel.";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("managementsitetrack@gmail.com",senderName);
        content = content.replace("[[name]]", user.getFullName()==null ? "" : user.getFullName());
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);
        emailSender.send(message);
    }


}
