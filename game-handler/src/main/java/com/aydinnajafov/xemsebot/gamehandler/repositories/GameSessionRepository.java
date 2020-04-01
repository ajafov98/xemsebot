package com.aydinnajafov.xemsebot.gamehandler.repositories;

import com.aydinnajafov.xemsebot.gamehandler.model.GameSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSessionRepository extends CrudRepository<GameSession, Long> {
}
