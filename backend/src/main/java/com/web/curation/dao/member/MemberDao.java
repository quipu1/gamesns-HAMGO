
package com.web.curation.dao.member;

import java.util.List;
import java.util.Optional;

import com.web.curation.model.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDao extends JpaRepository<Member, Long> {

    Optional<Member> findByUid(String uid);

    Optional<Member> findMemberByUid(String uid);

    Optional<Member> findMemberByNickname(String nickname);
    
    List<Member> findMemberByNicknameContaining(String nickname);

    boolean existsByUid(String uid);

    boolean existsByNickname(String nickname);
    
}
