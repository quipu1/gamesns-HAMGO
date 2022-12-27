package com.web.curation.controller;

import com.web.curation.model.BasicResponse;
import com.web.curation.model.badge.Badge;
import com.web.curation.model.member.Manner;
import com.web.curation.model.member.Member;
import com.web.curation.service.BadgeService;
import com.web.curation.service.MannerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;


@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })

@CrossOrigin(origins = "*")
@RestController
public class BadgeController {

    @Autowired
    BadgeService badgeService;

    @GetMapping("/member/badge")
    @ApiOperation(value = "뱃지")
    public Object getBadge(@RequestParam final String uid) {

        List<Badge> badge = badgeService.badgeList(uid);

        final BasicResponse result = new BasicResponse();
        result.status = true;
        result.data = "success";
        result.object = badge;
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/badge/file/{fileName}")
    @ApiOperation(value = "내파일")
    public Object bFile(@PathVariable final String fileName, HttpServletRequest request) throws MalformedURLException {
        Resource resource =  new FileSystemResource("C://upload//Badge//"+fileName+".png");

        if(fileName == null) {
            final BasicResponse result = new BasicResponse();
            result.status = true;
            result.data = "success";
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
