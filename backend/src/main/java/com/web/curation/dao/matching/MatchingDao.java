package com.web.curation.dao.matching;

import com.web.curation.model.board.Board;
import com.web.curation.model.matching.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchingDao extends JpaRepository<Game, String> {

    List<Game> findByGameName(String gameName);

}
