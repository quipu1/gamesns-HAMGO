package com.web.curation.config.matching;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.web.curation.service.MatchingService;

@Component
public class MachingEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MachingEventListener.class);

    @Autowired
    private MatchingService matchingService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        GenericMessage<?> generic = (GenericMessage<?>) accessor.getHeader("simpConnectMessage");
        
        
        
        Map<String, Object> nativeHeaders = (Map<String, Object>) generic.getHeaders().get("nativeHeaders");
        
        String matchingRoomId;
        if(nativeHeaders.containsKey("matchingRoomId")) {
        	matchingRoomId = ((List<String>) nativeHeaders.get("matchingRoomId")).get(0);
        } else {
        	return;
        }
        String sessionId = (String) generic.getHeaders().get("simpSessionId");

        /*StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("## headerAccessor :: " + headerAccessor);
        String chatRoomId = headerAccessor.getNativeHeader("chatRoomId").get(0);
        String sessionId = headerAccessor.getSessionId();*/
        System.out.println(nativeHeaders);
        System.out.println(matchingRoomId + " " + sessionId);
        
        logger.info("[Connected] room id : {} | websocket session id : {}", matchingRoomId, sessionId);
        
        matchingService.connectUser(matchingRoomId, sessionId);//여기서 게임 종류 인원에 맞는 작업을 가져와야함
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();

        logger.info("[Disconnected] websocket session id : {}", sessionId);

        matchingService.disconnectUser(sessionId);
    }
}
