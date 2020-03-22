package com.aydinnajafov.xemsebot.model;

import lombok.Builder;
import lombok.Data;

import java.lang.ref.PhantomReference;

@Data
@Builder
public class User {
    private String userName;
    private String firstName;
    private String lastName;
    private long userId;
    private long chatId;
    private String score;
    private boolean isReferee;


}
