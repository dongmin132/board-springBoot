package com.min.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.image.ImageProducer;
import java.util.List;

/*
    /articles
    /articles/{article-id}
    /articles/serarch
    /articles/search-hashtag
 */
@RequestMapping("/articles")
@Controller
public class ArticleController {
    @GetMapping
    public String articles(Model model) {
        model.addAttribute("articles", List.of());
        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, Model model) {
        model.addAttribute("article", "null");  //TODO: 나중에 실제 값을 넣어줘야 한다.
        model.addAttribute("articleComments", List.of());
        return "articles/detail";
    }
}
