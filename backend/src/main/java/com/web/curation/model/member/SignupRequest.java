package com.web.curation.model.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Valid
@ToString
public class SignupRequest {
    @ApiModelProperty(required = true)
    @NotNull
    String uid;
    @ApiModelProperty(required = true)
    @NotNull
    String nickname;
//    @ApiModelProperty(required = true)
//    @NotNull
//    String authority;
}
