package com.web.curation.controller;

import com.web.curation.model.BasicResponse;
import com.web.curation.model.board.AddBoard;
import com.web.curation.service.BoardService;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })

@CrossOrigin(origins = "*")
@RestController
public class BoardController {
    
    @Autowired
    BoardService boardService;
    
    @GetMapping("/board")
    @ApiOperation(value = "내 피드")
    public Object bList(@RequestParam(required = true) final String uid, @RequestParam(required = false) String bid){
		final BasicResponse result = new BasicResponse();
		
		try {
			result.object = boardService.bList(uid, bid);
			result.status = true;
	        result.data = "success";
		} catch (Exception e) {
			// TODO: handle exception
			result.status = false;
	        result.data = "failed";
	        System.out.println(e);
		}
		
        return new ResponseEntity<>(result, HttpStatus.OK);   
    }
    
    @GetMapping("/board/user")
    @ApiOperation(value = "유저페이지 노출용 특정 유저 한명의 피드")
    public Object bEqualList(@RequestParam(required = true) final String uid, @RequestParam(required = false) final String bid){
		final BasicResponse result = new BasicResponse();
		System.out.println(uid + " " + bid);
		try {
			result.object = boardService.bEqualList(uid,bid);
			result.status = true;
	        result.data = "success";
		} catch (Exception e) {
			// TODO: handle exception
			result.status = false;
	        result.data = "failed";
	        System.out.println(e);
		}
		
        return new ResponseEntity<>(result, HttpStatus.OK);   
    }
    
    @GetMapping("/board/file/{fileName}")
    @ApiOperation(value = "내파일")
    public Object bFile(@PathVariable final String fileName, HttpServletRequest request) throws MalformedURLException{
		Resource resource =  new FileSystemResource("C://upload//"+fileName);
		
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
    
    @PostMapping(value="/board")
    @ApiOperation(value="추가하기")
    public Object addBoard(AddBoard newBoard) {
    	final BasicResponse result = new BasicResponse();
    	System.out.println(newBoard.toString());
    	try {
    		boardService.addBoard(newBoard);
    		result.status = true;
            result.data = "success";
    		
		} catch (Exception e) {
			result.status = false;
	        result.data = "failed";
	        System.out.println(e);
		}

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping(value="/board/{bid}")
    @ApiOperation(value="수정하기")
    public Object modifyBoard(@PathVariable("bid") long bid ,AddBoard newBoard){
    	
    	final BasicResponse result = new BasicResponse();
    	
    	try {
    		boardService.modifyBoard(bid, newBoard);
    		result.status = true;
            result.data = "success";
    		
		} catch (Exception e) {
			result.status = false;
	        result.data = "failed";
	        System.out.println(e);
		}
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @DeleteMapping(value="/board/{bid}")
    @ApiOperation(value="삭제하기")
    public Object deleteBoard(@PathVariable("bid") long bid){
    	
    	final BasicResponse result = new BasicResponse();
    	try {
    		boardService.deleteBoard(bid);
    		result.status = true;
            result.data = "success";
    		
		} catch (Exception e) {
			result.status = false;
	        result.data = "failed";
	        System.out.println(e);
		}
    	
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
}