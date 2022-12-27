package com.web.curation.model.matching;

import java.util.List;

public class MatchingResponse {

    private ResponseResult responseResult;
    private String chatRoomId;
    private String sessionId;
    private String discordUrl;
    private List<MatchingRequest> matchedUser;
    
    public MatchingResponse() {
    }

    public MatchingResponse(ResponseResult responseResult, String chatRoomId, String sessionId) {
        this.responseResult = responseResult;
        this.chatRoomId = chatRoomId;
        this.sessionId = sessionId;
    }
    
    public MatchingResponse(ResponseResult responseResult, String chatRoomId, String sessionId, String discordUrl,
			List<MatchingRequest> matchedUser) {
		super();
		this.responseResult = responseResult;
		this.chatRoomId = chatRoomId;
		this.sessionId = sessionId;
		this.discordUrl = discordUrl;
		this.matchedUser = matchedUser;
	}

    public String getSessionId() {
        return sessionId;
    }

	public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public ResponseResult getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ResponseResult responseResult) {
        this.responseResult = responseResult;
    }


    public List<MatchingRequest> getMatchedUser() {
		return matchedUser;
	}

	public void setMatchedUser(List<MatchingRequest> matchedUser) {
		this.matchedUser = matchedUser;
	}

	public String getDiscordUrl() { return discordUrl; }

    public void setDiscordUrl(String discordUrl) { this.discordUrl = discordUrl; }

	@Override
	public String toString() {
		return "MatchingResponse [responseResult=" + responseResult + ", chatRoomId=" + chatRoomId + ", sessionId="
				+ sessionId + ", matchedUser=" + matchedUser + "]";
	}


	public enum ResponseResult {
        SUCCESS, CANCEL, TIMEOUT;
    }
}
