package com.example.MySpringConsoleHelloWorld;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MySpringConsoleHelloWorldApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MySpringConsoleHelloWorldApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Hello, Spring Console World! ");
    }
}