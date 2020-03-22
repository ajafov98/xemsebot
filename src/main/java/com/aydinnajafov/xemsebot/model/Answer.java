package com.aydinnajafov.xemsebot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.security.DenyAll;

@Data
@AllArgsConstructor

public class Answer {
    private String question;
    private String answerOfUser;
}
