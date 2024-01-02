package com.example.paymentfileupload.user.controller;

import com.example.paymentfileupload.user.model.request.PostCreateUserReq;
import com.example.paymentfileupload.user.model.response.PostCreateUserRes;
import com.example.paymentfileupload.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
        log.info("[UserController] 생성자로 UserService 의존성 주입 완료");
    }

    @PostMapping("/create")
    public ResponseEntity createUser(PostCreateUserReq request) {
        PostCreateUserRes response = userService.createUser(request);
        log.info("[UserController] create user success");
        return ResponseEntity.ok().body(response);
    }


}
