package com.web.curation.controller;

import com.web.curation.model.BasicResponse;
import com.web.curation.model.board.BoardLikeMember;
import com.web.curation.service.LikeService;
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
public class LikeController {

    @Autowired
    LikeService likeService;


    @GetMapping("/board/like")
    @ApiOperation(value = "좋아요 리스트")
    public Object likeList(@RequestParam final Long bid) {

        Optional<List<BoardLikeMember>> LList = likeService.getLike(bid);

        if (LList.isPresent()) return new ResponseEntity<>(LList.get(), HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.OK);

    }

    @GetMapping("/board/liked")
    @ApiOperation(value = "새로고침 좋아요")
    public Object Liked(@RequestParam Long bid, @RequestParam String uid) {

        System.out.println();

        int flag = likeService.Liked(bid, uid);

        final BasicResponse result = new BasicResponse();
        result.status = true;
        result.data = "success";
        result.object = flag;
        return new ResponseEntity<>(result, HttpStatus.OK);

    }


    @PostMapping("/board/AddOrDeleteLike")
    @ApiOperation(value = "좋아요 추가")
    public Object AddOrDeleteLike(@RequestBody BoardLikeMember boardLikeMember) {

        int flag = likeService.AddOrDeleteLike(boardLikeMember.getBid(), boardLikeMember.getUid());

        final BasicResponse result = new BasicResponse();
        result.status = true;
        result.data = "success";
        result.object = flag;
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

}