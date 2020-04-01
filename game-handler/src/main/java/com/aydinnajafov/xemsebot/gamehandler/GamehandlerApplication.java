package com.aydinnajafov.xemsebot.gamehandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class GamehandlerApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(GamehandlerApplication.class, args);
    }

}
