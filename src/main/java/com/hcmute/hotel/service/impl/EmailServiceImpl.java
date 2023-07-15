package com.hcmute.hotel.service.impl;

import com.hcmute.hotel.model.entity.BookingEntity;
import com.hcmute.hotel.model.entity.StayRatingEntity;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    @Override
    public void sendOwnerBookingConfirmation(BookingEntity booking, String ownerName) throws MessagingException, UnsupportedEncodingException {
        String toAddress =booking.getStay().getHost().getEmail();
        String senderName="UTETravel";
        String subject = "New Booking on UTE travel. Booking id:[[bookingId]]";
        String content = "Dear [[name]], <br>"
                + "We are delighted to inform you that a new booking request has been made for your stay, [[stayName]], for the following date:<br>"
                + "Check-in: [[startDate]]<br>"
                + "Check-out: [[endDate]]<br>"
                + "Booking Details:<br>"
                + "Customer Name: [[customerName]]<br>"
                + "Customer Email: [[customerEmail]]<br>"
                + "Customer Phone: [[CustomerPhoneNumber]]<br>"
                + "If you wish to accept the booking request, please log in to your account and navigate to the Trang quản lý section. From there, you can confirm the booking and update the availability calendar accordingly."
                + "Please note that the booking is not confirmed until you accept it through your account. If you do not take any action within [[specifyTime]], the booking request will automatically expire.<br>"
                + "Best regards,"
                + "UTETravel.";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("managementsitetrack@gmail.com",senderName);
        content=content.replace("[[bookingId]]",booking.getId());
        content=content.replace("[[name]]",booking.getStay().getHost().getFullName());
        content=content.replace("[[stayName]]", booking.getStay().getName());
        content=content.replace("[[startDate]]", booking.getCheckinDate().toLocalDate().toString());
        content=content.replace("[[endDate]]", booking.getCheckoutDate().toLocalDate().toString());
        content=content.replace("[[customerName]]",booking.getUser().getFullName());
        content=content.replace("[[customerEmail]]",booking.getUser().getEmail());
        content=content.replace("[[CustomerPhoneNumber]]",booking.getUser().getPhone());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        content=content.replace("[[specifyTime]]",booking.getExpiredConfirmTime().format(formatter));
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);
        emailSender.send(message);
    }

    @Override
    public void sendUserDeclineBookingEmail(BookingEntity booking, String reason) throws MessagingException, UnsupportedEncodingException {
        String toAddress =booking.getStay().getHost().getEmail();
        String senderName="UTETravel";
        String subject = "Your recent booking have been decline";
        String content = "Dear [[name]], <br>"
                + "We regret to inform you that we are unable to accommodate your booking request at [[stayName]] for the following requested dates:<br>"
                + "Check-in: [[startDate]]<br>"
                + "Check-out: [[endDate]]<br>"
                + "We understand that this may come as a disappointment, and we sincerely apologize for any inconvenience caused. Unfortunately, due to [[reason]], we are unable to secure your reservation.<br>"
                + "Once again, we apologize for any inconvenience caused, and we appreciate your understanding in this matter. We hope to have the opportunity to welcome you as our guest in the future.<br>"
                + "Best regards,"
                + "UTETravel.";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("managementsitetrack@gmail.com",senderName);
        content=content.replace("[[name]]",booking.getStay().getHost().getFullName());
        content=content.replace("[[stayName]]", booking.getStay().getName());
        content=content.replace("[[reason]]", reason);
        content=content.replace("[[startDate]]", booking.getCheckinDate().toLocalDate().toString());
        content=content.replace("[[endDate]]", booking.getCheckoutDate().toLocalDate().toString());
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);
        emailSender.send(message);
    }

    @Override
    public void sendForgotPasswordEmail(UserEntity user)
            throws MessagingException, UnsupportedEncodingException
    {
        String toAddress =user.getEmail();
        String senderName="UTETravel";
        String subject = "Reset Password Request";
        String content = "We have receive your reset password request,<br>"
                + "Please using the code below to reset your password:<br>"
                + "<b>[[resetCode]]</b><br>"
                + "Thank you,<br>"
                + "UTETravel.";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("managementsitetrack@gmail.com",senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[resetCode]]", user.getVerificationCode());
        helper.setText(content, true);
        emailSender.send(message);
    }

    @Override
    public void reportRating(StayRatingEntity rating) throws MessagingException, UnsupportedEncodingException {
        String toAddress ="managementsitetrack@gmail.com";
        String senderName="UTETravel";
        String subject = "Report comment from [[reporter]]";
        String content = "There an violent comment from [[commenter]]:,<br>"
                + "Message: [[message]]<br>"
                + "Please consider this comment and give [[commenter]] an temporary suspended<br>"
                + "Thank you,<br>"
                + "UTETravel.";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(rating.getStay().getHost().getEmail(),senderName);
        helper.setTo(toAddress);
        subject = subject.replace("[[reporter]]", rating.getStay().getHost().getFullName());
        helper.setSubject(subject);
        content = content.replace("[[commenter]]", rating.getUserRating().getFullName());
        content = content.replace("[[message]]", rating.getMessage());
        helper.setText(content, true);
        emailSender.send(message);
    }
}
