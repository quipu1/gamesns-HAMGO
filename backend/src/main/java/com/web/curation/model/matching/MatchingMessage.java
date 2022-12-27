package com.web.curation.model.matching;

import lombok.Data;

@Data
public class MatchingMessage {

    private String senderSessionId;
    private String message;
    private MessageType messageType;
    
	public MatchingMessage(String senderSessionId, String message, MessageType messageType) {
		super();
		this.senderSessionId = senderSessionId;
		this.message = message;
		this.messageType = messageType;
	}

	public MatchingMessage() {
		super();
	}

}
