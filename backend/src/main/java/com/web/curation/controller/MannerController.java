package com.web.curation.controller;

import com.web.curation.model.BasicResponse;
import com.web.curation.model.member.Manner;
import com.web.curation.model.member.Member;
import com.web.curation.service.MannerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })

@CrossOrigin(origins = "*")
@RestController
public class MannerController {

    @Autowired
    MannerService mannerService;


    @GetMapping("/member/manner")
    @ApiOperation(value = "매너점수")
    public Object mannerScore(@RequestParam final String uid) {

        int manner = mannerService.getManner(uid);

        final BasicResponse result = new BasicResponse();
        result.status = true;
        result.data = "success";

        if (manner < 0) {
            result.status = false;
            result.data = "fail";
        } else {
            result.object = manner;
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/member/manner")
    @ApiOperation(value = "매너점수 추가")
    public Object addManner(@RequestBody Manner manner2) {

        int manner = mannerService.addManner(manner2.getUid(), manner2.getScore());

        final BasicResponse result = new BasicResponse();
        result.status = true;
        result.data = "success";

        if (manner < 0) {
            result.status = false;
            result.data = "fail";
        } else {
            result.object = manner;
        }

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

}