package com.aydinnajafov.xemsebot.gamehandler.service.services.updateservices;

import com.aydinnajafov.xemsebot.gamehandler.model.GameSession;
import com.aydinnajafov.xemsebot.gamehandler.model.GroupChat;
import com.aydinnajafov.xemsebot.gamehandler.model.Topic;
import com.aydinnajafov.xemsebot.gamehandler.repositories.GroupChatRepository;
import com.aydinnajafov.xemsebot.gamehandler.service.handlers.MessageExecutingHandlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class SendQuestionService {
    @Autowired
    MessageExecutingHandlingService executeHandler;
    @Autowired
    GroupChatRepository groupChatRepository;

    public void sendFirstQuestionOfTopic(GroupChat groupChat) {
        GameSession gameSession = groupChat.getGameSession();
        long chatId = groupChat.getChatId();
        executeHandler.messageExecute(new SendMessage()
        .setChatId(chatId)
        .setText("Topic: " + gameSession.getGamePackage().getTopicList().get(gameSession.getTopicId()).getTopicName()));
        executeHandler.messageExecute(questionBuilder(groupChat));
    }

    private SendMessage questionBuilder(GroupChat groupChat) {

        GameSession gameSession = groupChat.getGameSession();
        Topic topic = gameSession.getGamePackage().getTopicList().get(gameSession.getTopicId());


        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(groupChat.getChatId());
        sendMessage.setReplyMarkup(answerButton());
        String question = (gameSession.getQuestionId() + 1) + ". " +
                topic.getQuestionList().get(gameSession.getQuestionId()).getQuestion();
        sendMessage.setText(question);
        gameSession.setQuestionId(gameSession.getQuestionId() + 1);
        groupChat.setQuestionSent(true);
        groupChatRepository.save(groupChat);
        return sendMessage;
    }




    private ReplyKeyboardMarkup answerButton() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("\uD83D\uDEA8");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
