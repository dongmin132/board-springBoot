package com.min.board.controller;

import com.min.board.config.SecurityConfig;
import com.min.board.dto.ArticleCommentDto;
import com.min.board.dto.ArticleDto;
import com.min.board.dto.ArticleWithCommentsDto;
import com.min.board.dto.UserAccountDto;
import com.min.board.service.ArticleService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Set;

import static io.micrometer.core.instrument.binder.http.HttpRequestTags.status;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@Import(SecurityConfig.class)
@WebMvcTest(ArticleController.class)        //입력한 컨트롤러만 체크해서 테스트할 수 있다.
class ArticleControllerTest {
    private final MockMvc mvc;

    @MockBean
    private ArticleService articleService;      //필드 주입

    public ArticleControllerTest(@Autowired MockMvc mvc) {      //테스트에서는 @Autowired를 붙여줘야 한다.
        this.mvc=mvc;
    }       //생성자 주입

    @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        //Given
        given(articleService.searchArticles(eq(null),eq(null),any(Pageable.class))).willReturn(Page.empty());
        //when & then
        mvc.perform(get("/articles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/index"))   // 실제 view에 이름을 검색한다.
                .andExpect(model().attributeExists("articles"));    //"articles"라는 키 값이 있는지 확인한다.
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));


    }

    @DisplayName("[view] [GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleView_thenReturnsArticlesView() throws Exception {
        //Given
        Long articleId=1L;
        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());
        //when & then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/detail"))   // 실제 view에 이름을 검색한다.
                .andExpect(model().attributeExists("article"))    //"article"라는 키 값이 있는지 확인한다.
                .andExpect(model().attributeExists("articleComments"));    //article에는 댓글들도 포함되어있다.
        then(articleService).should().getArticle(articleId);
    }

    @Disabled("구현 중")
    @DisplayName("[view] [GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleSearchView_thenReturnsArticlesView() throws Exception {
        //when & then
        mvc.perform(get("/articles/search"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/search"));   // 실제 view에 이름을 검색한다.


    }
    @Disabled
    @DisplayName("[view] [GET] 게시글 해시태그 검색 전용 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticlesView() throws Exception {
        //when & then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/search-hashtag"));   // 실제 view에 이름을 검색한다.
    }
    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "kdm",
                LocalDateTime.now(),
                "kdm"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "kdm",
                "password",
                "kdm@mail.com",
                "Dongmin",
                "This is memo",
                LocalDateTime.now(),
                "kdm",
                LocalDateTime.now(),
                "kdm"
        );
    }
}