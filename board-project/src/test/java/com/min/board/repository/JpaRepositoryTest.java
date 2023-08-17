package com.min.board.repository;

import com.min.board.config.JpaConfig;
import com.min.board.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Jpa 연결 테스트")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // When
        List<Article> articles = articleRepository.findAll();

        // Then
        assertThat(articles)
                .isNotNull()
                .hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine2() {
        // Given
        Long previousCount = articleRepository.count();
        // When
        Article saveArticle = articleRepository.save(Article.of("new article", "new content", "new hashtag"));

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousCount+1);


    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine2() {      //위 테스트는 Transactional이 걸려있기에 코드가 순회 후 롤백된다.
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHashtag = " #springboot";
        article.setHashtag(updatedHashtag);
        // When
        Article saveArticle = articleRepository.save(article);
        articleRepository.flush();

        // Then
        assertThat(saveArticle).hasFieldOrPropertyWithValue("hashtag",updatedHashtag);


    }

    @DisplayName("특정 article이 삭제되었을때 articleComment도 지워지는지")
    @Test
    void givenTestData_whenDeleting_thenWorksFine2() {
        // Given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        // When
        articleRepository.delete(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);


    }

}