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

    //Registering group chat
    public SendMessage groupRegisterConfirmation(Update update) {
        long chatId = update.getMessage().getChatId();
        String chatName = update.getMessage().getChat().getTitle();

        if (!groupChatRepository.existsById(chatId)) { //If the group is exists in DB send greeting
            groupChatRepository.save(new GroupChat(chatId, chatName));
        }

        return new SendMessage().setChatId(update.getMessage().getChatId())
                .setText("Hello! Good to see you in " + chatName + "\n"
                        + "Type \"/startgame@xemse_test_bot\" to start new game!");
    }

}
