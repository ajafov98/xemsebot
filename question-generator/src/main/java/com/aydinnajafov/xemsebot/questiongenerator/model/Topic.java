package com.aydinnajafov.xemsebot.questiongenerator.model;

import lombok.Data;

import java.util.List;

@Data
public class Topic {
    private int topicId;
    private int packageId;
    private String topicName;
    private List<Question> questionList;
}
