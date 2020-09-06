package com.arfat.OAuthServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@SpringBootApplication
@EnableAuthorizationServer
public class OAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuthServerApplication.class, args);
    }

    @Autowired
    PasswordEncoder encoder;

    @Bean
    ApplicationRunner appRunner() {
        return args -> {
            System.out.println("encoded password for 'pass123' = " + encoder.encode("pass123"));
            System.out.println("encoded password for 'pin' = " + encoder.encode("pin"));
        };
    }
}
