package com.aydinnajafov.xemsebot.questiongenerator.endpoint;

import com.aydinnajafov.xemsebot.questiongenerator.model.GamePackage;
import com.aydinnajafov.xemsebot.questiongenerator.service.QuestionsForGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @Autowired
    QuestionsForGameService questions;

    @GetMapping(path = "/get_questions")
    public GamePackage getQuestions() {
        return questions.getQuestions();
    }
}
