package com.bibliobytes.backend.auth.services.mail;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
public class MailServerConfig {
    private String from;
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean auth;
    private boolean ssl;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        System.out.println("Host: " + host);
        System.out.println("Port: " + port);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Auth: " + auth);
        System.out.println("SSL: " + ssl);

        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", ssl);
        props.put("mail.debug", "true");

        return mailSender;
    }
}
