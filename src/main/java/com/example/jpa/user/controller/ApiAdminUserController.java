package com.example.jpa.user.controller;

import com.example.jpa.notice.repository.NoticeRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.entity.UserLoginHistory;
import com.example.jpa.user.model.*;
import com.example.jpa.user.repository.UserLoginHistoryRepository;
import com.example.jpa.user.repository.UserRepository;
import com.example.jpa.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class ApiAdminUserController {
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final UserLoginHistoryRepository userLoginHistoryRepository;
    private final UserService userService;

    // Q48
//    @GetMapping("/api/admin/user")
//    public ResponseMessage userList(){
//        List<User> userList = userRepository.findAll();
//        long totalUserCount = userRepository.count();
//
//        return ResponseMessage.builder()
//                .totalCount(totalUserCount)
//                .data(userList)
//                .build();
//    }


    // Q49
    @GetMapping("/api/admin/user/{id}")
    public ResponseEntity<?> userDetail(@PathVariable Long id){
        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 존재하지 않습니다."), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(ResponseMessage.success(user));
    }


    // Q50
    @GetMapping("/api/admin/user/search")
    public ResponseEntity<?> findUser(@RequestBody UserSearch userSearch){
        List<User> userList =
                userRepository.findByEmailContainsOrPhoneContainsOrUserNameContains(userSearch.getEmail(), userSearch.getPhone(), userSearch.getUserName());

        return ResponseEntity.ok().body(ResponseMessage.success(userList));
    }

    // Q51
    @PatchMapping("/api/admin/user/{id}/status")
    public ResponseEntity<?> userStatus(@PathVariable Long id, @RequestBody UserStatusInput userStatusInput) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 존재하지 않습니다."), HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        user.setStatus(userStatusInput.getStatus());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // Q52
    @DeleteMapping("/api/admin/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 존재하지 않습니다."), HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if (noticeRepository.countByUser(user) > 0) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자가 작성한 공지사항이 있습니다."), HttpStatus.BAD_REQUEST);
        }

        userRepository.delete(user);
        return ResponseEntity.ok().build();
    }

    // Q53
    @GetMapping("/api/admin/user/login/history")
    public ResponseEntity<?> userLoginHistory() {

        List<UserLoginHistory> userLoginHistories = userLoginHistoryRepository.findAll();

        return ResponseEntity.ok().body(userLoginHistories);
    }

    // Q54
    @PatchMapping("/api/admin/user/{id}/lock")
    public ResponseEntity<?> userLock(@PathVariable Long id) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 존재하지 않습니다."), HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if (user.isLockYn()) {
            return new ResponseEntity<>(ResponseMessage.fail("이미 접속제한이 된 사용자 입니다."), HttpStatus.BAD_REQUEST);
        }

        user.setLockYn(true);
        userRepository.save(user);

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    // Q55
    @PatchMapping("/api/admin/user/{id}/unlock")
    public ResponseEntity<?> userUnLock(@PathVariable Long id) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            return new ResponseEntity<>(ResponseMessage.fail("사용자 정보가 존재하지 않습니다."), HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if (!user.isLockYn()) {
            return new ResponseEntity<>(ResponseMessage.fail("이미 접속제한이 해제된 사용자 입니다."), HttpStatus.BAD_REQUEST);
        }

        user.setLockYn(false);
        userRepository.save(user);

        return ResponseEntity.ok().body(ResponseMessage.success());
    }

    // Q56
    @GetMapping("/api/admin/user/status/count")
    public ResponseEntity<?> userStatusCount() {

        UserSummary userSummary = userService.getUserStatusCount();

        return ResponseEntity.ok().body(ResponseMessage.success(userSummary));
    }

    // Q57
    @GetMapping("/api/admin/user/today")
    public ResponseEntity<?> todayUser() {

        List<User> users = userService.getTodayUsers();

        return ResponseEntity.ok().body(ResponseMessage.success(users));
    }

    // Q58
    @GetMapping("/api/admin/user/notice/count")
    public ResponseEntity<?> userNoticeCount() {

        List<UserNoticeCount> userNoticeCountList = userService.getUserNoticeCount();

        return ResponseEntity.ok().body(ResponseMessage.success(userNoticeCountList));
    }

    // Q59
    @GetMapping("/api/admin/user/log/count")
    public ResponseEntity<?> userLogCount() {

        List<UserLogCount> userLogCounts = userService.getUserLogCount();

        return ResponseEntity.ok().body(ResponseMessage.success(userLogCounts));
    }

}
