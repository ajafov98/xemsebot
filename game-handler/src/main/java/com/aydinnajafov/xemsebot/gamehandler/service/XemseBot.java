package com.aydinnajafov.xemsebot.gamehandler.service;

import com.aydinnajafov.xemsebot.gamehandler.model.GameSession;
import com.aydinnajafov.xemsebot.gamehandler.model.GroupChat;
import com.aydinnajafov.xemsebot.gamehandler.repositories.GroupChatRepository;
import com.aydinnajafov.xemsebot.gamehandler.service.handlers.CallbackQueryHandlingService;
import com.aydinnajafov.xemsebot.gamehandler.service.handlers.MessageExecutingHandlingService;
import com.aydinnajafov.xemsebot.gamehandler.service.handlers.UpdateHandlingService;
import com.aydinnajafov.xemsebot.gamehandler.service.services.callbackservices.JoinNewGameService;
import com.aydinnajafov.xemsebot.gamehandler.service.services.updateservices.NewGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

@Component
public class XemseBot extends TelegramLongPollingBot {

    @Autowired
    GroupChatRepository groupChatRepository;
    @Autowired
    MessageExecutingHandlingService executeHandler;
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
    private final static String START_GAME_COMMAND = "/start_game";




    @Override
    public void onUpdateReceived(Update update) {
        GroupChat groupChat;

       /* if(update.hasMessage() && update.getMessage().hasText() && !update.getMessage().getChat().isGroupChat()) {
            if(update.getMessage().getText().contains("/start")) {
                User user = User.builder().chatId(update.getMessage().getChatId()).build();
                long groupId = Long.parseLong(update.getMessage().getText().split(" ")[1]);

            }
        }*/

        if(update.hasMessage() && update.getMessage().hasText()) { //Handle Updates with text messages
            groupChat =  selectGroup(update);

            if(checkCommand(update.getMessage().getText(), "/end_game")) {
                GameSession gameSession = groupChat.getGameSession();
                groupChat.setGameRegistrationCommencing(false);
                groupChat.setOnGame(false);
                groupChat.setQuestionSent(false);
                executeHandler.deleteMessage(new DeleteMessage().setChatId(groupChat.getChatId()).setMessageId(groupChat.getUsersListMessageId()));
                executeHandler.messageExecute(new SendMessage()
                        .setReplyMarkup(new ReplyKeyboardRemove())
                        .setChatId(groupChat.getChatId())
                        .setText("Game ended! Results: \n" + gameSession.showUsersScoreList()));

                groupChatRepository.save(groupChat);
            }

            //Check if registration active
            else if(groupChat.isGameRegistrationCommencing() && !checkCommand(update.getMessage().getText(), "/proceed_game")) { //Calling handler if game already started

                updateHandlingService.checkRegistrationHandler(groupChat, update);
            }



            //Check if group is on game
            else if(groupChat.isOnGame()) {
                if(updateHandlingService.checkOnGameHandler(groupChat, update)) {
                    executeHandler.messageExecute(new SendMessage()
                            .setChatId(groupChat.getChatId())
                            .setText("Further actions will be developed")
                    );
                }

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
            else if(checkCommand(update.getMessage().getText(), "/proceed_game")) {
                updateHandlingService.proceedGameHandler(groupChat);
            }

        } else if(update.hasCallbackQuery()) { //Handle CallbackQueries

            System.out.println("Reached as callback query");
            System.out.println(update.getMessage().getText());
            /*// Get GroupChat that update comes from
            groupChat = groupChatRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get();

            //Handle join to game button
            if(update.getCallbackQuery().getData().equals("join_to_game")) {
                callbackHandlingService.joinToGameHandler(groupChat, update);
            }*/
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

    private GroupChat selectGroup(Update update) {
        if(!update.getMessage().getNewChatMembers().isEmpty()) {
            updateHandlingService.checkAddedBot(update);
            return groupChatRepository.save(new GroupChat(update));
        } else if(groupChatRepository.findById(update.getMessage().getChatId()).isPresent()) {
            return groupChatRepository.findById(update.getMessage().getChatId()).get();
        } else {
            return groupChatRepository.save(new GroupChat(update));
        }
    }











}
