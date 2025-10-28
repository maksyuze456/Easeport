package org.easeport.itsupportsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ItSupportSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItSupportSystemApplication.class, args);
    }

}
