package com.example.jpa.user.model;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {
    @NotBlank(message = "이메일은 필수 항목 입니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수 항목 입니다")
    private String password;
}
