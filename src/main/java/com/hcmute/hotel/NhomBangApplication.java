package com.hcmute.hotel;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@EnableWebMvc
@SpringBootApplication()
@RequiredArgsConstructor
public class NhomBangApplication {

    public static void main(String[] args) {
        SpringApplication.run(NhomBangApplication.class, args);
    }

}
