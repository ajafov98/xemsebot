package com.aydinnajafov.xemsebot.gamehandler.service.services.callbackservices;

import com.aydinnajafov.xemsebot.gamehandler.model.GroupChat;
import com.aydinnajafov.xemsebot.gamehandler.repositories.GameSessionRepository;
import com.aydinnajafov.xemsebot.gamehandler.repositories.GroupChatRepository;
import com.aydinnajafov.xemsebot.gamehandler.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class JoinNewGameService {
    @Autowired
    GameSessionRepository gameSessionRepository;
    @Autowired
    GroupChatRepository groupChatRepository;


    //Joining to new game
    @Transactional
    public EditMessageText join(Update update, GroupChat groupChat) {
        //Get user information from the update
        User user = User.builder().chatId(update.getCallbackQuery().getMessage().getChatId()).build();

        //Adding user to Game Session of the Group and saving it to DB
        groupChat.getGameSession().getUserMap().put(user.getUserId(), user);
        groupChatRepository.save(groupChat);
        return editUsersList(groupChat);
    }

    //Editing message that shows list of users that connected to game
    private EditMessageText editUsersList(GroupChat groupChat) {
        long chatId = groupChat.getChatId();
        int usersListMessageId = groupChat.getUsersListMessageId();
        EditMessageText text = new EditMessageText().setChatId(chatId).setMessageId(usersListMessageId).setText(usersList(groupChat));
        // Adding inline markup for join to game button
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        rowInLine.add(new InlineKeyboardButton()
                // .setText("Join to new game")
                .setCallbackData("join_to_game")
                .setUrl("https//t.me/xemse_test_bot?start=" + groupChat.getChatId()).setCallbackData("join_to_game"));
        rowsInLine.add(rowInLine);
        inlineMarkup.setKeyboard(rowsInLine);
        text.setReplyMarkup(inlineMarkup);

        return text;
    }

    //Generating User list for, status of joined players
    private String usersList(GroupChat groupChat) {
        StringBuilder stringBuilder = new StringBuilder();

        groupChat.getGameSession().getUserMap()
                .forEach((userId, user) -> stringBuilder.append(user.getUserName()).append("\n"));

        return stringBuilder.toString();
    }
}

