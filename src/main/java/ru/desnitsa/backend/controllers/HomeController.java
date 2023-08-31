package ru.desnitsa.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.desnitsa.backend.dto.AboutDto;
import ru.desnitsa.backend.entities.About;
import ru.desnitsa.backend.services.AboutService;

@RestController
@RequestMapping("/")
public class HomeController {
    private final AboutService aboutService;

    public HomeController(AboutService aboutService) {
        this.aboutService = aboutService;
    }

    @GetMapping
    public Iterable<About> getAbout() {
        return aboutService.getAbout();
    }

    @PostMapping
    public About saveOrUpdateAbout(@ModelAttribute AboutDto aboutDto) {
        return aboutService.saveOrUpdateAbout(aboutDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAbout(@PathVariable Long id) {
        aboutService.deleteAbout(id);
    }
}
