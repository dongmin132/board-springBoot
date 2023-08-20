package com.min.board.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static io.micrometer.core.instrument.binder.http.HttpRequestTags.status;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@WebMvcTest(ArticleController.class)        //입력한 컨트롤러만 체크해서 테스트할 수 있다.
class ArticleControllerTest {
    private final MockMvc mvc;

    public ArticleControllerTest(@Autowired MockMvc mvc) {      //테스트에서는 @Autowired를 붙여줘야 한다.
        this.mvc=mvc;
    }

    @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        //when & then
        mvc.perform(get("/articles"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/index"))   // 실제 view에 이름을 검색한다.
                .andExpect(model().attributeExists("articles"));    //"articles"라는 키 값이 있는지 확인한다.

    }

    @DisplayName("[view] [GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleView_thenReturnsArticlesView() throws Exception {
        //when & then
        mvc.perform(get("/articles/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/detail"))   // 실제 view에 이름을 검색한다.
                .andExpect(model().attributeExists("article"))    //"article"라는 키 값이 있는지 확인한다.
                .andExpect(model().attributeExists("articleComments"));    //article에는 댓글들도 포함되어있다.

    }


    @DisplayName("[view] [GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleSearchView_thenReturnsArticlesView() throws Exception {
        //when & then
        mvc.perform(get("/articles/search"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/search"));   // 실제 view에 이름을 검색한다.


    }
    @DisplayName("[view] [GET] 게시글 해시태그 검색 전용 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticlesView() throws Exception {
        //when & then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/search-hashtag"));   // 실제 view에 이름을 검색한다.
    }
}