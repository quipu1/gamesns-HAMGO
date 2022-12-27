// 하단 DB 설정 부분은 Sub PJT II에서 데이터베이스를 구성한 이후에 주석을 해제하여 사용.

package com.web.curation.model.member;

import lombok.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Member {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;
    private String uid;
    private String nickname;
    private String pimg;

    private Integer manner;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createDate;

    @Builder
    public Member(String uid, String nickname, Authority authority) {
        this.uid = uid;
        this.nickname = nickname;
        this.authority = authority;
    }
    
}
