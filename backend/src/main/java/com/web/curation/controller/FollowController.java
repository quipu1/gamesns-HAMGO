package com.web.curation.controller;

import com.web.curation.model.BasicResponse;
import com.web.curation.model.follow.FollowRequest;
import com.web.curation.model.follow.Follower;
import com.web.curation.model.follow.Following;
import com.web.curation.model.member.Member;
import com.web.curation.service.AccountService;
import com.web.curation.service.FollowService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })

@CrossOrigin(origins = "*")
@RestController
public class FollowController {

    @Autowired
    AccountService accountService;

    @Autowired
    FollowService followService;

    @GetMapping("/follow/follower")
    @ApiOperation(value = "팔로워리스트")
    public Object follower(@RequestParam final String to) {

        Optional<Member> member = accountService.getMember(to);

        if(member.isPresent()) {

            List<Member> fList = followService.getFollower(to);
            final BasicResponse result = new BasicResponse();
            result.status = true;
            result.data = "success";
            result.object = fList;
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            final BasicResponse result = new BasicResponse();
            result.status = true;
            result.data = "fail";
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/follow/following")
    @ApiOperation(value = "팔로잉리스트")
    public Object following(@RequestParam final String from) {

        Optional<Member> member = accountService.getMember(from);

        if(member.isPresent()) {

            List<Member> fList = followService.getFollowing(from);
            final BasicResponse result = new BasicResponse();
            result.status = true;
            result.data = "success";
            result.object = fList;
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            final BasicResponse result = new BasicResponse();
            result.status = true;
            result.data = "fail";
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/follow/AddOrDeleteFollowing")
    @ApiOperation(value = "팔로잉 추가/삭제")
    public Object AddOrDeleteFollowing(@RequestBody FollowRequest followRequest) {

        boolean flag = followService.AddOrDeleteFollowing(followRequest.getFromNickname(), followRequest.getToNickname());
        final BasicResponse result = new BasicResponse();
        result.status = true;
        result.data = "success";
        result.object = flag;
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/follow/AddOrDeleteFollower")
    @ApiOperation(value = "팔로워 추가/삭제")
    public Object AddOrDeleteFollower(@RequestBody FollowRequest followRequest) {

        boolean flag = followService.AddOrDeleteFollower(followRequest.getFromNickname(), followRequest.getToNickname());

        final BasicResponse result = new BasicResponse();
        result.status = true;
        result.data = "success";
        result.object = flag;
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}