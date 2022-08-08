package com.rest.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.rest.app"})
public class RestApiApplication {

    public static void main(String []args){
        SpringApplication.run(RestApiApplication.class,args);
    }

}

