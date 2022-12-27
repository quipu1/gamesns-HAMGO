package com.web.curation.service;

import com.web.curation.dao.follow.FollowerDao;
import com.web.curation.dao.follow.FollowingDao;
import com.web.curation.dao.member.MemberDao;
import com.web.curation.model.follow.Follower;
import com.web.curation.model.follow.Following;
import com.web.curation.model.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FollowService {

    @Autowired
    MemberDao memberDao;

    @Autowired
    FollowerDao followerDao;

    @Autowired
    FollowingDao followingDao;

    public List<Member> getFollower(String toId) {

        List<Member> list = new ArrayList<>();

        for(Follower f : followerDao.findFollowerByToId(toId)) {
            list.add(memberDao.findMemberByUid(f.getFromId()).get());
        }
        return list;

    }

    public List<Member> getFollowing(String fromId) {

        List<Member> list = new ArrayList<>();

        for(Following f : followingDao.findFollowingByFromId(fromId)) {

            list.add(memberDao.findMemberByUid(f.getToId()).get());
        }

        return list;

    }

    public boolean AddOrDeleteFollowing(String fromNickname, String toNickname) {

        Optional<Member> memberOpt;
        Optional<Member> memberOpt2;

        // 팔로잉 리스트 update
        memberOpt = memberDao.findMemberByNickname(fromNickname);
        memberOpt2 = memberDao.findMemberByNickname(toNickname);

        if (memberOpt.isPresent() && memberOpt2.isPresent()) {

            try {

                String fromId = memberOpt.get().getUid();
                String toId = memberOpt2.get().getUid();

                // follower DB 에 있는 (fromId, toId) 쌍인지 확인할 변수
                boolean flag = false;
                Following delFollow = new Following();
                List<Following> fList = followingDao.findFollowingByFromId(fromId);
                for (Following f : fList) {
                    if (f.getFromId().equals(fromId) && f.getToId().equals(toId)) {
                        delFollow = f;
                        fList.remove(f);
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    followingDao.delete(delFollow);
                } else {

                    Following following = new Following();
                    following.setToId(toId);
                    following.setFromId(fromId);
                    followingDao.save(following);
                }

                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else return false;
    }


    public boolean AddOrDeleteFollower(String fromNickname, String toNickname) {

        Optional<Member> memberOpt = memberDao.findMemberByNickname(toNickname);
        Optional<Member> memberOpt2 = memberDao.findMemberByNickname(fromNickname);

        if(memberOpt.isPresent() && memberOpt2.isPresent()) {

            try {

                String fromId = memberOpt2.get().getUid();
                String toId = memberOpt.get().getUid();

                // follower DB 에 있는 (fromId, toId) 쌍인지 확인할 변수
                boolean flag = false;
                Follower delFollower = new Follower();
                List<Follower> fList = followerDao.findFollowerByToId(toId);
                for (Follower f : fList) {
                    if (f.getFromId().equals(fromId) && f.getToId().equals(toId)) {
                        delFollower = f;
                        fList.remove(f);
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    followerDao.delete(delFollower);
                } else {

                    Follower follower = new Follower();
                    follower.setToId(toId);
                    follower.setFromId(fromId);
                    followerDao.save(follower);
                }
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

}
