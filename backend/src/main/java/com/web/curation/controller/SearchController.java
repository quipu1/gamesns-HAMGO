package com.web.curation.controller;

import com.web.curation.model.BasicResponse;
import com.web.curation.model.board.Board;
import com.web.curation.model.board.ResponseBoard;
import com.web.curation.model.member.Member;
import com.web.curation.service.SearchService;
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
public class SearchController {

    @Autowired
    SearchService searchService;



    @GetMapping("/searchMember")
    @ApiOperation(value = "유저 찾기")
    public Object search(@RequestParam String nickname){

        Optional<Member> memberOpt = searchService.getMember(nickname);

        if(memberOpt.isPresent()) {

            return new ResponseEntity<>(memberOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/search")
    @ApiOperation(value = "유저 찾기")
    public Object searchMember(@RequestParam String nickname){
        //System.out.println(nickname);
        List<Member> memberOpt = searchService.searchMember(nickname);

        for (Member u : memberOpt) {
            System.out.println(u);
        }

        if(memberOpt.size() >= 0) {
            return new ResponseEntity<>(memberOpt, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/search/hashtag")
    @ApiOperation(value = "해시태그 찾기")
    public Object searchHashtag(@RequestParam String hashtag){
        System.out.println(hashtag);
        List<ResponseBoard> hashtagBoard = searchService.searchHashtag(hashtag);

        if(hashtagBoard.size() >= 0) {
            return new ResponseEntity<>(hashtagBoard, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

}