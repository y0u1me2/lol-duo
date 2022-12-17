package com.lol.duo.comment;

import com.lol.duo.posting.Posting;
import com.lol.duo.posting.PostingService;
import com.lol.duo.user.SiteUser;
import com.lol.duo.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@RequestMapping("/comment")
@Controller
public class CommentController {
    @Autowired
    private PostingService postingService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createComment(Model model, @PathVariable("id") Integer id, @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal) {
        Posting posting = postingService.getPosting(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("posting", posting);
            return "posting_detail";
        }
        Comment comment = commentService.create(posting, commentForm.getContent(), siteUser);
        return String.format("redirect:/posting/detail/%s#comment_%s", id, comment.getId());
    }

    // 댓글 수정 버튼 클릭 시 (댓글 편집 화면 이동)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyComment(CommentForm commentForm, @PathVariable("id") Integer id, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        return "comment_form";
    }

    // 댓글 수정 버튼 클릭 시 (DB 처리 후 게시글 상세 화면으로 이동)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyComment(@Valid CommentForm commentForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/posting/detail/%s#comment_%s", comment.getPosting().getId(), comment.getId());
    }

    // 댓글 삭제 버튼 클릭 시 처리
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteComment(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/posting/detail/%s", comment.getPosting().getId());
    }

    // 댓글 추천
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String voteComment(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.commentService.vote(comment, siteUser);
        return String.format("redirect:/posting/detail/%s#comment_%s", comment.getPosting().getId(), comment.getId());
    }
}
