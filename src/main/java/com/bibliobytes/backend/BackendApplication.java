package com.bibliobytes.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
//        try {
//            JweConfig.generateRSAKey();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
}
