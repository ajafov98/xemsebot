package com.aydinnajafov.xemsebot.service;

import com.aydinnajafov.xemsebot.model.GroupChat;
import com.aydinnajafov.xemsebot.repositories.GroupChatRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Data
@NoArgsConstructor
public class UpdateHandlingService {
    @Autowired
    private GroupChatRepository groupChatRepository;
    @Autowired
    private MessageExecutingHandlingService executeHandler;
    @Autowired
    private StartNewGameService newGameService;
    @Autowired
    private NewGroupService newGroupService;

    //Check if group exists in DB. If not, add to DB
    public void checkAddedBot(Update update) {
        if(!update.getMessage().getNewChatMembers().isEmpty()) {
            if(update.getMessage().getNewChatMembers().listIterator().next().getBot()) {
                executeHandler.messageExecute(newGroupService.groupRegisterConfirmation(update));
            }
        }
    }

    //Check if registration active
    public void checkRegistrationHandler(GroupChat groupChat, Update update) { //Will take action if group in registering stage
        //Deleting sent message to the group
        executeHandler.deleteMessage(new DeleteMessage().setChatId(groupChat.getChatId()).setMessageId(update.getMessage().getMessageId()));
        //New message warning about registration
        executeHandler.messageExecute(new SendMessage()
                .setChatId(groupChat.getChatId())
                .setText("Registration is active don't write please!" + "\n"
                        + "You can end resgistration and start the game with \"/proceedgame\" command"));
    }

    //Check start game command.
    public void checkStartGameHandler(GroupChat groupChat, Update update) {
        //Check if bot have rights to delete messages
        //It's needed to delete messages during registration and messages from not playing users while game is commencing
        if (executeHandler.checkRights(update)) {
            //Sending message with inline registration button
            long registrationMessageId = executeHandler.executeWithValue(newGameService.startGameSession(groupChat));
            //Saving registration message for deleting after end of registration
            addRegisterMessageIdToGroupChat(groupChat, registrationMessageId);
        } else { executeHandler.warnNotAdminMessage(update); //Sending warning if bot is not admin
        }
    }

    //End the registration and proceed to game
    public void proceedGameHandler(GroupChat groupChat) {
        executeHandler.messageExecute(new SendMessage()
                .setChatId(groupChat.getChatId())
                .setText("Matchmaking is ended, registered players are: " + groupChat.getGameSession().showUsersList()));
        groupChat.setGameRegistrationCommencing(false);
        //TODO: Add isOnGame setting to true. Set while game rules
        executeHandler.deleteMessage(new DeleteMessage().setChatId(groupChat.getChatId()).setMessageId(groupChat.getUsersListMessageId()));
        //Setting state of group
        groupChatRepository.save(groupChat);
    }

    //Pinging service
    public void pingHandler(GroupChat groupChat) {
        executeHandler.messageExecute(new SendMessage()
                .setChatId(groupChat.getChatId())
                .setText("Pong"));
    }



    //Saving message that holds join to game button and list of registered users for further deletion
    private void addRegisterMessageIdToGroupChat(GroupChat groupChat, long messageId) {
        groupChat.setUsersListMessageId(Math.toIntExact(messageId));
        groupChatRepository.save(groupChat);
    }



}
