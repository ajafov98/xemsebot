package com.aydinnajafov.xemsebot.service;

import com.aydinnajafov.xemsebot.model.GroupChat;
import com.aydinnajafov.xemsebot.model.User;
import com.aydinnajafov.xemsebot.repositories.GroupChatRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Data
@NoArgsConstructor
public class CallbackQueryHandlingService {

    @Autowired
    private XemseBot xemseBot;
    @Autowired
    private GroupChatRepository groupChatRepository;
    @Autowired
    private MessageExecutingHandlingService executeHandler;
    @Autowired
    private JoinNewGameService joinNewGameService;

    //Handle join to game button
    public void joinToGameHandler(GroupChat groupChat, Update update) {
        if(checkRepeatedUser(groupChat, update)) { //Check if user is already registered
            //Adding user to registered users message that holds join to game button
            executeHandler.messageExecute(joinNewGameService.join(update, groupChat));
            //Answer for join to game button
            executeHandler.answerCallbackQueryExecute(new AnswerCallbackQuery()
                    .setCallbackQueryId(update.getCallbackQuery().getId())
                    .setText("SUCCESS"));

        } else {

            //Warn user that he is already registered
            executeHandler.answerCallbackQueryExecute(new AnswerCallbackQuery()
                    .setCallbackQueryId(update.getCallbackQuery().getId())
                    .setText("You're already registered"));
        }
    }


    private boolean checkRepeatedUser(GroupChat groupChat, Update update) {
        //Get user name of the user
        long userId = update.getCallbackQuery().getFrom().getId().longValue();

        if(groupChat.getGameSession().getUserMap().isEmpty()) { //Check if list is empty
            return true;
        }

        return !groupChat.getGameSession().getUserMap().containsKey(userId);
    }
}
