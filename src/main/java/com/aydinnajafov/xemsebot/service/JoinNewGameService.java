package com.aydinnajafov.xemsebot.service;

import com.aydinnajafov.xemsebot.model.GameSession;
import com.aydinnajafov.xemsebot.model.GroupChat;
import com.aydinnajafov.xemsebot.model.User;
import com.aydinnajafov.xemsebot.repositories.GameSessionRepository;
import com.aydinnajafov.xemsebot.repositories.GroupChatRepository;
import com.google.inject.internal.cglib.core.$DuplicatesPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.games.Game;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

@Service
public class JoinNewGameService {
    @Autowired
    GameSessionRepository gameSessionRepository;
    @Autowired
    GroupChatRepository groupChatRepository;

    public EditMessageText join(Update update, GroupChat groupChat) {

        User user = User.builder()
                .userId(update.getCallbackQuery().getFrom().getId())
                .userName(update.getCallbackQuery().getFrom().getUserName())
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .firstName(update.getCallbackQuery().getFrom().getFirstName())
                .lastName(update.getCallbackQuery().getFrom().getLastName())
                .build();

        System.out.println("Registered user's username is:" + update.getCallbackQuery().getMessage().getFrom().getUserName());

        groupChat.getGameSession().getUserList().add(user);
        groupChatRepository.save(groupChat);
        return editUsersList(groupChat);
    }

    //Editing list of users that connected to game
    private EditMessageText editUsersList(GroupChat groupChat) {
        long chatId = groupChat.getChatId();
        int usersListMessageId = groupChat.getUsersListMessageId();
        EditMessageText text = new EditMessageText().setChatId(chatId).setMessageId(usersListMessageId).setText(usersList(groupChat));

        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        rowInLine.add(new InlineKeyboardButton().setText("Join to new game").setCallbackData("join_to_game"));
        rowsInLine.add(rowInLine);
        inlineMarkup.setKeyboard(rowsInLine);
        text.setReplyMarkup(inlineMarkup);

        return text;
    }

    private String usersList(GroupChat groupChat) {
        StringBuilder stringBuilder = new StringBuilder();

        for(User user : groupChat.getGameSession().getUserList()) {
            System.out.println("\n" + user.getUserName());
            stringBuilder.append("\n").append(user.getUserName());
        }

        return stringBuilder.toString();
    }





}
