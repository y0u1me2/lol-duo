package com.lol.duo.posting;

import com.lol.duo.comment.Comment;
import com.lol.duo.exception.DataNotFoundException;
import com.lol.duo.user.SiteUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostingService {
    @Autowired
    private PostingRepository postingRepository;

    public Page<Posting> getList(int page, String keyword) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        Specification<Posting> spec = search(keyword);
        return postingRepository.findAll(spec, pageable);
    }

    public Posting getPosting(Integer id) {
        Optional<Posting> posting = postingRepository.findById(id);
        if (posting.isPresent()) {
            return posting.get();
        } else {
            throw new DataNotFoundException("Data does not exist");
        }
    }

    public void create(String subject, String content, SiteUser author) {
        Posting posting = Posting.builder()
                .subject(subject)
                .content(content)
                .createDate(LocalDateTime.now())
                .author(author)
                .build();
        postingRepository.save(posting);
    }

    public void modify(Posting posting, String subject, String content) {
        posting.setSubject(subject);
        posting.setContent(content);
        posting.setModifyDate(LocalDateTime.now());
        this.postingRepository.save(posting);
    }

    public void delete(Posting posting) {
        this.postingRepository.delete(posting);
    }

    public void vote(Posting posting, SiteUser siteUser) {
        posting.getVoter().add(siteUser);
        this.postingRepository.save(posting);
    }

    private Specification<Posting> search(String keyword) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Posting> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Posting, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Posting, Comment> a = q.join("commentList", JoinType.LEFT);
                Join<Comment, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + keyword + "%"), // 제목
                        cb.like(q.get("content"), "%" + keyword + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + keyword + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + keyword + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + keyword + "%"));   // 답변 작성자
            }
        };
    }
}
