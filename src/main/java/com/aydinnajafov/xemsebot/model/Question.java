package com.aydinnajafov.xemsebot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private int questionId;
    private int topicId;
    private long packageId;
    private String question;
    private String correctAnswer;
    private String criteria;
}
