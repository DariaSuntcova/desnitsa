package ru.desnitsa.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.desnitsa.backend.dto.NewsDto;
import ru.desnitsa.backend.dto.NewsUpdateDto;
import ru.desnitsa.backend.entities.News;
import ru.desnitsa.backend.services.NewsService;

@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public Iterable<News> getNews() {
        return newsService.getNews();
    }

    @GetMapping("{id}")
    public News getNewsById(@PathVariable Long id) {
        return newsService.getNewsById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public News saveNews(@ModelAttribute NewsDto newsDto) {
        return newsService.saveNews(newsDto);
    }

    @PutMapping(value = "{id}")
    public News updateNews(@ModelAttribute NewsUpdateDto newsUpdateDto, @PathVariable Long id) {
        return newsService.updateNews(id, newsUpdateDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
    }
}
