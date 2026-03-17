package com.example.spl2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Spl2Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Spl2Application.class, args);
        // Try to read server.port if set, default to 8080
        String port = ctx.getEnvironment().getProperty("local.server.port");
        if (port == null) port = ctx.getEnvironment().getProperty("server.port", "8080");
        System.out.println("Application entry point: http://localhost:" + port + "/");
    }

}
