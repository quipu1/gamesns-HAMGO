package com.web.curation.service;

import com.web.curation.dao.member.MemberDao;
import com.web.curation.model.OAuthToken;
import com.web.curation.model.member.ImgRequest;
import com.web.curation.model.member.Member;
import com.web.curation.model.member.SignupRequest;
import com.web.curation.util.SecurityUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    MemberDao memberDao;

    @Autowired
    private OAuth2Kakao oAuth2Kakao;

    public String kakaoLogin(String code) {
        OAuthToken oAuthToken = oAuth2Kakao.getAccessToken(code);

        System.out.println("KAKAO oauth Token : " + oAuthToken.getAccess_token());

        String uid = oAuth2Kakao.getMemberByAccessToken(oAuthToken.getAccess_token());
        return uid;
    }

    // uid 로 회원정보 가져오기
    @Transactional(readOnly = true)
    public Optional<Member> getMember(String uid) {

        return memberDao.findMemberByUid(uid);

    }

    // 닉네임으로 회원정보 가져오기
    @Transactional(readOnly = true)
    public Optional<Member> getMemberByNickname(String nickname) {
        return memberDao.findMemberByNickname(nickname);
    }

    // 현재 SecurituContext에 있는 유저 정보 가져오기
    @Transactional(readOnly = true)
    public Optional<Member> getCurMemberInfo() {
        return memberDao.findMemberByUid(SecurityUtil.getCurrentMemberUid());
    }

    @Transactional
    public Optional<Member> getUserByCode(String code) {
        return memberDao.findByUid(code);
    }

    @Transactional
    // 닉네임으로 회원 정보 조회(중복체크)
    public Optional<Member> getUserByNickname(String nickname) {

        return memberDao.findMemberByNickname(nickname);
    }

    // 닉네임 받아와서 회원정보 추가(user table 에 insert)
    public boolean addUser(SignupRequest request) {

        Member member = new Member();
        member.setUid(request.getUid());
        member.setNickname(request.getNickname());

        try {
            memberDao.save(member);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 닉네임과 프로필 사진을 받아와서 회원정보 수정
    public boolean updateMember(Member member, ImgRequest request, String path) {

        member.setNickname(request.getNickname());

        if(!path.equals("C://upload//")) member.setPimg(path);

        try{
            memberDao.save(member);
            return true;
        } catch (Exception e){
            System.out.println(e);
            return false;
        }
    }


    // 카카오톡 access_token 으로 사용자 id 받기
    public int getMemberInfo(String access_Token){

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null ){
                result += line;
            }

            JSONObject parser = new JSONObject(result);
            System.out.println(parser.get("id"));

            return (int) parser.get("id");

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 카카오톡 로그아웃
    public void kakaoLogout(String access_Token){
        String reqURL = "https://kapi.kakao.com/v1/user/unlink";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
