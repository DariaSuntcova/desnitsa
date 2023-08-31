package ru.desnitsa.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.desnitsa.backend.dto.TourDto;
import ru.desnitsa.backend.entities.Tour;
import ru.desnitsa.backend.services.TourService;

@RestController
@RequestMapping("/tour")
public class TourController {
    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public Iterable<Tour> getTours() {
        return tourService.getTours();
    }

    @GetMapping("{id}")
    public Tour getTourById(@PathVariable Long id) {
        return tourService.getTourById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Tour postTour(@ModelAttribute TourDto tourDto) {
        return tourService.saveTour(tourDto);
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Tour updateTour(@ModelAttribute TourDto tourDto, @PathVariable Long id) {
        return tourService.updateTour(id, tourDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
    }
}
