package com.web.curation.model.board;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.curation.model.file.ImgFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBoard {
	
    private Long bid;
    private String uid;
    private String nickname;
    private String contents;
    private String hashtags;

    private int likes;
    private int share;
    
    private List<ImgFile> imgFiles;
    
    public ResponseBoard(Board board, List<ImgFile> imgFiles,String nickname) {
		this.bid = board.getBid();
		this.uid = board.getUid();
		this.nickname = nickname;
		this.contents = board.getContents();
		this.likes = board.getLikes();
		this.share = board.getShare();
		this.imgFiles = imgFiles;
		this.createDate = board.getCreateDate();
		this.hashtags = board.getHashtags();
	}

    private LocalDateTime createDate;
}