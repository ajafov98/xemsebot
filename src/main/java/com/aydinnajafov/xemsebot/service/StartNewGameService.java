package com.aydinnajafov.xemsebot.service;

import com.aydinnajafov.xemsebot.model.GameSession;
import com.aydinnajafov.xemsebot.model.GroupChat;
import com.aydinnajafov.xemsebot.repositories.GroupChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.AutoPopulatingList;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class StartNewGameService {
    @Autowired
    GroupChatRepository groupChatRepository;

    public SendMessage startGameSession(GroupChat groupChat) { //TODO: Add multiple game types in future
        System.out.println("Start new game session service started");
        System.out.println("Initializing new game");
        initializeNewGame(groupChat);
        //Inline keyboard for joining the game
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        rowInLine.add(new InlineKeyboardButton().setText("Join to new game").setCallbackData("join_to_game"));
        rowsInLine.add(rowInLine);
        inlineMarkup.setKeyboard(rowsInLine);
        //Constructing new message
        SendMessage message = new SendMessage();
        message.setChatId(groupChat.getChatId()).setReplyMarkup(inlineMarkup).setText("List of users:");
        return message;
    }

    // Initialize a gameSession and attach it to groupChat

    private void initializeNewGame(GroupChat chat) {
        System.out.println("started to initialize");
        long chatId = chat.getChatId();
        System.out.println("Here is chat id: " + chatId);
        long latestGameSessionId = chat.getLatestGameSessionId() + 1;
        GameSession gameSession = new GameSession(latestGameSessionId); //TODO: Add Packages base and attach Package by the type of game;
        chat.setGameRegistrationCommencing(true);
        chat.setLatestGameSessionId(latestGameSessionId);
        chat.setGameSession(gameSession);
        groupChatRepository.save(chat);
    }

}
