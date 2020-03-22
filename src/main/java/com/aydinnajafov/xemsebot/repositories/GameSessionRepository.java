package com.aydinnajafov.xemsebot.repositories;

import com.aydinnajafov.xemsebot.model.GameSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSessionRepository extends CrudRepository<GameSession, Long> {
}
