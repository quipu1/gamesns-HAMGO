package com.web.curation.model.board;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

@Valid
@Data
@ToString
public class AddBoard {
	
    @ApiModelProperty(required = true)
    @NotNull
    String uid;
    @ApiModelProperty(required = true)
    @NotNull
    String content;

    @ApiModelProperty(required = true)
    @NotNull
    String hashtags;

    @ApiModelProperty(required = false)
    MultipartFile[] multipartFiles;
    
}
