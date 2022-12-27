package com.web.curation.service;

import com.web.curation.config.security.TokenProvider;
import com.web.curation.dao.member.MemberDao;
import com.web.curation.model.jwt.TokenDto;
import com.web.curation.model.member.Authority;
import com.web.curation.model.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberDao memberDao;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 회원가입을 처리한다
     * 성공 시 true를 실패 시 false를 반환
     *
     * @param uid
     * @param nickname
     * @return
     */
    @Transactional
    public boolean signup(String uid, String nickname) {
        if (memberDao.existsByUid(uid) || memberDao.existsByNickname(nickname)) {
            return false;
        }

        Member member = new Member();
        member.setUid(uid);
        member.setNickname(nickname);
        member.setAuthority(Authority.ROLE_USER);
        member.setPassword(passwordEncoder.encode(uid));
        member.setManner(200);

        System.out.println(member);

        try {
            memberDao.save(member);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 로그인을 처리한다
     * 성공하면 access token을 반환
     * 
     * @param uid
     * @return
     */
    @Transactional
    public Optional<TokenDto> login(String uid) {
        // Login ID / PW 기반으로 AuthenticationToken 생성, 여기선 pid를 id와 password로 사용했음을 주의
        // 또한, password로 들어오는 uid는 passwordEncoder로 처리가안된 값이 들어와야한다.
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(uid, uid);

        // 실제 검증이 이루어진다
        // autheticate 메서드가 실행될 때, CustomUserDetailsService에서 만들었던 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        String key = authentication.getName();
        redisTemplate.opsForValue().set(key, tokenDto.getRefreshToken());
        System.out.println(tokenDto.getRefreshTokenExpiresIn());
        redisTemplate.expire(key, 60 * 60 * 24 * 7, TimeUnit.SECONDS);

//        refreshTokenDao.save(refreshToken);
        System.out.println(tokenDto);
        Optional<TokenDto> returnToken = Optional.ofNullable(tokenDto);

        return returnToken;
    }

    /**
     * Aceess Token과 Refresh Token을 받아 검증을 하고
     * Access Token이 만료됬다면 이를 복호화 하여 유저 정보(nickname)을 가져오고 저장소에 있는
     * Refresh Token이 만료되었는지를 검사하여 만료가 안됬고 정보가 일치한다면 새로운 토큰을 생성하고 DB에 저장하고
     * Access Token을 반환
     *
     * at = access token
     * rt = refresh token
     *
     * @param at
     * @return
     */
    @Transactional
    public Optional<TokenDto> reissuance(String at) {
        // Access Token에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(at);

        System.out.println("reissuance id : " + authentication.getName());

        // 저장소에서 Member nickname을 기반으로 Refresh token 값을 가져온다.
        String key = authentication.getName();
        String getRefreshToken = (String)redisTemplate.opsForValue().get(key);
        System.out.println(getRefreshToken);

        if (getRefreshToken == null || getRefreshToken.equals("")
                || getRefreshToken.length() == 0 || redisTemplate.opsForValue().get(at) != null) {
            throw new RuntimeException("로그아웃 된 사용자입니다.");
        }

        // Refresh Token 검증
        if (!tokenProvider.validateToken(getRefreshToken)) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 저장소 정보 업데이트
        redisTemplate.opsForValue().set(key, tokenDto.getRefreshToken());
        redisTemplate.expire(key, 60 * 60 * 24 * 7, TimeUnit.SECONDS);

        Optional<TokenDto> returnToken = Optional.ofNullable(tokenDto);


        System.out.println("==============================================");
        System.out.println("토큰 재발급 완료");
        System.out.println("uid = " + key);
        System.out.println("AccessToken = " + tokenDto.getAccessToken());
        System.out.println("refreshToken = " + tokenDto.getRefreshToken());
        System.out.println("==============================================");

        // 토큰 발급
        return returnToken;
    }

    /**
     * 로그아웃을 처리한다
     * 로그아웃이 이미 되었다면 false를 반환
     * 로그아웃이 가능하다면 redis에 access token이 키 값인 set을 넣어
     * 로그아웃한 토큰임을 남긴다
     *
     * at = access token
     * rt = refresh token
     *
     * @param at
     * @return
     */
    @Transactional
    public boolean logout(String at) {
        Authentication authentication = tokenProvider.getAuthentication(at);
        String key = authentication.getName();

        // 권한이 없는 경우
        if (!tokenProvider.validateToken(at)) {
            return false;
        }

        String getRefreshToken = (String)redisTemplate.opsForValue().get(key);

        // 이미 로그아웃된 사용자
        if (getRefreshToken == null || getRefreshToken.length() <= 0
                || getRefreshToken.equals("") || redisTemplate.opsForValue().get(at) != null) {
            return false;
        }

        Date expireDate = tokenProvider.getExpireDate(at);
        System.out.println(expireDate.getTime());
        redisTemplate.opsForValue().set(at, "logout");
        // 액세스 토큰의 남은 시간만큼 logout blacklist에 추가하여 로그아웃이 된 토큰임을 저장한다.
        redisTemplate.expire(at, expireDate.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

        return true;
    }
}
