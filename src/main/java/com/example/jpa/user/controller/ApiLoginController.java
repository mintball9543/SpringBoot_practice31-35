package com.example.jpa.user.controller;

import com.example.jpa.common.exception.BizException;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.model.UserLogin;
import com.example.jpa.user.model.UserLoginToken;
import com.example.jpa.user.service.UserService;
import com.example.jpa.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class ApiLoginController {

    private final UserService userService;

    // Q83
    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLogin userLogin, Errors errors) {

        if (errors.hasErrors()) {
            return ResponseResult.fail("입력값이 정확하지 않습니다.", ResponseError.of(errors.getAllErrors()));
        }

        User user = null;
        try {
            user = userService.login(userLogin);
        } catch (BizException e) {
            return ResponseResult.fail(e.getMessage());
        }
        UserLoginToken userLoginToken = JWTUtils.createToken(user);

        if (userLoginToken == null) {
            return ResponseResult.fail("JWT 생성에 실패하였습니다.");
        }
        return ResponseResult.success(userLoginToken);
    }
}
