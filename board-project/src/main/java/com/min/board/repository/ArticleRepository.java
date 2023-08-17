package com.min.board.repository;

import com.min.board.domain.Article;
import com.min.board.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
