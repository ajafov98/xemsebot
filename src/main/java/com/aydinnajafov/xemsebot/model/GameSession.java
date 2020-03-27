package com.aydinnajafov.xemsebot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RedisHash
public class GameSession {
    @Id
    private long sessionId;
    private long groupChatId;
    private List<Package> packagesList;
    private Map<Long, User> userMap;
    private long refId;
    private boolean waitingAnswer;
    private long answeringUserId;

    public GameSession (long sessionId) {
        this.sessionId = sessionId;
        packagesList = new ArrayList<>();
        userMap = new HashMap<>();
        waitingAnswer = false;
    }

    public  String showUsersList() {
        StringBuilder stringBuilder = new StringBuilder();
        userMap.forEach((userId, user) -> stringBuilder.append(user.getUserName()).append("\n"));
        return stringBuilder.toString();
    }

    public boolean isPlayer(long userId) {
        return userMap.containsKey(userId);
    }
}
