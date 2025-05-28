package com.example.jpa.user.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.jpa.board.entity.Board;
import com.example.jpa.board.entity.BoardComment;
import com.example.jpa.board.model.ServiceResult;
import com.example.jpa.board.service.BoardService;
import com.example.jpa.common.model.ResponseResult;
import com.example.jpa.notice.entity.Notice;
import com.example.jpa.notice.entity.NoticeLike;
import com.example.jpa.notice.model.NoticeResponse;
import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.notice.repository.NoticeLikeRepository;
import com.example.jpa.notice.repository.NoticeRepository;
import com.example.jpa.user.entity.User;
import com.example.jpa.user.exception.ExistEmailException;
import com.example.jpa.user.exception.PasswordNotMatchException;
import com.example.jpa.user.exception.UserNotFoundException;
import com.example.jpa.user.model.*;
import com.example.jpa.user.repository.UserRepository;
import com.example.jpa.user.service.PointService;
import com.example.jpa.util.JWTUtils;
import com.example.jpa.util.PasswordUtils;
import jdk.vm.ci.meta.Local;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.DecodeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class ApiUserController {

    private final UserRepository userRepository;
    private  final NoticeRepository noticeRepository;
    private final NoticeLikeRepository noticeLikeRepository;
    private final BoardService boardService;
    private final PointService pointService;

    // Q31
//    @PostMapping("/api/user")
//    public ResponseEntity<?> addUser(@RequestBody @Valid UserInput userInput, Errors errors){
//        List<ResponseError> reponseErrorList = new ArrayList<>();
//
//        if(errors.hasErrors()){
//            errors.getAllErrors().forEach((e) -> {
//                reponseErrorList.add(ResponseError.of((FieldError)e));
//            });
//            return new ResponseEntity<>(reponseErrorList, HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity<>(userInput, HttpStatus.OK);
//    }

//    Q32
//    @PostMapping("/api/user")
//    public ResponseEntity<?> addUser(@RequestBody @Valid UserInput userInput, Errors errors) {
//        List<ResponseError> responseErrorList = new ArrayList<>();
//
//        if (errors.hasErrors()) {
//            errors.getAllErrors().forEach((e) -> {
//                responseErrorList.add(ResponseError.of((FieldError) e));
//            });
//            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
//        }
//
//        User user = User.builder()
//                .email(userInput.getEmail())
//                .userName(userInput.getUserName())
//                .password(userInput.getPassword())
//                .phone(userInput.getPhone())
//                .regDate(java.time.LocalDateTime.now())
//                .build();
//        userRepository.save(user);
//
//        return new ResponseEntity<>(userInput, HttpStatus.OK);
//    }

    @PutMapping("/api/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdate userUpdate, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(id)
                .orElseThrow(()->new UserNotFoundException("사용자 정보가 없습니다."));

        user.setPhone(userUpdate.getPhone());
        user.setUpdateDate(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok().build();

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> UserNotFoundExceptionHandler(UserNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/api/user/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        UserResponse userResponse = UserResponse.of(user);

        return userResponse;
    }

    @GetMapping("/api/user/{id}/notice")
    public List<NoticeResponse> userNotice(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        List<Notice> noticeList = noticeRepository.findByUser(user);

        List<NoticeResponse> noticeResponseList = new ArrayList<>();
        noticeList.stream().forEach(e -> {
            noticeResponseList.add(NoticeResponse.of(e));
        });

        return noticeResponseList;
    }

    // Q36
//    @PostMapping("/api/user")
//    public ResponseEntity<?> addUser(@RequestBody @Valid UserInput userInput, Errors errors){
//        List<ResponseError> responseErrorList = new ArrayList<>();
//        if(errors.hasErrors()){
//            errors.getAllErrors().stream().forEach((e)->{
//                responseErrorList.add(ResponseError.of((FieldError)e));
//            });
//            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
//        }
//
//        if(userRepository.countByEmail(userInput.getEmail()) > 0){
//            throw new ExistEmailException("이미 존재하는 이메일입니다.");
//        }
//
//        User user = User.builder()
//                .email(userInput.getEmail())
//                .userName(userInput.getUserName())
//                .password(userInput.getPassword())
//                .phone(userInput.getPhone())
//                .regDate(LocalDateTime.now())
//                .build();
//
//        userRepository.save(user);
//
//        return ResponseEntity.ok().build();
//    }

    @ExceptionHandler(value = {ExistEmailException.class, PasswordNotMatchException.class})
    public ResponseEntity<?> ExistsEmailExceptionHandler(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Q37
    @PatchMapping("/api/user/{id}/password")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id, @RequestBody UserInputPassword userInputPassword, Errors errors) {
        List<ResponseError> responseErrorList = new ArrayList<>();

        if (errors.hasErrors()) {
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByIdAndPassword(id, userInputPassword.getPassword())
                .orElseThrow(() -> new PasswordNotMatchException("비밀번호가 일치하지 않습니다."));

        user.setPassword(userInputPassword.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // Q38
    private String getEncryptPassword(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

    @PostMapping("/api/user")
    public ResponseEntity<?> addUser(@RequestBody @Valid UserInput userInput, Errors errors){
        List<ResponseError> responseErrorList = new ArrayList<>();
        if(errors.hasErrors()){
            errors.getAllErrors().stream().forEach((e)->{
                responseErrorList.add(ResponseError.of((FieldError)e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        if(userRepository.countByEmail(userInput.getEmail()) > 0){
            throw new ExistEmailException("이미 존재하는 이메일입니다.");
        }

        String encryptPassword = getEncryptPassword(userInput.getPassword());

        User user = User.builder()
                .email(userInput.getEmail())
                .userName(userInput.getUserName())
                .password(encryptPassword)
                .phone(userInput.getPhone())
                .regDate(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    // Q39
    @DeleteMapping("/api/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        try {
            userRepository.delete(user);
        } catch(DataIntegrityViolationException e){
            String message = "제약조건에 문제가 발생하였습니다.";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            return new ResponseEntity<>("회원 탈퇴 중 문제가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }

    // Q40
    @GetMapping("/api/user")
    public ResponseEntity<?> findUser(@RequestBody UserInputFind userInputFind) {
        User user = userRepository.findByUserNameAndPhone(userInputFind.getUserName(), userInputFind.getPhone())
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        UserResponse userResponse = UserResponse.of(user);

        return ResponseEntity.ok().body(userResponse);
    }

    // Q41
    private String getResetPassword() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "").substring(0, 10);
    }

    @GetMapping("/api/user/{id}/password/reset")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        String resetPassword = getResetPassword();
        String encryptPassword = getEncryptPassword(resetPassword);
        user.setPassword(encryptPassword);
        userRepository.save(user);

        String message = String.format("[%s]님의 임시 비밀번호가 발급되었습니다. 비밀번호 : %s", user.getUserName(), resetPassword);
        sendSMS(message);

        return ResponseEntity.ok().build();
    }

    void sendSMS(String message){
        System.out.println("문자 발송 : " + message);
    }


    // Q42
    @GetMapping
    public List<NoticeLike> likeNotice(@PathVariable Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        List<NoticeLike> noticeLikeList = noticeLikeRepository.findByUser(user);
        return noticeLikeList;
    }

    // Q43
//    @PostMapping("/api/user/login")
//    public ResponseEntity<?> createToken(@RequestBody @Valid UserLogin userLogin, Errors errors){
//        if(errors.hasErrors()){
//            List<ResponseError> responseErrorList = new ArrayList<>();
//            errors.getAllErrors().forEach((e) -> {
//                responseErrorList.add(ResponseError.of((FieldError) e));
//            });
//            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
//        }
//
//        User user = userRepository.findByEmail(userLogin.getEmail())
//                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));
//
//        if(PasswordUtils.equalPassword(userLogin.getPassword(), user.getPassword()) == false)
//            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
//
//        return ResponseEntity.ok().build();
//
//    }


    // Q44~45
    @PostMapping("/api/user/login")
    public ResponseEntity<?> createToken(@RequestBody @Valid UserLogin userLogin, Errors errors){
        if(errors.hasErrors()){
            List<ResponseError> responseErrorList = new ArrayList<>();
            errors.getAllErrors().forEach((e) -> {
                responseErrorList.add(ResponseError.of((FieldError) e));
            });
            return new ResponseEntity<>(responseErrorList, HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmail(userLogin.getEmail())
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        if(PasswordUtils.equalPassword(userLogin.getPassword(), user.getPassword()) == false)
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");

        // 토큰
        LocalDateTime expiredDateTime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDateTime);

        String token = JWT.create()
                .withExpiresAt(expiredDate)
                .withClaim("user_id",user.getId())
                .withSubject(user.getUserName())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512("fastcampus".getBytes()));


        return ResponseEntity.ok().body(UserLoginToken.builder().token(token).build());
    }


    // Q46
    @PatchMapping("/api/user/login")
    public ResponseEntity<?> refreshToken(HttpServletRequest request){
        String token = request.getHeader("F-TOKEN");
        String email = "";
        try {
            email = JWT.require(Algorithm.HMAC512("fastcampus".getBytes()))
                    .build()
                    .verify(token)
                    .getIssuer();
        } catch (SignatureVerificationException | JWTDecodeException e) {
            throw new PasswordNotMatchException("비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("사용자 정보가 없습니다."));

        LocalDateTime expiredDateTime = LocalDateTime.now().plusMonths(1);
        Date expiredDate = java.sql.Timestamp.valueOf(expiredDateTime);

        String newToken = JWT.create()
                .withExpiresAt(expiredDate) // 30일
                .withClaim("user_id", user.getId())
                .withSubject(user.getUserName())
                .withIssuer(user.getEmail())
                .sign(Algorithm.HMAC512("fastcampus".getBytes()));

        return ResponseEntity.ok().body(UserLoginToken.builder().token(newToken).build());
    }

    // Q47
    @DeleteMapping("/api/user/login")
    public ResponseEntity<?> removeToken(@RequestHeader("F-TOKEN") String token){
        String email = "";

        try{
            email = JWTUtils.getIssuer(token);
        } catch (SignatureVerificationException | JWTDecodeException e) {
            return new ResponseEntity<>("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 토큰 삭제 로직은 애매하므로 기간을 설정하거나 횟수 제한을 둔다.

        return ResponseEntity.ok().build();
    }

    // Q80
    @GetMapping("/api/user/board/post")
    public ResponseEntity<?> myPost(@RequestHeader("F-TOKEN") String token) {

        String email = "";
        try {
            email = JWTUtils.getIssuer(token);
        } catch (SignatureVerificationException e) {
            return ResponseResult.fail("토근 정보가 정확하지 않습니다.");
        }

        List<Board> list = boardService.postList(email);
        return ResponseResult.success(list);
    }

    // Q81
    @GetMapping("/api/user/board/comment")
    public ResponseEntity<?> myComments(@RequestHeader("F-TOKEN") String token) {

        String email = "";
        try {
            email = JWTUtils.getIssuer(token);
        } catch (SignatureVerificationException e) {
            return ResponseResult.fail("토근 정보가 정확하지 않습니다.");
        }

        List<BoardComment> list = boardService.commentList(email);
        return ResponseResult.success(list);
    }

    // Q82
    @PostMapping("/api/user/point")
    public ResponseEntity<?> userPoint(@RequestHeader("F-TOKEN") String token
            , @RequestBody UserPointInput userPointInput ) {

        String email = "";
        try {
            email = JWTUtils.getIssuer(token);
        } catch (SignatureVerificationException e) {
            return ResponseResult.fail("토근 정보가 정확하지 않습니다.");
        }

        ServiceResult result = pointService.addPoint(email, userPointInput);
        return ResponseResult.result(result);
    }
}
