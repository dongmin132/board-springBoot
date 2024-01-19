package com.min.board.dto;

import com.min.board.domain.Article;
import com.min.board.domain.UserAccount;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        Set<HashtagDto> hashtagDtos,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {     //

    public static ArticleDto of(Long id, UserAccountDto userAccountDto, String title, String content, Set<HashtagDto> hashtagDtos, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, userAccountDto, title, content, hashtagDtos, createdAt, createdBy, modifiedAt, modifiedBy);
    }
    public static ArticleDto of(UserAccountDto userAccountDto, String title, String content, Set<HashtagDto> hashtagDtos) {
        return new ArticleDto(null,userAccountDto, title, content, hashtagDtos, null,null,null,null);
    }

    public static ArticleDto toDto(Article entity) {     //엔티티를 입력하면 DTO로 변환
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.toDto(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtags().stream()
                        .map(HashtagDto::toDto)
                        .collect(Collectors.toUnmodifiableSet()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Article toEntity(UserAccount userAccount) {         //Dto를 엔티티로
                                        //Article은 Dto의 변경점을 모르기 때문에 Dto의 변화가 Article의 영향을 주지 않는다.
        return Article.of(
                userAccount,
                title,
                content
        );
    }


}


