package com.aydinnajafov.xemsebot.questiongenerator.dao;

import com.aydinnajafov.xemsebot.questiongenerator.model.GamePackage;
import com.aydinnajafov.xemsebot.questiongenerator.model.Question;
import com.aydinnajafov.xemsebot.questiongenerator.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class MainDAO {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    DataSource dataSource;

    public GamePackage getRandomPackage() {
        String query = "select package_id from packages order by rand() limit 1;";
        return jdbcTemplate
                .queryForObject(query, new BeanPropertyRowMapper<>(GamePackage.class));
    }

    public List<Topic> getRandomTopicsOfPackage(int packageId) {
        String query = "select * from topics where package_id = ? order by rand() limit 5;";
       return jdbcTemplate
               .query(query,new Object[]{packageId}, new BeanPropertyRowMapper<>(Topic.class));
    }

    public List<Question> getQuestionsOfTopic(int topicId, int packageId) {
        String query = "select * from questions where package_id = ? and topic_id = ?";
        return jdbcTemplate
                .query(query,
                        new Object[]{packageId, topicId}, new BeanPropertyRowMapper<>(Question.class));
    }
}
