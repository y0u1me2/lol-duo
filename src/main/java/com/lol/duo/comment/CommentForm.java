package com.lol.duo.comment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class CommentForm {
    @NotEmpty(message = "내용은 필수 항목입니다.")
    private String content;
}
