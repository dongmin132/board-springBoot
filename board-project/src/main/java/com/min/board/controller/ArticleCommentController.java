package com.min.board.controller;

import com.min.board.dto.UserAccountDto;
import com.min.board.dto.request.ArticleCommentRequest;
import com.min.board.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {
    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest, Long articleId) {
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(UserAccountDto.of(
                "kdm", "asdf1234", "kdm@mail.com", "kdmNickname", "memo", null, null, null, null
        )));

        return "redirect:/articles/" + articleCommentRequest.articleId();
    }
        @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable Long commentId, Long articleId) {

        articleCommentService.deleteArticleComment(commentId);

        return "redirect:/articles/" + articleId;
    }

}
