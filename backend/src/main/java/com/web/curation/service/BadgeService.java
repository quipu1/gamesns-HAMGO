package com.web.curation.service;

import com.web.curation.dao.badge.BadgeDao;
import com.web.curation.dao.board.BoardDao;
import com.web.curation.dao.follow.FollowerDao;
import com.web.curation.dao.member.MemberDao;
import com.web.curation.dao.reply.ReplyDao;
import com.web.curation.model.badge.Badge;
import com.web.curation.model.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {

    @Autowired
    MemberDao memberDao;

    @Autowired
    BadgeDao badgeDao;

    @Autowired
    BoardDao boardDao;

    @Autowired
    ReplyDao replyDao;

    @Autowired
    FollowerDao followerDao;

    public List badgeList(String uid) {

        List<Badge> badges = new ArrayList<>();

        // 매너점수
        int mscore = memberDao.findByUid(uid).get().getManner();
        badges.addAll(badgeDao.findBadgeByTypeAndLowcutLessThanEqual("manner", mscore));

        // 보드
        // countby가 long으로 리턴, 없으면 0 리턴
        // intValue로 int 형변환
        int boardcnt = boardDao.countBoardByUid(uid).intValue();
        badges.addAll(badgeDao.findBadgeByTypeAndLowcutLessThanEqual("board", boardcnt));

        // 댓글
        int replycnt = replyDao.countReplyByUid(uid).intValue();
        badges.addAll(badgeDao.findBadgeByTypeAndLowcutLessThanEqual("reply", replycnt));

        // 팔로워
        int followcnt = followerDao.countFollowerByToId(uid).intValue();
        badges.addAll(badgeDao.findBadgeByTypeAndLowcutLessThanEqual("follower", followcnt));

        return badges;
    }

}
