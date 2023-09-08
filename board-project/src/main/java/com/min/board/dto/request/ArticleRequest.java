package com.min.board.dto.request;

import com.min.board.domain.UserAccount;
import com.min.board.dto.ArticleDto;
import com.min.board.dto.UserAccountDto;

public record ArticleRequest(
        String title,
        String content,
        String hashtag
) {
    public static ArticleRequest of(String title, String content, String hashtag) {
        return new ArticleRequest(title, content, hashtag);
    }
    public ArticleDto toArticleDto(UserAccountDto userAccountDto) {
        return ArticleDto.of(
                userAccountDto,
                title,
                content,
                hashtag
        );
    }
}
