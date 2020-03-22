package com.aydinnajafov.xemsebot.service;

import com.aydinnajafov.xemsebot.model.GroupChat;
import com.aydinnajafov.xemsebot.repositories.GroupChatRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Data
public class NewGroupService {

    @Autowired
    private GroupChatRepository groupChatRepository;


    public SendMessage groupRegisterConfirmation(Update update) {
        SendMessage sendMessage = new SendMessage();
        long chatId = update.getMessage().getChatId();
        String chatName = update.getMessage().getChat().getTitle();
        if(groupChatRepository.existsById(chatId)) {
            String welcomeMessage = "Hi!!!" + "\n" +
                    "Good to see you again in " + chatName +" !!!" + "\n" +
                    "Type command \"/startgame\" to start game";
            return sendMessage.setChatId(chatId).setText(welcomeMessage);
        } else {
            groupChatRepository.save(new GroupChat(chatId, chatName));
            String welcomeMessage = "Hello everybody!!!!" + "\n" +
                    "I'm bot for playing \"Xəmsə\"!!!" + "\n" +
                    "Type command \"/startgame\" to start new game!!!";
            return sendMessage.setChatId(chatId).setText(welcomeMessage);
        }
    }


}
