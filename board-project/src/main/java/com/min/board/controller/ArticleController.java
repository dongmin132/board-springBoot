package com.min.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
}
