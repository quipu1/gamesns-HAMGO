package com.web.curation.service;

import com.web.curation.dao.follow.FollowerDao;
import com.web.curation.dao.follow.FollowingDao;
import com.web.curation.dao.board.LikeDao;
import com.web.curation.dao.member.MemberDao;
import com.web.curation.model.board.BoardLikeMember;
import com.web.curation.model.follow.Follower;
import com.web.curation.model.follow.Following;
import com.web.curation.model.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    LikeDao likeDao;


    public Optional<List<BoardLikeMember>> getLike(Long bid) {

        Optional<List<BoardLikeMember>> list = likeDao.findBoardLikeMemberByBid(bid);

        return list;
    }

    // 새로고침 좋아요 확인
    public int Liked(Long bid, String uid) {

        Optional<BoardLikeMember> likeOpt;

        likeOpt = likeDao.findBoardLikeMemberByBidAndUid(bid, uid);

        try {

            if (likeOpt.isPresent()) {
                return 1;
            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 리턴 2 -> 에러
            return 2;
        }
    }


    // 좋아요 추가/삭제
    public int AddOrDeleteLike(Long bid, String uid) {

        Optional<BoardLikeMember> likeOpt;

        likeOpt = likeDao.findBoardLikeMemberByBidAndUid(bid, uid);

        BoardLikeMember boardLikeMember = null;

        try {

            if (likeOpt.isPresent()) {

                boardLikeMember = likeOpt.get();
                likeDao.delete(boardLikeMember);
                // 리턴 0 -> 좋아요 삭제
                return 0;

            } else {

                boardLikeMember = new BoardLikeMember();
                boardLikeMember.setBid(bid);
                boardLikeMember.setUid(uid);
                likeDao.save(boardLikeMember);
                // 리턴 1 -> 좋아요 추가
                return 1;
            }

        } catch(Exception e) {
            e.printStackTrace();
            // 리턴 2 -> 에러
            return 2;
        }
    }
}
