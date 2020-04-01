package com.aydinnajafov.xemsebot.questiongenerator.model;

import lombok.Data;

@Data
public class Question {
    private int questionId;
    private int topicId;
    private int packageId;
    private String question;
    private String answer;
    private String criteria;
}
