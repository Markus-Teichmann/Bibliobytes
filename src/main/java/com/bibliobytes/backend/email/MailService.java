package com.bibliobytes.backend.email;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@AllArgsConstructor
public class MailService {
    private JavaMailSender mailSender;

    public void sendSimpleMessage(String from, String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public String sendCodeTo(String address) {
        // Generate Random Code
        IntStream stream = new Random().ints(6L, 0, 10);
        String code = Arrays.toString(stream.toArray());
        code = code.replaceAll("[^0-9]", "");

        System.out.println("Der Code für die Email lautet: " + code);

        // Email verschicken
//            mailService.sendSimpleMessage(
//                    mailConfig.getFrom(),
//                    user.getEmail(),
//                    "Registrierung bei Bibiliobytes",
//                    "Bitte gebe den Code: " + code + "auf der Website ein."
//            );
        return code;
    }

}
