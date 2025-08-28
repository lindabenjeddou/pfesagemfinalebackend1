package tn.esprit.PI.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tn.esprit.PI.entity.User;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendRegistrationEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("benjeddoulinda90@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            String resetLink = "http://localhost:3000/reset/" + resetToken;
            String emailBody = String.format("Hello,\n\nYou have requested to reset your password. Click the link below to set a new password:\n\n%s\n\nIf you did not request a password reset, please ignore this email.\n\nRegards,\nYour App Team", resetLink);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("benjeddoulinda90@gmail.com");
            message.setTo(to);
            message.setSubject("Password Reset");
            message.setText(emailBody);

            javaMailSender.send(message);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
        }
    }
    public void sendConfirmationEmail(String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("benjeddoulinda90@gmail.com");
            message.setTo(to);
            message.setSubject("Account Confirmed SAGEM MAGAZIN");
            message.setText("Your account has been confirmed. Thank you for registering!");

            javaMailSender.send(message);
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
        }
    }

}
