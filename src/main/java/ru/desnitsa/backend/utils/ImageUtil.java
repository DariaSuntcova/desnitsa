package ru.desnitsa.backend.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.imgscalr.Scalr;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.desnitsa.backend.entities.Image;
import ru.desnitsa.backend.exceptions.UnsupportedMediaTypeException;
import ru.desnitsa.backend.repositories.ImageRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ImageUtil {
    private static final String HOST_ADDRESS = "http://localhost:9090/image/";
    private static final List<String> contentType = List.of("image/gif", "image/jpeg", "image/png", "image/bmp");
    private final ImageRepository imageRepository;

    public ImageUtil(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Transactional
    public String resizeAndSaveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (!contentType.contains(file.getContentType())) {
            throw new UnsupportedMediaTypeException();
        }
        String fileName = RandomStringUtils.randomAlphanumeric(10, 16) + ".jpg";
        File imageFile = new File(fileName);
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage scaledImage;
            int height = originalImage.getHeight();
            if (height > 900) {
                scaledImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_HEIGHT, 900);
                ImageIO.write(scaledImage, "jpg", imageFile);
                FileInputStream fileInputStream = new FileInputStream(imageFile);
                imageRepository.save(new Image(null, fileName, MediaType.IMAGE_JPEG_VALUE, imageFile.length(), fileInputStream.readAllBytes()));
                fileInputStream.close();
            } else {
                imageRepository.save(new Image(null, fileName, file.getContentType(), file.getSize(), file.getBytes()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            imageFile.delete();
        }
        return HOST_ADDRESS + fileName;
    }
}