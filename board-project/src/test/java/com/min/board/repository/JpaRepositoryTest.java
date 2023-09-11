package com.min.board.repository;

import com.min.board.config.JpaConfig;
import com.min.board.domain.Article;
import com.min.board.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Jpa 연결 테스트")
@Import(JpaRepositoryTest.TestJpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {
    @Autowired      //@Autowired를 활용한 주입
    ArticleRepository articleRepository;
    @Autowired
    ArticleCommentRepository articleCommentRepository;
    @Autowired
    UserAccountRepository userAccountRepository;

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
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("kdm", "pw", null, null, null));
        Article article = Article.of(userAccount, "userAccount", "new content", "#spring");
        // When
        Article saveArticle = articleRepository.save(article);

        // Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);


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
        assertThat(saveArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);


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

    @EnableJpaAuditing
    @TestConfiguration      //Test할 때만 실행
    public static class TestJpaConfig {     //
        @Bean
        public AuditorAware<String> auditorAware() {        //createBy가 저절로 만들어지지 않기 때문에 Auditing을 테스트 용으로 하나만든다.
            return () -> Optional.of("kdm");
        }
    }

}