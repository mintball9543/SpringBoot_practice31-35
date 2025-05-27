package com.example.jpa.common.model;

import com.example.jpa.user.model.ResponseMessage;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

public class ResponseResult {

    public static ResponseEntity<?> fail(String message) {
        return ResponseEntity.badRequest().body(message);
    }

    public static ResponseEntity<?> success() {
        return ResponseEntity.ok().build();
    }

}
