package com.aydinnajafov.xemsebot.gamehandler.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String userName;
    private String firstName;
    private String lastName;
    private long userId;
    private long chatId;
    private int score;
    private boolean isReferee;
    private boolean isAnswering;
}
