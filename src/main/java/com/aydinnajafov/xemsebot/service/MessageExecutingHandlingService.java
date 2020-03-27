package com.aydinnajafov.xemsebot.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Data
@NoArgsConstructor
public class MessageExecutingHandlingService {

    @Autowired
    private XemseBot xemseBot;
    @Value("${xemsebot.bot_userid}")
    private int botUserId;


    public void messageExecute(SendMessage sendMessage, Update update) {
        try {
            SendMessage sendMessage1 = new SendMessage();
            sendMessage1.setChatId(update.getMessage().getFrom().getId().longValue()).setText("SUCCESS");
            xemseBot.execute(sendMessage1);
            xemseBot.execute(sendMessage);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void messageExecute(SendMessage sendMessage) {
        try {
            xemseBot.execute(sendMessage);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public long executeWithValue(SendMessage sendMessage) {
        try {
            Message message =  xemseBot.execute(sendMessage);
            return message.getMessageId();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return 0;
        }
    }

    public void messageExecute(EditMessageText messageText) {
        try {
            xemseBot.execute(messageText);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void deleteMessage(DeleteMessage deleteMessage) {
        try {
            xemseBot.execute(deleteMessage);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void answerCallbackQueryExecute(AnswerCallbackQuery answerCallbackQuery) {
        try {
            xemseBot.execute(answerCallbackQuery);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Check rights of bot
    boolean checkRights(Update update) {
        GetChatMember getChatMember = new GetChatMember().setChatId(update.getMessage().getChatId()).setUserId(botUserId);
        ChatMember chatMember;
        try {
            //Get bot as a ChatMember
            chatMember = xemseBot.execute(getChatMember);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            chatMember = null;
        }
        if(chatMember != null) {
            try {
                //Checking if bot can delete messages
                return chatMember.getCanDeleteMessages();
            } catch (NullPointerException ex) {
                return false;
            }
        }
        return false;
    }

    //Warning if bot is not admin of group or can't delete messages
    public void warnNotAdminMessage(Update update) {
        messageExecute(new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("To start game please, declare bot as a admin and give him permissions to delete a messages"));
    }
}