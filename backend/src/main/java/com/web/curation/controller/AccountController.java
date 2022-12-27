package com.web.curation.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;

import com.web.curation.model.BasicResponse;
import com.web.curation.model.member.ImgRequest;
import com.web.curation.model.member.SignupRequest;
import com.web.curation.model.member.Member;

import com.web.curation.service.AccountService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;


@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })

@CrossOrigin(origins = "*")
@RestController
public class AccountController {

    @Autowired
    AccountService service;

//    @GetMapping("/kakaoLogout")
//    @ApiOperation(value = "kakaoLogout")
//    public void klogout(@RequestParam String access_token) {
//
//        service.kakaoLogout(access_token);
//
//    }

    @GetMapping("/kakaoLogin")
    @ApiOperation(value = "카카오 로그인")
    public Object kakaoLogin(@RequestParam("code") String code) {
        String uid = service.kakaoLogin(code);
        System.out.println(uid);

        return new ResponseEntity<>(uid, HttpStatus.OK);
    }

    // 현재 로그인을 요청한 사용자의 정보 가져오기
    @GetMapping("/existUser/{code}")
    public Object getCurMemberInfo(@PathVariable("code") String code) {
        final BasicResponse result = new BasicResponse();

        // 비정상적으로 처리됬을때를 위한 반환값
        result.status = false;
        result.data = "failed";

        Optional<Member> member = service.getUserByCode(code);

        if (member.isPresent()) {
            result.status = true;
            result.data = "success";
            result.object = member.get().getNickname();
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 현재 로그인한 사용자의 정보 가져오기
    @GetMapping("/info/me")
    public Object getCurMemberInfo() {
        final BasicResponse result = new BasicResponse();

        // 비정상적으로 처리됬을때를 위한 반환값
        result.status = false;
        result.data = "failed";

        Optional<Member> member = service.getCurMemberInfo();

        if (member.isPresent()) {
            result.status = true;
            result.data = "success";
            result.object = member.get();
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/info")
    public Object getNickByUid(@RequestParam("uid") String uid) {
        final BasicResponse result = new BasicResponse();

        // 비정상적으로 처리됬을때를 위한 반환값
        result.status = false;
        result.data = "failed";

        Optional<Member> member = service.getMember(uid);

        if (member.isPresent()) {
            result.status = true;
            result.data = "success";
            result.object = member.get();
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    // 원하는 nickname의 유저 정보 가져오기
    @GetMapping("/info/{nickname}")
    public Object getMemberInfo(@PathVariable("nickname") String nickname) {
        final BasicResponse result = new BasicResponse();

        // 비정상적으로 처리됬을때를 위한 반환값
        result.status = false;
        result.data = "failed";

        Optional<Member> member = service.getMemberByNickname(nickname);

        if (member.isPresent()) {
            result.status = true;
            result.data = "success";
            result.object = member.get();
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/dupcheck")
    @ApiOperation(value = "중복체크")
    public Object dupcheck(@RequestParam("nickname") String nickname) {

        final BasicResponse result = new BasicResponse();

        if(service.getUserByNickname(nickname).isPresent()){
            // 중복 => 가입 실패
            result.status = true;
            result.data = "fail";
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            result.status = true;
            result.data = "success";
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    // myPage 에서 회원 정보 수정
    // 처음에 기본이미지로 저장된 이미지를 수정
    @PutMapping("/account/mypageUpdate")
    @ApiOperation(value="닉네임, 이미지 경로 수정")
    public Object imgPut(ImgRequest request
                         ) throws IllegalStateException, IOException {

        System.out.println(request);
//        System.out.println(request.getMultipartFile());
        Optional<Member> memberOpt = service.getMember(request.getUid());

        final BasicResponse result = new BasicResponse();

        if(memberOpt.isPresent()) {

            Member member = memberOpt.get();

            // img 파일 이름 설정 및 경로 지정
            String filePath = "C://upload//";
            String storedFileName = "";

            if(request.getMultipartFile() != null) {
                String originFile = request.getMultipartFile().getOriginalFilename();
                String originalFileExtension = originFile.substring(originFile.lastIndexOf("."));
                storedFileName = request.getUid() + originalFileExtension;
                File file = new File(filePath + storedFileName);

                request.getMultipartFile().transferTo(file);

            }

            if(service.updateMember(member, request, filePath + storedFileName)) {
                result.status = true;
                result.data = "success";
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                result.status = true;
                result.data = "fail";
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        } else{
            result.status = true;
            result.data = "fail";
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/account/file/{uid}")
    @ApiOperation(value = "내파일")
    public Object bFile(@PathVariable final String uid, HttpServletRequest request) throws MalformedURLException{
    	Optional<Member> member = service.getMember(uid);

    	Resource resource;

		if(member.get().getPimg() == null) {

		    resource = new FileSystemResource("C://upload/default.png");

			final BasicResponse result = new BasicResponse();
			result.status = true;
	        result.data = "success";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "default" + "\"")
                    .body(resource);
        } else {
		    System.out.println("있다있어!!" + uid);
            resource =  new FileSystemResource(member.get().getPimg());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }

    }
}