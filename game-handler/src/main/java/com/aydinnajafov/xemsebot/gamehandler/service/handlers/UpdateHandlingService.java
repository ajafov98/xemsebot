package com.aydinnajafov.xemsebot.gamehandler.service.handlers;

import com.aydinnajafov.xemsebot.gamehandler.model.GamePackage;
import com.aydinnajafov.xemsebot.gamehandler.model.GroupChat;
import com.aydinnajafov.xemsebot.gamehandler.model.Topic;
import com.aydinnajafov.xemsebot.gamehandler.model.User;
import com.aydinnajafov.xemsebot.gamehandler.repositories.GroupChatRepository;
import com.aydinnajafov.xemsebot.gamehandler.service.services.updateservices.NewGroupService;
import com.aydinnajafov.xemsebot.gamehandler.service.services.updateservices.SendQuestionService;
import com.aydinnajafov.xemsebot.gamehandler.service.services.updateservices.StartGameRegistrationService;
/*import com.aydinnajafov.xemsebot.questiongenerator.model.Topic;
import com.aydinnajafov.xemsebot.questiongenerator.service.QuestionsForGameService;*/
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Data
@NoArgsConstructor
public class UpdateHandlingService {
    @Autowired
    private GroupChatRepository groupChatRepository;
    @Autowired
    private MessageExecutingHandlingService executeHandler;
    @Autowired
    private StartGameRegistrationService newGameService;
    @Autowired
    private NewGroupService newGroupService;
    @Autowired
    private SendQuestionService questionService;


    //Check if group exists in DB. If not, add to DB
    public void checkAddedBot(Update update) {
        if(update.getMessage().getNewChatMembers().listIterator().next().getBot()) {
            executeHandler.messageExecute(newGroupService.groupRegisterConfirmation(update));
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
                .setText("Matchmaking is ended, registered players are: " + groupChat.getGameSession().showUsersScoreList()));
        User referee = groupChat.getGameSession().getUserMap().get(selectReferee(groupChat));
        referee.setReferee(true);
        groupChat.getGameSession().setRefId(referee.getChatId());
        executeHandler.messageExecute(new SendMessage()
        .setChatId(groupChat.getChatId())
        .setText(referee.getUserName() + " is your game Referee"));
        groupChat.getGameSession().getUserMap().put(referee.getUserId(), referee);
        groupChat.setGameRegistrationCommencing(false);
        executeHandler.deleteMessage(new DeleteMessage().setChatId(groupChat.getChatId()).setMessageId(groupChat.getUsersListMessageId()));
        groupChat.setOnGame(true);
        groupChat.getGameSession().setGamePackage(getTopicNames());
        questionService.sendFirstQuestionOfTopic(groupChat);
        groupChatRepository.save(groupChat);

    }

    //Pinging service
    public void pingHandler(GroupChat groupChat) {
        executeHandler.messageExecute(new SendMessage()
                .setChatId(groupChat.getChatId())
                .setText("Pong"));
    }

    public boolean checkOnGameHandler(GroupChat groupChat, Update update) {
        if(groupChat.getGameSession().getUserMap().containsKey(update.getMessage().getFrom().getId().longValue())) {
            return true;
        } else {
            executeHandler.messageExecute(new SendMessage()
                    .setChatId(update.getMessage().getFrom().getId().longValue())
                    .setText("Group is on game, please wait until it ends"));

            executeHandler.deleteMessage(new DeleteMessage()
                    .setChatId(groupChat.getChatId())
                    .setMessageId(update.getMessage().getMessageId()));

            return false;
        }
    }




    //Saving message that holds join to game button and list of registered users for further deletion
    private void addRegisterMessageIdToGroupChat(GroupChat groupChat, long messageId) {
        groupChat.setUsersListMessageId(Math.toIntExact(messageId));
        groupChatRepository.save(groupChat);
    }

    private GamePackage getTopicNames() {
        RestTemplate restTemplate = new RestTemplate();
        GamePackage gamePackage = restTemplate.getForObject("http://localhost:8181/get_questions", GamePackage.class);
        return gamePackage;
    }

    private Long selectReferee(GroupChat groupChat) {
        Random random = new Random();
        List<Long> userList = new ArrayList<>(groupChat.getGameSession().getUserMap().keySet());
        return userList.get(random.nextInt(userList.size()));
    }




}
