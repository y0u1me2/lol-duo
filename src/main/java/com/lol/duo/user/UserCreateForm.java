package com.lol.duo.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserCreateForm {
    @Size(min = 3, max = 25)
    @NotEmpty(message = "사용자 이름은 필수 입력 항목입니다.")
    private String username;

    @NotEmpty(message = "이메일은 필수 입력 항목입니다.")
    @Email
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 항목입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호는 필수 입력 항목입니다.")
    private String password2;
}
