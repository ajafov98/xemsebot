package com.aydinnajafov.xemsebot.questiongenerator.model;

import lombok.Data;

import java.util.List;

@Data
public class GamePackage {
    private int packageId;
    private String packageName;
    private List<Topic> topicList;
}
