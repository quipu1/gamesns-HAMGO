package com.web.curation.service;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.curation.model.OAuthToken;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuth2Kakao {
    // POST 방식으로 key-value 데이터를 요청
    public OAuthToken getAccessToken(String authorizeCode) {
        // http 요청을 간단하게 처리해주는 클래스
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        // REST api 키 주의!!
        params.add("client_id", "d9244e59520303e14f40a6a210aae21d");
        params.add("redirect_url", "http://localhost:8080/kakaoLogin");
        params.add("code", authorizeCode);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담는다.
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        String url = "https://kauth.kakao.com/oauth/token";
        try {
            // 실제로 요청하기
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            // 요청하여 반환된 값을 해당 오브젝트에 맞게 key, value값을 매칭
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oAuthToken;
    }

    // AccessToken을 사용하여 유저정보 받기
    public String getMemberByAccessToken(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<String> response = null;

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        String url = "https://kapi.kakao.com/v2/user/me";
        JSONObject parser = null;
        try {
            // 실제 요청
            response = restTemplate.postForEntity(url, request, String.class);
            parser = new JSONObject(response.getBody());
        } catch (RestClientException e) {
            e.printStackTrace();
        }

        System.out.println("KAKAO user info parser : " + parser);

        return String.valueOf(parser.getBigInteger("id"));
    }
}
