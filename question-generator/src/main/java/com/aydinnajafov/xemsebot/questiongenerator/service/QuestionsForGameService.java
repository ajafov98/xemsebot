package com.aydinnajafov.xemsebot.questiongenerator.service;

import com.aydinnajafov.xemsebot.questiongenerator.dao.MainDAO;
import com.aydinnajafov.xemsebot.questiongenerator.model.GamePackage;
import com.aydinnajafov.xemsebot.questiongenerator.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionsForGameService {
    @Autowired
    MainDAO mainDAO;

    public GamePackage getQuestions() {
        GamePackage gamePackage = mainDAO.getRandomPackage();
        int packageId = gamePackage.getPackageId();
        List<Topic> gameTopics = mainDAO.getRandomTopicsOfPackage(packageId);

        for(Topic topic : gameTopics) {
            topic.setQuestionList(mainDAO.getQuestionsOfTopic(topic.getTopicId(), packageId));
        }

        gamePackage.setTopicList(gameTopics);

        return gamePackage;
    }
}
