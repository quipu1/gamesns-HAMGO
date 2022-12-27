package com.web.curation.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() { }

    /**
     * JwtFilter에서 SecurityContext에 세팅한 유저 정보를 꺼낸다.
     * SecurityContext는 ThreadLocal에 사용자의 정보를 저장
     *
     * @return
     */
    public static String getCurrentMemberUid() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        return authentication.getName();
    }

}
