package com.lol.duo.posting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostingRepositoryTest {
    @Autowired
    private PostingRepository postingRepository;

    @Test
    public void insert() {
        for (int i = 1; i <= 5; i++) {
            String subject = String.format("테스트 데이터입니다. [%03d]", i);
            String content = "내용 없음";
            Posting value = Posting.builder()
                    .subject(subject)
                    .content(content)
                    .createDate(LocalDateTime.now())
                    .build();
            postingRepository.save(value);
        }
    }
}