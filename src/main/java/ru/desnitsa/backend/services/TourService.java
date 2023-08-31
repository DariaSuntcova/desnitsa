package ru.desnitsa.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.desnitsa.backend.dto.TourDto;
import ru.desnitsa.backend.entities.ImageUrls;
import ru.desnitsa.backend.entities.Tour;
import ru.desnitsa.backend.exceptions.ContentNotFoundException;
import ru.desnitsa.backend.repositories.TourRepository;

import ru.desnitsa.backend.utils.ImageUtil;

import java.util.*;

@Service
public class TourService {
    private final TourRepository tourRepository;
    private final ImageService imageService;
    private final ImageUtil imageUtil;

    public TourService(TourRepository tourRepository, ImageService imageService, ImageUtil imageUtil) {
        this.tourRepository = tourRepository;
        this.imageService = imageService;
        this.imageUtil = imageUtil;
    }

    public Iterable<Tour> getTours() {
        return tourRepository.findAll();
    }

    public Tour getTourById(Long id) {
        return tourRepository.findById(id).orElseThrow(ContentNotFoundException::new);
    }

    public Tour saveTour(TourDto tourDto) {
        String mainImageUrl = imageUtil.resizeAndSaveFile(tourDto.mainImageFile());
        Set<ImageUrls> imageUrlList = new HashSet<>();
        Spliterator<MultipartFile> spliterator = tourDto.imageList().spliterator();
        while (spliterator.tryAdvance(imageFile -> imageUrlList.add(new ImageUrls(null, null, null, imageUtil.resizeAndSaveFile(imageFile)))));
        return tourRepository.save(Tour.of(tourDto.title(),
                tourDto.description(),
                tourDto.tourDate(),
                mainImageUrl,
                imageUrlList));
    }

    public Tour updateTour(Long id, TourDto tourDto) {
        String mainImageUrl = imageUtil.resizeAndSaveFile(tourDto.mainImageFile());
        Set<ImageUrls> imageUrlList = new HashSet<>();
        Spliterator<MultipartFile> spliterator = tourDto.imageList().spliterator();
        while (spliterator.tryAdvance(imageFile -> imageUrlList.add(new ImageUrls(null, null, null,imageUtil.resizeAndSaveFile(imageFile)))));
        return tourRepository.findById(id)
                .map(existingTour -> new Tour(existingTour.id(),
                                tourDto.title(),
                                tourDto.description(),
                                tourDto.tourDate(),
                                existingTour.createdDate(),
                                existingTour.lastModifiedDate(),
                                mainImageUrl,
                                imageUrlList
                        )
                )
                .map(tourRepository::save)
                .orElseThrow(ContentNotFoundException::new);
    }

    public void deleteTour(Long id) {
        String mainImageUrl;
        Set<String> imageUrlList = new HashSet<>();
        Optional<Tour> optionalTour = tourRepository.findById(id);
        if (optionalTour.isPresent()) {
            mainImageUrl = optionalTour.get().mainImageUrl();
            optionalTour.get().imageUrlList().forEach(imageUrls -> imageUrlList.add(imageUrls.imageUrl()));
            imageUrlList.add(mainImageUrl);
            imageUrlList.remove(null);
            imageUrlList.forEach(imageName -> imageService.deleteImage(imageName.substring(imageName.lastIndexOf("/") + 1)));
            tourRepository.deleteById(id);
        }
    }
}
