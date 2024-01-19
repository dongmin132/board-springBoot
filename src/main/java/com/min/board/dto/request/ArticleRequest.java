package com.min.board.dto.request;

import com.min.board.domain.UserAccount;
import com.min.board.dto.ArticleDto;
import com.min.board.dto.HashtagDto;
import com.min.board.dto.UserAccountDto;

import java.util.Set;

public record ArticleRequest(
        String title,
        String content
) {
    public static ArticleRequest of(String title, String content) {
        return new ArticleRequest(title, content);
    }
    public ArticleDto toArticleDto(UserAccountDto userAccountDto) {
        return toArticleDto(userAccountDto, null);
    }
    public ArticleDto toArticleDto(UserAccountDto userAccountDto, Set<HashtagDto> hashtagDtos) {
        return ArticleDto.of(
                userAccountDto,
                title,
                content,
                hashtagDtos

        );
    }
}
