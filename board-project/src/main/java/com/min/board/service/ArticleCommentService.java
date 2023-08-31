package com.min.board.service;

import com.min.board.domain.Article;
import com.min.board.domain.ArticleComment;
import com.min.board.dto.ArticleCommentDto;
import com.min.board.repository.ArticleCommentRepository;
import com.min.board.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ArticleCommentService {
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;


    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {

        return articleCommentRepository.findByArticle_Id(articleId)
                .stream()
                .map(ArticleCommentDto::toDto)
                .toList();
    }

    public void saveArticleComment(ArticleCommentDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.articleId());
            articleCommentRepository.save(dto.toEntity(article));
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패, 댓글의 게시글을 찾을 수 없습니다 - dto: {}", dto);
        }
    }


    public void updateArticleComment(ArticleCommentDto dto) {
        try {
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if (dto.content() != null) {
                articleComment.setContent(dto.content());
            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 업데이트 실패, 댓글의 게시글을 찾을 수 없습니다 - dto: {}", dto);
        }
    }


    public void deleteArticleComment(Long articleCommentId) {
        articleCommentRepository.deleteById(articleCommentId);
    }
}
