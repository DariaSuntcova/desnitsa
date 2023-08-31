package ru.desnitsa.backend.services;

import org.springframework.stereotype.Service;
import ru.desnitsa.backend.dto.NewsDto;
import ru.desnitsa.backend.dto.NewsUpdateDto;
import ru.desnitsa.backend.entities.ImageUrls;
import ru.desnitsa.backend.entities.News;
import ru.desnitsa.backend.exceptions.BadRequestException;
import ru.desnitsa.backend.exceptions.ContentNotFoundException;
import ru.desnitsa.backend.repositories.NewsRepository;
import ru.desnitsa.backend.utils.ImageUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class NewsService {
    private final NewsRepository newsRepository;
    private final ImageService imageService;
    private final ImageUtil imageUtil;

    public NewsService(NewsRepository newsRepository, ImageService imageService, ImageUtil imageUtil) {
        this.newsRepository = newsRepository;
        this.imageService = imageService;
        this.imageUtil = imageUtil;
    }

    public Iterable<News> getNews() {
        return newsRepository.findAll();
    }

    public News getNewsById(Long id) {
        return newsRepository.findById(id).orElseThrow(ContentNotFoundException::new);
    }

    public News saveNews(NewsDto newsDto) {
        checkNewsDto(newsDto.title(), newsDto.description(), newsDto.newsDate());
        String mainImageUrl = imageUtil.resizeAndSaveFile(newsDto.mainImageFile());

        Set<ImageUrls> imageUrlList = new HashSet<>();
        if (newsDto.imageList() != null) {
            newsDto.imageList().forEach(imageFile -> imageUrlList.add(new ImageUrls(
                    null, null, null, imageUtil.resizeAndSaveFile(imageFile))));
        }

        return newsRepository.save(
                News.of(newsDto.title(),
                        newsDto.description(),
                        newsDto.newsDate(),
                        newsDto.videoLink(),
                        mainImageUrl,
                        imageUrlList));
    }

    public News updateNews(Long id, NewsUpdateDto newsUpdateDto) {
        News news = getNewsById(id);
        checkNewsDto(newsUpdateDto.title(), newsUpdateDto.description(), newsUpdateDto.newsDate());
        String mainImageUrl = news.mainImageUrl();

        if (newsUpdateDto.mainImageFile() != null && !newsUpdateDto.mainImageFile().isEmpty()) {
            if (mainImageUrl != null) {
                imageService.deleteImage(getImageNameNoLocalhost(mainImageUrl));
            }
            mainImageUrl = imageUtil.resizeAndSaveFile(newsUpdateDto.mainImageFile());
        }

        Set<ImageUrls> imageUrlList = new HashSet<>();

        if (newsUpdateDto.imagesToDelete() != null && !newsUpdateDto.imagesToDelete().isEmpty()) {
            for (ImageUrls imageUrls : news.imageUrlList())
                if (!newsUpdateDto.imagesToDelete().contains(imageUrls.imageUrl())) {
                    imageUrlList.add(imageUrls);
                } else {
                    imageService.deleteImage(getImageNameNoLocalhost(imageUrls.imageUrl()));
                }
        }

        if (newsUpdateDto.imagesToAdd() != null && !newsUpdateDto.imagesToAdd().isEmpty()) {
            newsUpdateDto.imagesToAdd().forEach(imageFile -> imageUrlList.add(
                    new ImageUrls(null, null, null, imageUtil.resizeAndSaveFile(imageFile))));
        }

        return newsRepository.save(new News(
                id,
                newsUpdateDto.title(),
                newsUpdateDto.description(),
                newsUpdateDto.newsDate(),
                news.createdDate(),
                news.lastModifiedDate(),
                newsUpdateDto.videoLink(),
                mainImageUrl,
                imageUrlList
        ));
    }

    public void deleteNews(Long id) {
        News news = getNewsById(id);
        imageService.deleteImage(getImageNameNoLocalhost(news.mainImageUrl()));
        news.imageUrlList().forEach(imageUrls ->
                imageService.deleteImage(getImageNameNoLocalhost(imageUrls.imageUrl())));
        newsRepository.deleteById(id);
    }

    private void checkNewsDto(String title, String description, Date newsDate) {
        if (title == null || title.isEmpty() || title.isBlank()) {
            throw new BadRequestException("Заголовок не может быть пустым");
        }
        if (description == null || description.isEmpty() || description.isBlank()) {
            throw new BadRequestException("Описание не может быть пустым");
        }
        if (newsDate == null) {
            throw new BadRequestException("Дата не может быть пустой");
        }
    }

    private String getImageNameNoLocalhost(String imageNameWithLocalhost) {
        return imageNameWithLocalhost.substring(imageNameWithLocalhost.lastIndexOf("/") + 1);
    }
}
