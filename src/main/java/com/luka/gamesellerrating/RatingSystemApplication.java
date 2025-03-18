package com.luka.gamesellerrating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Profile("prod")
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class RatingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(RatingSystemApplication.class, args);
    }
}
