package com.aydinnajafov.xemsebot.service;

import com.aydinnajafov.xemsebot.model.GameSession;
import com.aydinnajafov.xemsebot.model.GroupChat;
import com.aydinnajafov.xemsebot.model.User;
import com.aydinnajafov.xemsebot.repositories.GroupChatRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class XemseBot extends TelegramLongPollingBot {

    @Autowired
    GroupChatRepository groupChatRepository;

    @Autowired
    NewGroupService newGroupService;
    @Autowired
    JoinNewGameService joinNewGameService;
    @Autowired
    UpdateHandlingService updateHandlingService;
    @Autowired
    CallbackQueryHandlingService callbackHandlingService;
    @Value("${xemsebot.bot_user_name}")
    private String botUserName;
    @Value("${xemsebot.bot_token}")
    private String botToken;
    private final static String START_GAME_COMMAND = "/startgame";




    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            updateHandlingService.checkAddedBot(update);
        }

        if(update.hasMessage() && update.getMessage().hasText()) { //Handle Updates with text messages


            // Get GroupChat that update comes from
            GroupChat groupChat = groupChatRepository.findById(update.getMessage().getChatId()).get();

            //Check if registration active
            if(groupChat.isGameRegistrationCommencing() && !update.getMessage().getText().equals("/proceedgame@xemse_test_bot")) { //Calling handler if game already started
                updateHandlingService.checkRegistrationHandler(groupChat, update);
            }

            //Check start game command.
            else if(update.getMessage().getChatId() < 0 && update.getMessage().getText().contains(START_GAME_COMMAND)) {
               updateHandlingService.checkStartGameHandler(groupChat, update);
            }

            //Pinging service
            else if(checkCommand(update.getMessage().getText(), "/ping")) {
                updateHandlingService.pingHandler(groupChat);
            }

            //End the registration and proceed to game
             else if(checkCommand(update.getMessage().getText(), "/proceedgame@xemse_test_bot")) {
                updateHandlingService.proceedGameHandler(groupChat);
            }

        } else if(update.hasCallbackQuery()) { //Handle CallbackQueries

            // Get GroupChat that update comes from
            GroupChat groupChat = groupChatRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get();

            //Handle join to game button
            if(update.getCallbackQuery().getData().equals("join_to_game")) {
                callbackHandlingService.joinToGameHandler(groupChat, update);
            }
        }
    }

    private boolean checkCommand(String message, String condition) {
        return  message.equals(condition) || message.equals(condition.concat("@xemse_test_bot"));
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }











}
