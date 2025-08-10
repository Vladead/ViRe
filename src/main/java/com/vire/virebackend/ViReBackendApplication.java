package com.vire.virebackend;

import com.vire.virebackend.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = {JwtProperties.class})
public class ViReBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViReBackendApplication.class, args);
    }

}
