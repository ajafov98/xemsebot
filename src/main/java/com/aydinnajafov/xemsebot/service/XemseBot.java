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
    ObjectFactory<SendMessage> senMessageFactory;
    @Autowired
    StartNewGameService gameService;
    @Autowired
    NewGroupService newGroupService;
    @Autowired
    JoinNewGameService joinNewGameService;
    @Value("${xemsebot.bot_user_name}")
    private String botUserName;
    @Value("${xemsebot.bot_token}")
    private String botToken;
    @Value("${xemsebot.bot_userid}")
    private int botUserId;
    private final static String START_GAME_COMMAND = "/startgame";




    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            checkAddedBot(update);
        }

        if(update.hasMessage() && update.getMessage().hasText()) {
            GroupChat groupChat = groupChatRepository.findById(update.getMessage().getChatId()).get();
            if(groupChat.isGameRegistrationCommencing() && !update.getMessage().getText().contains("/")) {
                deleteMessage(new DeleteMessage().setChatId(groupChat.getChatId()).setMessageId(update.getMessage().getMessageId()));
                messageExecute(senMessageFactory.getObject()
                        .setChatId(groupChat.getChatId())
                        .setText("Registration is active don't write please!" + "\n"
                        + "You can end resgistration with \"/proceedgame\" command"));
                messageExecute(senMessageFactory.getObject()
                        .setChatId(groupChat.getChatId())
                        .setReplyToMessageId(groupChat.getUsersListMessageId())
                        .setText("Please continue to join the game or stop registration with" +
                                " \"/proceedgame\" command ")
                );

            }
            if(update.getMessage().getChatId() < 0 && update.getMessage().getText().contains(START_GAME_COMMAND)) {
                if (checkRights(update)) {
                    System.out.println("Running start game session");
                    long registrationMessageId = executeWithValue(gameService.startGameSession(groupChat));
                    addRegisterMessageIdToGroupChat(groupChat, registrationMessageId);
                } else { warnNotAdminMessage(update);
                }
            }

            if(checkCommand(update.getMessage().getText(), "/ping")) {
                messageExecute(senMessageFactory.getObject()
                        .setChatId(groupChat.getChatId())
                        .setText("Pong"));
            } else if(checkCommand(update.getMessage().getText(), "/getuser")) {
                messageExecute(senMessageFactory.getObject().setChatId(groupChat.getChatId())
                .setReplyToMessageId(update.getMessage().getMessageId())
                .setText(messageInfo(update)), update);
            } else if(checkCommand(update.getMessage().getText(), "/proceedgame")) {
                messageExecute(senMessageFactory.getObject()
                        .setChatId(groupChat.getChatId())
                        .setText("Matchmaking is ended, registered players are: " + groupChat.getGameSession().showUsersList()));
                groupChat.setGameRegistrationCommencing(false);
                deleteMessage(new DeleteMessage().setChatId(groupChat.getChatId()).setMessageId(groupChat.getUsersListMessageId()));
                groupChatRepository.save(groupChat);
            }
        } else if(update.hasCallbackQuery()) {
            GroupChat groupChat = groupChatRepository.findById(update.getCallbackQuery().getMessage().getChatId()).get();
            if(update.getCallbackQuery().getData().equals("join_to_game")) {

                try{
                    if(checkRepeatedUser(groupChat, update)) {
                        messageExecute(joinNewGameService.join(update, groupChat));
                        execute(new AnswerCallbackQuery()
                                .setCallbackQueryId(update.getCallbackQuery().getId())
                                .setText("SUCCESS"));

                    } else {
                        execute(new AnswerCallbackQuery()
                                .setCallbackQueryId(update.getCallbackQuery().getId())
                                .setText("You're already registered"));
                    }

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }








    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    private boolean checkRepeatedUser(GroupChat groupChat, Update update) {
        String username = update.getCallbackQuery().getFrom().getUserName();
        if(groupChat.getGameSession().getUserList().isEmpty()) {
            return true;
        }
        for(User u : groupChat.getGameSession().getUserList()) {
            if(u.getUserName().equals(username)) {
                return false;
            }
        }
        return true;
    }

    private void messageExecute(SendMessage sendMessage, Update update) {
        try {
            SendMessage sendMessage1 = new SendMessage();
            sendMessage1.setChatId(update.getMessage().getFrom().getId().longValue()).setText("SUCCESS");
            execute(sendMessage1);
            execute(sendMessage);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void messageExecute(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private long executeWithValue(SendMessage sendMessage) {
        try {
            Message message = execute(sendMessage);
            return message.getMessageId();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return 0;
        }
    }

    private void messageExecute(EditMessageText messageText) {
        try {
            execute(messageText);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void deleteMessage(DeleteMessage deleteMessage) {
        try {
            execute(deleteMessage);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    private boolean checkCommand(String message, String condition) {
            return  message.equals(condition) || message.equals(condition.concat("@xemse_test_bot"));
    }

    private String messageInfo(Update update) {
        String messageBuilder = "UpdateID: " + update.getUpdateId() + "\n" +
                "Chat Title: " + update.getMessage().getChat().getTitle()+ "\n" +
                "Chat Id: " + update.getMessage().getChat().getId()+ "\n" +
                "Chat First Name: " + update.getMessage().getFrom().getFirstName() + "\n" +
                "Chat User Name: " + update.getMessage().getFrom().getUserName() + "\n" +
                "From Id: " + update.getMessage().getFrom().getId() + "\n" +
                "Chat Last Name: " + update.getMessage().getFrom().getLastName() + "\n" +
                "Is Command: " + update.getMessage().isCommand() + "\n";
        return messageBuilder;
    }

    private void checkAddedBot(Update update) {
        if(!update.getMessage().getNewChatMembers().isEmpty()) {
            if(update.getMessage().getNewChatMembers().listIterator().next().getBot()) {
                messageExecute(newGroupService.groupRegisterConfirmation(update));
            }
        }
    }

    private void addRegisterMessageIdToGroupChat(GroupChat groupChat, long messageId) {
        groupChat.setUsersListMessageId(Math.toIntExact(messageId));
        groupChatRepository.save(groupChat);
    }

    private boolean checkRights(Update update) {
        GetChatMember getChatMember = new GetChatMember().setChatId(update.getMessage().getChatId()).setUserId(botUserId);
        ChatMember chatMember;
        try {
            chatMember = execute(getChatMember);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            chatMember = null;
        }
        if(chatMember != null) {
            try {
                return chatMember.getCanDeleteMessages();
            } catch (NullPointerException ex) {
                return false;
            }
        }
        return false;
    }

    public void warnNotAdminMessage(Update update) {
        messageExecute(senMessageFactory.getObject()
                .setChatId(update.getMessage().getChatId())
                .setText("To start game please, declare bot as a admin and give him permissions to delete a messages"));
    }
}
