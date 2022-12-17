package com.lol.duo.posting;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class PostingForm {
    @NotEmpty(message = "제목은 필수 입력값입니다.")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "내용은 필수 입력값입니다.")
    private String content;
}
