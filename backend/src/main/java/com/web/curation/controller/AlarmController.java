package com.web.curation.controller;

import com.web.curation.model.alarm.SocketVO;
import lombok.Data;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@CrossOrigin("*")
@RequestMapping("/alarm")
public class AlarmController {

    // member 별 팔로우 알람을 위한 Map
    Map<String, Set<followingUser>> FollowAlarm = new HashMap<>();

    @Data
    public class followingUser {
        String uid;
        String nickname;

        public followingUser(String uid, String nickname) {
            this.uid = uid;
            this.nickname = nickname;
        }
    }

    @MessageMapping("/first")
    @SendTo("/firstSend")
    public Map<String, Set<followingUser>> first(){
        return FollowAlarm;
    }

    // /receive를 메시지를 받을 endpoint로 설정합니다.
    @MessageMapping("/receive")

    // /send로 메시지를 반환합니다.
    @SendTo("/send")

    // SocketHandler는 1) /receive에서 메시지를 받고, /send로 메시지를 보내줍니다.
    // 정의한 SocketVO를 1) 인자값, 2) 반환값으로 사용합니다.
    public Map<String, Set<followingUser>> SocketHandler(SocketVO socketVO) {

        System.out.println(socketVO);

        String fromMember = socketVO.getMemberName();
        String toMember = socketVO.getFollowingName();

        System.out.println("from : " + fromMember + ", to : " + toMember);

        if(!fromMember.equals("")) {
            // FollowAlarm update
            updateFollow(socketVO);
        }

        System.out.println(FollowAlarm);

        return FollowAlarm;
    }

    private void updateFollow(SocketVO socketVO) {
        // FollowAlarm 에 newMember 가 있으면 해당 리스트를 가져오고 없으면 새로운 리스트 생성
        Set<followingUser> members = FollowAlarm.get(socketVO.getFollowingName()) != null ? FollowAlarm.get(socketVO.getFollowingName()) : new HashSet<>();

        // toMember 를 key로 갖고 있는 Map 이 있다면 팔로우 신청 목록에 fromMember가 있는지 확인한다.
        // (toMember, fromMember) 쌍이 똑같은게 있다면 flag 를 true 로 바꿔주고 Set 에서 삭제한다.
        boolean flag = false;
        String fromMember = socketVO.getMemberName();

        followingUser fU = new followingUser(socketVO.getUid(), fromMember);

        if(members.size() != 0) {
            for (followingUser u : members) {
                if(u.getNickname().equals(fromMember)){
                    members.remove(fU);
                    flag = true;
                    break;
                }
            }
        }

        if(!flag) members.add(fU);

        FollowAlarm.put(socketVO.getFollowingName(), members);
    }



}