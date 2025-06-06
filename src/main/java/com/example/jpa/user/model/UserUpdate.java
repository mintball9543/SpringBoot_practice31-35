package com.example.jpa.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserUpdate {

    @Size(max=20, message = "연락처는 최대 20자까지 입력해 주세요")
    @NotBlank(message = "연락처는 필수 항목 입니다")
    private String phone;
}
