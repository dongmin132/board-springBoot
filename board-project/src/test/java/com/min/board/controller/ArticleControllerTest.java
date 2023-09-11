package com.min.board.controller;

import com.min.board.config.TestSecurityConfig;
import com.min.board.domain.constant.FormStatus;
import com.min.board.domain.constant.SearchType;
import com.min.board.dto.ArticleDto;
import com.min.board.dto.ArticleWithCommentsDto;
import com.min.board.dto.UserAccountDto;
import com.min.board.dto.request.ArticleRequest;
import com.min.board.dto.response.ArticleResponse;
import com.min.board.service.ArticleService;
import com.min.board.service.PaginationService;
import com.min.board.util.FormDataEncoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("View 컨트롤러 - 게시글")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(ArticleController.class)        //입력한 컨트롤러만 체크해서 테스트할 수 있다.
class ArticleControllerTest {
    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;
    @MockBean
    private ArticleService articleService;      //필드 주입
    @MockBean
    private PaginationService paginationService;

    public ArticleControllerTest(@Autowired MockMvc mvc,
                                 @Autowired FormDataEncoder formDataEncoder) {      //테스트에서는 @Autowired를 붙여줘야 한다.
        this.mvc=mvc;
        this.formDataEncoder = formDataEncoder;
    }       //생성자 주입

    @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        //Given
        given(articleService.searchArticles(eq(null),eq(null),any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1,2,3,4,5));   // anyInt(): 아무 숫자나 넣어줘라
        //when & then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/index"))   // 실제 view에 이름을 검색한다.
                .andExpect(model().attributeExists("articles"))    //"articles"라는 키 값이 있는지 확인한다.
                .andExpect(model().attributeExists("paginationBarNumbers"));    //"articles"라는 키 값이 있는지 확인한다.
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[view] [GET] 게시글 리스트 (게시판) 페이지 - 검색어와 함께 호출")
    @Test
    public void givenSearchKeyword_whenSearchingArticlesView_thenReturnsArticlesView() throws Exception {
        //Given
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";
        given(articleService.searchArticles(eq(searchType),eq(searchValue),any(Pageable.class))).willReturn(Page.empty());  //searchType과 searchValue는 주어진 값과 일치해야 하면 any는 Pageable을 구성한 아무 객체가 와도 상관없다는 뜻
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1,2,3,4,5));   // anyInt(): 아무 숫자나 넣어줘라
        //when & then
        mvc.perform(get("/articles")
                        .queryParam("searchType",searchType.name())
                        .queryParam("searchValue",searchValue)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/index"))   // 실제 view에 이름을 검색한다.
                .andExpect(model().attributeExists("articles"))    //"articles"라는 키 값이 있는지 확인한다.
                .andExpect(model().attributeExists("searchTypes"));
        then(articleService).should().searchArticles(eq(searchType), eq(searchValue), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }


    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortingParams_whenSearchingArticlesPage_thenReturnsArticlesPage() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mvc.perform(
                        get("/articles")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBarNumbers", barNumbers));
        then(articleService).should().searchArticles(null, null, pageable);
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("[view] [GET] 게시글 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    @Test
    public void givenNothing_whenRequestingArticlePage_thenRedirectsToLoginPage() throws Exception {
        //Given
        long articleId = 1L;
        //When & Then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(articleService).shouldHaveNoInteractions();
    }

    @WithMockUser
    @DisplayName("[view] [GET] 게시글 상세 페이지 - 정상 호출, 인증된 사용자")
    @Test
    public void givenNothing_whenRequestingArticleView_thenReturnsArticlesView() throws Exception {
        //Given
        Long articleId=1L;
        given(articleService.getArticleWithComments(articleId)).willReturn(createArticleWithCommentsDto());
        //when & then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/detail"))   // 실제 view에 이름을 검색한다.
                .andExpect(model().attributeExists("article"))    //"article"라는 키 값이 있는지 확인한다.
                .andExpect(model().attributeExists("articleComments"));    //article에는 댓글들도 포함되어있다.
        then(articleService).should().getArticleWithComments(articleId);
    }



    @Disabled("구현 중")
    @DisplayName("[view] [GET] 게시글 검색 전용 페이지 - 정상 호출")
    @Test
    public void givenNothing_whenRequestingArticleSearchView_thenReturnsArticlesView() throws Exception {
        //when & then
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/search"));   // 실제 view에 이름을 검색한다.


    }

    @DisplayName("[view] [GET] 게시글 해시태그 검색 전용 페이지 - 정상 호출, 해시태그 입력X")
    @Test
    public void givenNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticleSearchHashtagView() throws Exception {
        //Given
        given(articleService.searchArticlesViaHashtag(eq(null), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1, 2, 3, 4, 5));
        given(articleService.getHashtags()).willReturn(List.of("#java", "#spring", "#boot"));

        //when & then
        mvc.perform(get("/articles/search-hashtag"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/search-hashtag"))   // 실제 view에 이름을 검색한다.
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attributeExists("hashtags"))     //모델에 존재만을 확인하는 것.
                .andExpect(model().attributeExists("paginationBarNumbers"));
        then(articleService).should().searchArticlesViaHashtag(eq(null), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
        then(articleService).should().getHashtags();

    }

    @DisplayName("[view] [GET] 게시글 해시태그 검색 전용 페이지 - 정상 호출, 해시태그 입력")
    @Test
    public void givenHashtag_whenRequestingArticleHashtagSearchView_thenReturnsArticleSearchHashtagView() throws Exception {
        //Given
        String hashtag = "#java";
        given(articleService.searchArticlesViaHashtag(eq(hashtag), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(1, 2, 3, 4, 5));
        given(articleService.getHashtags()).willReturn(List.of("#java", "#spring", "#boot"));
        //when & then
        mvc.perform(get("/articles/search-hashtag")
                        .queryParam("searchValue",hashtag)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))    //ComplatibleWith 메서드를 사용하여 mdeiaType만을 검사하고 그 안에 옵션은 검사하지않는다.
                .andExpect(view().name("articles/search-hashtag"))   // 실제 view에 이름을 검색한다.
                .andExpect(model().attribute("articles", Page.empty()))
                .andExpect(model().attributeExists("hashtags"))     //모델에 존재만을 확인하는 것.
                .andExpect(model().attributeExists("paginationBarNumbers"));
        then(articleService).should().searchArticlesViaHashtag(eq(hashtag), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
        then(articleService).should().getHashtags();

    }

    @WithMockUser
    @DisplayName("[view][GET] 새 게시글 작성 페이지")
    @Test
    void givenNothing_whenRequesting_thenReturnsNewArticlePage() throws Exception {
        //Given
        //When & Then
        mvc.perform(get("/articles/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }

    @WithUserDetails(value="kdmTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 새 게시글 등록 - 정상 호출")
    @Test
    void givenNewArticleInfo_whenRequesting_thenSavesNewArticle() throws Exception {
        //Given
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));

        //When & Then
        mvc.perform(
                        post("/articles/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().saveArticle(any(ArticleDto.class));

    }

    @WithMockUser
    @DisplayName("[view][GET] 게시글 수정 페이지")
    @Test
    void givenArticleId_whenRequesting_thenReturnsUpdatedArticlePage() throws Exception {
        //Given
        long articleId = 1L;
        ArticleDto dto= createArticleDto();
        given(articleService.getArticle(articleId)).willReturn(dto);
        //When & Then
        mvc.perform(get("/articles/" + articleId + "/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/form"))
                .andExpect(model().attribute("article", ArticleResponse.from(dto)))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE));
        then(articleService).should().getArticle(articleId);
    }

    @WithUserDetails(value="kdmTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)

    @DisplayName("[view][POST] 게시글 수정 - 정상 호출")
    @Test
    void givenUpdatedArticleInfo_whenRequesting_thenUpdatesNewArticle() throws Exception {
        //Given
        ArticleRequest articleRequest = ArticleRequest.of("new title", "new content", "#new");
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));

        //When & Then
        mvc.perform(
                        post("/articles/form")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(articleRequest))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().saveArticle(any(ArticleDto.class));

    }
    @WithUserDetails(value="kdmTest",setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("[view][POST] 게시글 삭제 - 정상 호출")
    @Test
    void givenArticleIdToDelete_whenRequesting_thenDeleteArticle() throws Exception {
        //Given
        long articleId = 1L;
        String userId = "kdmTest";
        willDoNothing().given(articleService).deleteArticle(articleId,userId);

        //When & Then
        mvc.perform(
                        post("/articles/"+articleId+"/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/articles"))
                .andExpect(redirectedUrl("/articles"));
        then(articleService).should().deleteArticle(articleId,userId);

    }

    private ArticleDto createArticleDto() {
        return ArticleDto.of(
                createUserAccountDto(),
                "title",
                "content",
                "#java"
        );
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