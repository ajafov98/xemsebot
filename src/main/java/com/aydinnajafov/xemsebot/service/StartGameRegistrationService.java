package com.aydinnajafov.xemsebot.service;


import com.aydinnajafov.xemsebot.model.GameSession;
import com.aydinnajafov.xemsebot.model.GroupChat;
import com.aydinnajafov.xemsebot.repositories.GroupChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class StartGameRegistrationService {
    @Autowired
    GroupChatRepository groupChatRepository;

    //Starting new game session
    public SendMessage startGameSession(GroupChat groupChat) { //TODO: Add multiple game types in future
        //Calling implementation of game initialization
        initializeNewGame(groupChat);
        //Inline keyboard button for joining the game
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        rowInLine.add(new InlineKeyboardButton().setText("Join to new game").setCallbackData("join_to_game"));
        rowsInLine.add(rowInLine);
        inlineMarkup.setKeyboard(rowsInLine);
        //Constructing new message with list of joined users
        SendMessage message = new SendMessage();
        message.setChatId(groupChat.getChatId()).setReplyMarkup(inlineMarkup).setText("List of users:");
        return message;
    }

    // Initialize a gameSession and attach it to groupChat. Then save it to DB

    private void initializeNewGame(GroupChat chat) {
        long chatId = chat.getChatId();
        long latestGameSessionId = chat.getLatestGameSessionId() + 1;
        GameSession gameSession = new GameSession(latestGameSessionId); //TODO: Add Packages base and attach Package by the type of game;
        chat.setGameRegistrationCommencing(true);
        chat.setLatestGameSessionId(latestGameSessionId);
        chat.setGameSession(gameSession);
        groupChatRepository.save(chat);
    }

}

