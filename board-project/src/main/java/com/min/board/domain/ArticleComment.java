package com.min.board.domain;

import java.time.LocalDateTime;

public class ArticleComment {
    private Long id;
    private Article article;        //게시글 ID
    private String content;         // 댓글 내용

    private LocalDateTime createAt;
    private String createdBy;
    private LocalDateTime modifiedAt;
    private String modifiedBy;
}
