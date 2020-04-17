package com.aydinnajafov.xemsebot.gamehandler.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashMap;
import java.util.Map;

@Data
@RedisHash
public class GameSession {
    @Id
    private long sessionId;
    private long groupChatId;
    private GamePackage gamePackage;
    private Map<Long, User> userMap;
    private long refId;
    private boolean waitingAnswer;
    private long answeringUserId;
    private int topicId;
    private int questionId;
    private int lastScore;
    private int lastAnswerQueue;
    private int answerQueue;

    public GameSession (long sessionId) {
        this.sessionId = sessionId;
        userMap = new HashMap<>();
        waitingAnswer = false;
        topicId = 0;
        lastScore = 0;
        questionId = 0;
    }

    public  String showUsersList() {
        StringBuilder stringBuilder = new StringBuilder();
        userMap.forEach((userId, user) -> stringBuilder.append(user.getUserName()).append("\n"));
        return stringBuilder.toString();
    }

    public String showUsersScoreList() {
        StringBuilder stringBuilder = new StringBuilder();
        userMap.forEach((userId, user) -> stringBuilder
                .append(user.getUserName()).append(":\t").append(user.getScore()).append("\n"));
        return stringBuilder.toString();
    }

    public boolean isPlayer(long userId) {
        return userMap.containsKey(userId);
    }
}
