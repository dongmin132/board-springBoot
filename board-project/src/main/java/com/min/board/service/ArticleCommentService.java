package com.min.board.service;

import com.min.board.dto.ArticleCommentDto;
import com.min.board.repository.ArticleCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ArticleCommentService {
    private final ArticleCommentRepository articleCommentRepository;


    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return List.of();
    }

    public void saveArticleComment(ArticleCommentDto dto) {
        
    }

    public void deleteArticleComment(Long articleCommentId) {
    }

    public void updateArticleComment(ArticleCommentDto dto) {
    }

}
