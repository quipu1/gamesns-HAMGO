package com.web.curation.controller;

import com.web.curation.model.BasicResponse;
import com.web.curation.model.common.MetadataResponse;
import com.web.curation.service.CommonService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;

    @GetMapping("/getMeta")
    @ApiOperation(value = "메타데이터 조회")
    public Object getMeta(@RequestParam String url) {
        final BasicResponse result = new BasicResponse();
        // 비정상적으로 처리됬을때를 위한 반환값
        result.status = false;
        result.data = "failed";

        try {
            Optional<MetadataResponse> getMetadata = commonService.getMetaData(url);

            if (getMetadata.isPresent()) {
                result.status = true;
                result.data = "success";
                result.object = getMetadata.get();
            }
        } catch (IllegalArgumentException e) {
            // 예외처리
            result.data = "잘못된 URL 입니다.";
        } catch (Exception e) {
            result.data = "문제가 발생했습니다. URL을 확인해주세요.";
            e.printStackTrace();
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
