package com.example.jpa.user.controller;

import com.example.jpa.notice.model.ResponseError;
import com.example.jpa.user.model.UserInput;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ApiUserController {


    @PostMapping("/api/user")
    public ResponseEntity<?> addUser(@RequestBody @Valid UserInput userInput, Errors errors){
        List<ResponseError> reponseErrorList = new ArrayList<>();

        if(errors.hasErrors()){
            errors.getAllErrors().forEach((e) -> {
                reponseErrorList.add(ResponseError.of((FieldError)e));
            });
            return new ResponseEntity<>(reponseErrorList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userInput, HttpStatus.OK);
    }
}
