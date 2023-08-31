package ru.desnitsa.backend.services;

import org.springframework.stereotype.Service;
import ru.desnitsa.backend.dto.AboutDto;
import ru.desnitsa.backend.entities.About;
import ru.desnitsa.backend.repositories.AboutRepository;
import ru.desnitsa.backend.utils.ImageUtil;

import java.util.Optional;

@Service
public class AboutService {
    private final AboutRepository aboutRepository;
    private final ImageService imageService;
    private final ImageUtil imageUtil;

    public AboutService(AboutRepository aboutRepository, ImageService imageService, ImageUtil imageUtil) {
        this.aboutRepository = aboutRepository;
        this.imageService = imageService;
        this.imageUtil = imageUtil;
    }

    public Iterable<About> getAbout() {
        return aboutRepository.findAll();
    }

    public About saveOrUpdateAbout(AboutDto aboutDto) {
        String firstImageUrl = imageUtil.resizeAndSaveFile(aboutDto.firstImageFile());
        String secondImageUrl = imageUtil.resizeAndSaveFile(aboutDto.secondImageFile());
        return aboutRepository.findAboutByConstant("about")
                .map(existingAbout -> new About(existingAbout.id(),
                        existingAbout.constant(),
                        aboutDto.description(),
                        firstImageUrl,
                        secondImageUrl))
                .map(aboutRepository::save)
                .orElseGet(() -> aboutRepository.save(About.of(aboutDto.description(), firstImageUrl, secondImageUrl)));
    }

    public void deleteAbout(Long id) {
        String firstImageUrl, secondImageUrl;
        Optional<About> optionalAbout = aboutRepository.findById(id);
        if (optionalAbout.isPresent()) {
            firstImageUrl = optionalAbout.get().firstImageUrl();
            if (firstImageUrl != null) {
                imageService.deleteImage(firstImageUrl.substring(firstImageUrl.lastIndexOf("/") + 1));
            }
            secondImageUrl = optionalAbout.get().secondImageUrl();
            if (secondImageUrl != null) {
                imageService.deleteImage(secondImageUrl.substring(secondImageUrl.lastIndexOf("/") + 1));
            }
            aboutRepository.deleteById(id);
        }
    }
}
