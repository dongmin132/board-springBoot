package com.min.board.service;

import com.min.board.domain.Article;
import com.min.board.domain.type.SearchType;
import com.min.board.dto.ArticleDto;
import com.min.board.dto.ArticleWithCommentsDto;
import com.min.board.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {     //searchKeyword가 없을 경우에는 전체 결과
            return articleRepository.findAll(pageable).map(ArticleDto::toDto);
        }


        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::toDto);
            case ID ->
                    articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::toDto);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::toDto);
            case HASHTAG ->
                    articleRepository.findByHashtag("#" + searchKeyword, pageable).map(ArticleDto::toDto);     //현재는 검색할때 #이 자동으로 들어가 있는 상태이다.             //이 문제는 추후 리팩토링할 예정
            case NICKNAME ->
                    articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::toDto);
        };

    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticle(long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
        return ArticleWithCommentsDto.toDto(article);

    }

    public void saveArticle(ArticleDto dto) {
        articleRepository.save(dto.toEntity());
    }

    public void updateArticle(ArticleDto dto) {     //클래스 레벨에 트랜잭셔널에 의해서 메서드 단위로 트랜잭셔널이 묶여있어서
        //영속성 컨텍스트가 엔티티에 변함을 감지하고 그것에 대해서 쿼리를 날리게 된다.
        //즉, save()를 안적어도 된다.
        try {
            Article article = articleRepository.getReferenceById(dto.id());
            if (dto.title() != null) article.setTitle(dto.title());
            if (dto.content() != null) article.setContent(dto.content());
            article.setHashtag(dto.hashtag());
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 찾을 수 없습니다 - dto: {}",dto);
        }

    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }
}
