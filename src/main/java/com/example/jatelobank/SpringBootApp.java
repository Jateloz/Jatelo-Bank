package com.example.jatelobank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
@SpringBootApplication
public class SpringBootApp {
    public static void main(String[] args){
        SpringApplication.run(SpringBootApp.class,args);

    }

}
