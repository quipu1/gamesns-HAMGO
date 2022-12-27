package com.web.curation.dao.board;

import java.util.List;

import com.web.curation.model.board.Board;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardDao extends JpaRepository<Board, String> {

    //List<Board> findBoardByUid(String uid);

    @Query( "from Board b " +
            "where b.bid < :bid and " +
            "( b.uid in (select f.toId from Following f where f.fromId = :uid) or " +
            "b.uid = :uid )" +
            "order by b.createDate desc")
    List<Board> findFollowFeedByUid(@Param("bid") long bid, @Param("uid") String uid, Pageable limitTen);

    @Query( "from Board b " +
            "where b.bid < :bid " +
            "order by b.createDate desc")
    List<Board> findFollowFeed(@Param("bid") long bid, Pageable limitTen);

    Board findBoardByBid(long bid);

    List<Board> findBoardByUidAndBidLessThan(String uid,long bid,Pageable limitTen);

    Long countBoardByUid(String uid);
    
    List<Board> findBoardByHashtagsContainingOrderByCreateDateDesc(String hashtags);

}
