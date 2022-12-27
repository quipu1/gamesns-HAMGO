package com.web.curation.dao.board;

import com.web.curation.model.board.BoardLikeMember;
import com.web.curation.model.follow.Follower;
import com.web.curation.model.follow.Following;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeDao extends JpaRepository<BoardLikeMember, String> {

    Optional<List <BoardLikeMember>> findBoardLikeMemberByBid(Long Bid);

    Optional<BoardLikeMember> findBoardLikeMemberByBidAndUid(Long Bid, String Uid);


}
