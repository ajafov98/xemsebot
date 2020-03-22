package com.aydinnajafov.xemsebot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Data
@RedisHash
public class GameSession {
    @Id
    private long sessionId;
    private long groupChatId;
    private List<Package> packageList;
    private List<User> userList;
    private long refId;
    private boolean waitingAnswer;
    private long answeringUserId;

    public GameSession (long sessionId) {
        this.sessionId = sessionId;
        packageList = new ArrayList<>();
        userList = new ArrayList<>();
        waitingAnswer = false;
    }

    public  String showUsersList() {
        StringBuilder stringBuilder = new StringBuilder();

        for(User user : userList) {
            stringBuilder.append("\n").append(user.getUserName());
        }

        return stringBuilder.toString();
    }
}
