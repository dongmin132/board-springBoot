package com.min.board.domain;

import java.time.LocalDateTime;

public class Article {
    private Long id;
    private String title;       //제목
    private String content;     //내용
    private String hashtag;     //해시태그로 검색하기

    private LocalDateTime createAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
