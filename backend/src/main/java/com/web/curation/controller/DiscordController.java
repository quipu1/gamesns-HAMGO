package com.web.curation.controller;

import com.web.curation.service.DiscordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/discord")
@RequiredArgsConstructor
public class DiscordController {
    private final DiscordService discordService;

    @PostMapping("/test2")
    public void create() {
        List<String> list = new ArrayList<>();
        list.add("HETE#3427");
        list.add("조용일#0225");
        list.add("심찬인#3942");
        discordService.createChannel("test", list);
    }

    @GetMapping("/check")
    public Object userCheck(String userTag) {
        return new ResponseEntity<Boolean>(discordService.userCheck(userTag), HttpStatus.OK);
    }

    @DeleteMapping("/test")
    public void delete() {
        discordService.deleteChannel();
    }
}
