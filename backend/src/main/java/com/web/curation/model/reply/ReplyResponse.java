package com.web.curation.model.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Valid
@ToString
public class ReplyResponse {
	
    private Long rid;
    // member id
    private String uid;
    // board id
    private Long bid;
    
    private String nickname;
    
    private String content;

    private int cnt;

    private LocalDateTime regDate;

	public ReplyResponse(Reply reply, String nickname) {
		super();
		this.rid = reply.getRid();
		this.uid = reply.getUid();
		this.bid = reply.getBid();
		this.nickname = nickname;
		this.content = reply.getContent();
		this.cnt = reply.getCnt();
		this.regDate = reply.getRegDate();
	}
    
    
    
}



