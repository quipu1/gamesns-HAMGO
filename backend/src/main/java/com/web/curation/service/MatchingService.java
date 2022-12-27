package com.web.curation.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.PostConstruct;

import com.web.curation.dao.matching.MatchingDao;
import com.web.curation.model.matching.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.web.curation.model.matching.MatchingResponse.ResponseResult;

@Service
public class MatchingService {

    private static final Logger logger = LoggerFactory.getLogger(MatchingService.class);
    
    private Map< String ,Map<MatchingRequest, DeferredResult<MatchingResponse>>> waitingQueue;
    
    
    // hashTable 사용 쓰레드 세이프하게 
    // watingUser (게임이름, SET)
    
    
    // {key : websocket session id, value : chat room id}
    private Map<String, String> connectedUsers;
    private ReentrantReadWriteLock lock;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private DiscordService discordService;

    @PostConstruct
    private void setUp() {
        //this.waitingUsers = new LinkedHashMap<>();//순서가 지켜지는
        this.lock = new ReentrantReadWriteLock();
        this.connectedUsers = new ConcurrentHashMap<>();
        this.waitingQueue = new Hashtable<String, Map<MatchingRequest,DeferredResult<MatchingResponse>>>();
    }

    @Async("asyncThreadPool")
    public void joinChatRoom(MatchingRequest request, DeferredResult<MatchingResponse> deferredResult) {
        logger.info("## Join chat room request. {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());
        if (request == null || deferredResult == null) {
            return;
        }
        try {
            lock.writeLock().lock();
            if(waitingQueue.containsKey(request.getKey())) {//이미 생성된 키가 있으면
            	waitingQueue.get(request.getKey()).put(request,deferredResult);
            } else {
            	Map<MatchingRequest, DeferredResult<MatchingResponse>> newUserPool = new LinkedHashMap<>();
            	newUserPool.put(request, deferredResult);
            	waitingQueue.put(request.getKey(),newUserPool);
            }
            //waitingUsers.put(request, deferredResult);
        } finally {
            lock.writeLock().unlock();
            establishMatchingRoom(request);
        }
    }

    public void cancelChatRoom(MatchingRequest matchingRequest) {
        try {
            lock.writeLock().lock();
            setJoinResult(waitingQueue.get(matchingRequest.getKey()).remove(matchingRequest), 
            		new MatchingResponse(ResponseResult.CANCEL, null, matchingRequest.getSessionId()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void timeout(MatchingRequest matchingRequest) {
        try {
            lock.writeLock().lock();
            setJoinResult(waitingQueue.get(matchingRequest.getKey()).remove(matchingRequest), 
            		new MatchingResponse(ResponseResult.TIMEOUT, null, matchingRequest.getSessionId()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void establishMatchingRoom(MatchingRequest request) {//웨이팅 큐
        try {
            logger.debug("Current waiting users : " + waitingQueue.get(request.getKey()).size());
            lock.readLock().lock();
            if (waitingQueue.get(request.getKey()).size() < Integer.parseInt(request.getPeopleLimit())) {//유저가 특정수 이하 면 컷
                return;
            }

            Iterator<MatchingRequest> itr = waitingQueue.get(request.getKey()).keySet().iterator();
            
            List<MatchingRequest> roomUserKey = new ArrayList<>();
            List<DeferredResult<MatchingResponse>> roomUserValue = new ArrayList<>();
            for (int i = 0; i < Integer.parseInt(request.getPeopleLimit()); i++) {
            	roomUserKey.add(itr.next());
			}

            // 여기서 계정정보를 디코로 넘겨서
            // 디스코드가 생성된 방 주소를 준다.
            List<String> userTagList = new ArrayList<String>();
            for (MatchingRequest matchingRequest : roomUserKey) {
                userTagList.add(matchingRequest.getDiscordId());
            }

            // 방을 생성하고 초대코드를 가져온다
            Optional<String> getUrl = discordService.createChannel("hamgo?", userTagList);

            String url = "";
            if (getUrl.isPresent()) {
                System.out.println(url);
                url = getUrl.get();
            } else {
                // url을 못갖고 오는 경우를 처리
                return;
            }
            //MatchingRequest user1 = itr.next();
            //MatchingRequest user2 = itr.next();

            String uuid = UUID.randomUUID().toString(); //채팅방 이름 생성.
            
            for (int i = 0; i < Integer.parseInt(request.getPeopleLimit()); i++) {
            	roomUserValue.add(waitingQueue.get(request.getKey()).remove(roomUserKey.get(i)));
			}
            
            //DeferredResult<MatchingResponse> user1Result = waitingUsers.remove(user1);
            //DeferredResult<MatchingResponse> user2Result = waitingUsers.remove(user2);
            
            
            for (int i = 0; i < Integer.parseInt(request.getPeopleLimit()); i++) {
            	roomUserValue.get(i).setResult(new MatchingResponse(ResponseResult.SUCCESS, uuid, roomUserKey.get(i).getSessionId(), url, roomUserKey));
			}
            
            //user1Result.setResult(new MatchingResponse(ResponseResult.SUCCESS, uuid, user1.getSessionId()));
            //user2Result.setResult(new MatchingResponse(ResponseResult.SUCCESS, uuid, user2.getSessionId()));
            
        } catch (Exception e) {
            logger.warn("Exception occur while checking waiting users", e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void sendMessage(String matchingRoomId, MatchingMessage matchingMessage) {
        String destination = getDestination(matchingRoomId);
        messagingTemplate.convertAndSend(destination, matchingMessage);
    }

    public void connectUser(String matchingRoomId, String websocketSessionId) {
        connectedUsers.put(websocketSessionId, matchingRoomId);
    }

    public void disconnectUser(String websocketSessionId) {
    	System.out.println("종료 시도:"+connectedUsers.get(websocketSessionId));
        String matchingRoomId = connectedUsers.get(websocketSessionId);
        MatchingMessage matchingMessage = new MatchingMessage();

        matchingMessage.setMessageType(MessageType.DISCONNECTED);
        sendMessage(matchingRoomId, matchingMessage);
    }

    private String getDestination(String matchingRoomId) {
        return "/topic/chat/" + matchingRoomId;
    }

    private void setJoinResult(DeferredResult<MatchingResponse> result, MatchingResponse response) {
        if (result != null) {
            result.setResult(response);
        }
    }

    @Autowired
    MatchingDao matchingDao;

    public List<String> getGames(){

        Set<String> game = new HashSet<>();

        for(Game g : matchingDao.findAll()) {
            game.add(g.getGameName());
        }

        return new ArrayList<>(game);
    }

    public List<Game> getPeople(String game){
        try {
            return matchingDao.findByGameName(game);
        } catch (Exception e) {
            return null;
        }
    }



}