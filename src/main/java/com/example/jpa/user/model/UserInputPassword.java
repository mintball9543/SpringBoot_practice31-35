package com.example.jpa.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInputPassword {
    @NotBlank(message = "현재 비밀번호는 필수 항목입니다.")
    private String password;

    @Size(min = 4, max = 20, message = "새 비밀번호는 4자 이상 20자 이하로 입력해 주세요.")
    @NotBlank(message = "새 비밀번호는 필수 항목입니다.")
    private String newPassword;


}
