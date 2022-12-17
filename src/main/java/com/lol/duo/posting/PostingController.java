package com.lol.duo.posting;

import com.lol.duo.comment.CommentForm;
import com.lol.duo.user.SiteUser;
import com.lol.duo.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RequestMapping("/posting")
@Controller
public class PostingController {
    @Autowired
    private PostingService postingService;
    @Autowired
    private UserService userService;

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        Page<Posting> paging = postingService.getList(page, keyword);
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        return "posting_list";
    }

    @RequestMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, CommentForm commentForm) {
        Posting posting = postingService.getPosting(id);
        model.addAttribute("posting", posting);
        return "posting_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createPosting(PostingForm postingForm) {
        return "posting_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String createPosting(@Valid PostingForm postingForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "posting_form";
        } else {
            SiteUser siteUser = this.userService.getUser(principal.getName());
            postingService.create(postingForm.getSubject(), postingForm.getContent(), siteUser);
            return "redirect:/posting/list";
        }
    }

    // 게시물 수정 버튼 눌렀을 때 화면 이동
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyPosting(PostingForm postingForm, @PathVariable("id") Integer id, Principal principal) {
        Posting posting = this.postingService.getPosting(id);
        if(!posting.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        postingForm.setSubject(posting.getSubject());
        postingForm.setContent(posting.getContent());
        return "posting_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyPosting(@Valid PostingForm postingForm, BindingResult bindingResult, Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "posting_form";
        }
        Posting posting = this.postingService.getPosting(id);
        if (!posting.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.postingService.modify(posting, postingForm.getSubject(), postingForm.getContent());
        return String.format("redirect:/posting/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deletePosting(Principal principal, @PathVariable("id") Integer id) {
        Posting posting = this.postingService.getPosting(id);
        if (!posting.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.postingService.delete(posting);
        return "redirect:/posting/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String votePosting(Principal principal, @PathVariable("id") Integer id) {
        Posting posting = this.postingService.getPosting(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.postingService.vote(posting, siteUser);
        return String.format("redirect:/posting/detail/%s", id);
    }
}
