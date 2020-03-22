package com.aydinnajafov.xemsebot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class XemsebotApplication {

    public static void main(String[] args) {

        ApiContextInitializer.init();
        SpringApplication.run(XemsebotApplication.class, args);
    }

}
