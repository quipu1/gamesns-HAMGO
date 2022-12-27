package com.web.curation.service;

import com.web.curation.dao.member.MemberDao;
import com.web.curation.model.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MannerService {

    @Autowired
    MemberDao memberDao;

    @Transactional
    public int getManner(String uid) {

        try {

            Optional<Member> optionalMember = memberDao.findMemberByUid(uid);


            if (optionalMember.isPresent()) {

                Member member = optionalMember.get();

                int manner = member.getManner();

                return manner;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return -1;

    }

    @Transactional
    public int addManner(String uid, int score) {

        try {

            Optional<Member> optionalMember = memberDao.findMemberByUid(uid);

            if (optionalMember.isPresent()) {

                Member member = optionalMember.get();

                int manner = member.getManner() + score;

                member.setManner(manner);

                // 어떻게 저장하지,,,
                memberDao.save(member);

                return manner;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 리턴 2 -> 에러
            return -1;
        }

        return -1;

    }

}
