package com.web.curation.service;

import com.web.curation.dao.ImgFile.ImgFileDao;
import com.web.curation.dao.board.BoardDao;
import com.web.curation.dao.member.MemberDao;
import com.web.curation.model.board.Board;
import com.web.curation.model.board.ResponseBoard;
import com.web.curation.model.member.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SearchService {

    @Autowired
    MemberDao memberDao;
    @Autowired
    BoardDao boardDao;
    @Autowired
    ImgFileDao imgFileDao;

    public Optional<Member> getMember(String nickname) {
        return memberDao.findMemberByNickname(nickname);
    }

    public List<Member> searchMember(String nickname) {
        return memberDao.findMemberByNicknameContaining(nickname);
    }

    public List<ResponseBoard>searchHashtag(String hashtag) {
        List<Board> hashtagBoardList = boardDao.findBoardByHashtagsContainingOrderByCreateDateDesc(hashtag);
        List<ResponseBoard> responseBoardList = new ArrayList();
        for (Board board : hashtagBoardList) {
        //  foreach문 = 앞에있는 것 = 뒤에있는것에서 하나씩 빼서 넣을 장소 : 뒤에있는 것 = 여러개가 담겨있는 리스트
            responseBoardList.add(new ResponseBoard(board,imgFileDao.findImgFileByBid(board.getBid()),memberDao.findMemberByUid(board.getUid()).get().getNickname()));
//          optional = 올지안올지모름. 이걸 꺼내기 위해서는 get이라는 함수가 필요하다. (get으로 꺼내서 나온건 Member)
        }
//        Collections.reverse(responseBoardList);

        return responseBoardList;
    }
}
