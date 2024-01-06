package com.backend.komeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
public class KoMeetApplication {

    public static void main(String[] args) {
        SpringApplication.run(KoMeetApplication.class, args);
    }

}
