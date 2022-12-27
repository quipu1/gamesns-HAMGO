package com.web.curation.model.matching;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchingRequest {

    private String sessionId;
    private String gameName;
    private String peopleLimit;
    private String discordId;
    private String uid;
    private String key;
    
	public MatchingRequest(String sessionId, String gameName, String peopleLimit, String discordId ,String uid) {
		super();
		this.sessionId = sessionId;
		this.gameName = gameName;
		this.peopleLimit = peopleLimit;
	    this.discordId = discordId;
		this.uid = uid;
		this.key = this.gameName + this.peopleLimit;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchingRequest other = (MatchingRequest) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}
	
}
