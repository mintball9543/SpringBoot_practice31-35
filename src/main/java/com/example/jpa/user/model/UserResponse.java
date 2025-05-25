package com.example.jpa.user.model;

import com.example.jpa.user.entity.User;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
public class UserResponse {
    private long id;
    private String email;
    private String userName;
    private String phone;


    public static UserResponse of(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .phone(user.getPhone())
                .build();
    }
}
