package com.pp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class CasePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasePlatformApplication.class, args);
    }

}
