package com.web.curation.controller;

import com.web.curation.model.BasicResponse;
import com.web.curation.model.matching.*;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.web.curation.service.MatchingService;
import com.web.curation.util.ServletUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/matching")
public class MatchingController {

    private static final Logger logger = LoggerFactory.getLogger(MatchingController.class);

    @Autowired
    private MatchingService matchingService;

    @GetMapping("/games")// 게임 별 인원 수
    @ApiOperation(value = "게임 목록")
    public Object getGames(){
        List<String> games = matchingService.getGames();
        System.out.println("hi games : " + games);
        final BasicResponse result = new BasicResponse();

        result.status = true;
        if(games != null) {
            result.data = "success";
            result.object = games;
        } else {
            result.data = "fail";
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/people")// 게임 별 인원 수
    @ApiOperation(value = "게임 별 인원 수")
    public Object getPeople(@RequestParam String game) {
//        System.out.println("game : " + game);
        List<Game> people = matchingService.getPeople(game);
//        System.out.println("people : " + people);
        final BasicResponse result = new BasicResponse();

        result.status = true;
        if(people != null){
            result.data = "success";
            result.object = people;
        } else {
            result.data = "fail";
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // tag :: async
    @GetMapping("/join")//채팅방 요청.
    @ResponseBody
    public DeferredResult<MatchingResponse> joinRequest(@RequestParam String gameName, @RequestParam String peopleLimit, @RequestParam String discordId, @RequestParam String uid) {
        String sessionId = ServletUtil.getSession().getId();
        logger.info(">> Join request. session id : {}", sessionId);
        final MatchingRequest user = new MatchingRequest(sessionId,gameName,peopleLimit,discordId,uid); //게임 이름을 합쳐서 새로운 생성
        final DeferredResult<MatchingResponse> deferredResult = new DeferredResult<>(null); //비동기 프로세스 생성.
        matchingService.joinChatRoom(user, deferredResult); //채팅룸 생성으로 보임. 정보확인 필요.

        deferredResult.onCompletion(() -> matchingService.cancelChatRoom(user));//에러 확인되면 채팅룸 닫기.
        deferredResult.onError((throwable) -> matchingService.cancelChatRoom(user));
        deferredResult.onTimeout(() -> matchingService.timeout(user));

        return deferredResult;
    }

    @GetMapping("/cancel")
    @ResponseBody
    public ResponseEntity<Void> cancelRequest(@RequestParam String gameName, @RequestParam String peopleLimit,@RequestParam String discordId,@RequestParam String uid) {
        String sessionId = ServletUtil.getSession().getId();
        	
        logger.info(">> Cancel request. session id : {}", sessionId);
        //System.out.println("캔슬 들어올 때" + gameName+" " + sessionId);
        final MatchingRequest user = new MatchingRequest(sessionId,gameName,peopleLimit,discordId,uid);
        matchingService.cancelChatRoom(user);

        return ResponseEntity.ok().build();
    }

    // -- tag :: async
    // tag :: websocket stomp
    @MessageMapping("/chat.message/{matchingRoomId}")// 채팅하는 곳.
    public void sendMessage(@DestinationVariable("matchingRoomId") String matchingRoomId, @Payload MatchingMessage matchingMessage) {
        //logger.info("Request message. roomd id : {} | chat message : {} | principal : {}", chatRoomId, chatMessage);
        if (!StringUtils.hasText(matchingRoomId) || matchingMessage == null) {
            return;
        }
        
        if (matchingMessage.getMessageType() == MessageType.CHAT) {
            matchingService.sendMessage(matchingRoomId, matchingMessage);
        }
    }
    // -- tag :: websocket stomp
}