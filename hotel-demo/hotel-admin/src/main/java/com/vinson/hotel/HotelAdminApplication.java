package com.vinson.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class HotelAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(HotelAdminApplication.class, args);
    }
}
