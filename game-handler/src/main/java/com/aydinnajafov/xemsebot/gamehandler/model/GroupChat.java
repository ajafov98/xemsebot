package com.aydinnajafov.xemsebot.gamehandler.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;

@Data
@RedisHash
public class GroupChat implements Serializable {
    @Id
    private long chatId;
    private String groupChatName;
    private GameSession gameSession;
    private boolean onGame;
    private long latestGameSessionId;
    private boolean gameRegistrationCommencing;
    private int usersListMessageId;
    private boolean questionSent;

    @PersistenceConstructor
    public GroupChat(long chatId, String groupChatName) {
        this.chatId = chatId;
        this.groupChatName = groupChatName;
        latestGameSessionId = 0;
        this.onGame = false;
        this.gameRegistrationCommencing = false;
    }

    public GroupChat(Update update) {
        this.chatId = update.getMessage().getChatId();
        this.groupChatName = update.getMessage().getChat().getTitle();
        latestGameSessionId = 0;
        this.onGame = false;
        this.gameRegistrationCommencing = false;
    }
}
