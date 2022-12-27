package com.web.curation.model.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// 소켓을 통해 통신하기 위한 class
public class SocketVO {
    // 유저의 이름을 저장하기 위한 변수
    private String memberName;

    // 유저의 아이디를 저장하기 위한 변수
    private String uid;

    // 팔로잉한 유저의 이름을 저장하기 위한 변수
    private String followingName;
}
