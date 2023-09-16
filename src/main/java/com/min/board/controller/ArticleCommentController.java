package com.min.board.controller;

import com.min.board.dto.UserAccountDto;
import com.min.board.dto.request.ArticleCommentRequest;
import com.min.board.dto.security.BoardPrincipal;
import com.min.board.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest,
                                        @AuthenticationPrincipal BoardPrincipal principal
                                        ) {
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(principal.toUserAccountDto()));

        return "redirect:/articles/" + articleCommentRequest.articleId();
    }
        @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable Long commentId,
                                       @AuthenticationPrincipal BoardPrincipal principal,
                                       Long articleId) {

        articleCommentService.deleteArticleComment(commentId, principal.getUsername());

        return "redirect:/articles/" + articleId;
    }

}
