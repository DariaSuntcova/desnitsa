package ru.desnitsa.backend.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.desnitsa.backend.dto.PartnerDto;
import ru.desnitsa.backend.entities.Partner;
import ru.desnitsa.backend.exceptions.ContentNotFoundException;
import ru.desnitsa.backend.exceptions.IOBadRequestException;
import ru.desnitsa.backend.exceptions.UnsupportedMediaTypeException;
import ru.desnitsa.backend.repositories.PartnerRepository;
import ru.desnitsa.backend.utils.ImageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PartnerServiceTest {
    /*
     * Test mainImageFile content
     */
    private static final String PARTNER_DTO_CONTENT_NAME = "imageFile";
    private static final File IMAGE = new File("./src/test/resources/partner-image-test.png");
    private static final File EMPTY_IMAGE = new File("./src/test/resources/partner-empty-image-test.png");

    private static final MockMultipartFile NOT_IMAGE_FILE_WITH_CONTENT = new MockMultipartFile(
            PARTNER_DTO_CONTENT_NAME,
            "testFile.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Test file content".getBytes()
    );

    private static MockMultipartFile getImagePngWithContent() throws IOException {
        return new MockMultipartFile(
                PARTNER_DTO_CONTENT_NAME,
                "testFile.png",
                MediaType.IMAGE_PNG_VALUE,
                new FileInputStream(IMAGE)
        );
    }

    private static MockMultipartFile getImageEmptyPng() throws IOException {
        return new MockMultipartFile(
                PARTNER_DTO_CONTENT_NAME,
                "testFile.png",
                MediaType.IMAGE_PNG_VALUE,
                new FileInputStream(EMPTY_IMAGE)
        );
    }


    private static long suiteStartTime;
    private long testStartTime;

    private PartnerRepository partnerRepository;
    private ImageService imageService;
    private ImageUtil imageUtil;
    private PartnerService partnerService;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running PartnerServiceTest");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("PartnerServiceTest complete: " + (System.nanoTime() - suiteStartTime));
    }

    @BeforeEach
    public void initTest() {
        partnerRepository = Mockito.mock(PartnerRepository.class);
        imageService = Mockito.mock(ImageService.class);
        imageUtil = Mockito.mock(ImageUtil.class);
        partnerService = new PartnerService(partnerRepository, imageService, imageUtil);
        System.out.println("Starting new test");
        testStartTime = System.nanoTime();
    }

    @AfterEach
    public void finalizeTest() {
        System.out.println("Test complete: " + (System.nanoTime() - testStartTime));
    }

    @ParameterizedTest
    @MethodSource("parametersForDeleteByIdTest")
    public void deleteByIdTest(long id) {
        String imageUrl = "image_url";
        Mockito.when(partnerRepository.findById(id)).thenReturn(Optional.of(new Partner(id, "title", imageUrl, "link")));
        partnerService.deletePartner(id);
        Mockito.verify(partnerRepository, Mockito.times(1)).deleteById(id);
        Mockito.verify(imageService, Mockito.times(1)).deleteImage(imageUrl);
    }

    private static Stream<Arguments> parametersForDeleteByIdTest() {

        return Stream.of(
                Arguments.of(0),
                Arguments.of(-99),
                Arguments.of(Long.MIN_VALUE),
                Arguments.of(Long.MAX_VALUE),
                Arguments.of(99)
        );

    }

    @ParameterizedTest
    @MethodSource("parametersForDeleteByIdThrowExceptionTest")
    public void deleteByIdThrowExceptionTest(Throwable throwable) {
        long id = 0;
        Mockito.doThrow(throwable).when(partnerRepository).deleteById(id);
        Assertions.assertDoesNotThrow(() -> partnerService.deletePartner(id));
    }

    private static Stream<Arguments> parametersForDeleteByIdThrowExceptionTest() {

        String error = "test error";

        return Stream.of(
                Arguments.of(new RuntimeException()),
                Arguments.of(new RuntimeException(error)),
                Arguments.of(new NullPointerException()),
                Arguments.of(new NullPointerException(error)),
                Arguments.of(new IllegalArgumentException()),
                Arguments.of(new IllegalArgumentException(error))
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForUpdatePartnerTest")
    public void updatePartnerTest(Partner existingPartner) {
        String newTitle = "new title";
        String newLink = "new link";
        String newImageUrl = "new url";
        PartnerDto newData = new PartnerDto(newTitle, newLink, null);
        Partner updatedPartner = new Partner(existingPartner.id(), newData.title(), newImageUrl, newData.link());

        ArgumentCaptor<Partner> captorPartner = ArgumentCaptor.forClass(Partner.class);

        Mockito.when(partnerRepository.findById(existingPartner.id())).thenReturn(Optional.of(existingPartner));
        Mockito.when(partnerRepository.save(captorPartner.capture())).thenReturn(updatedPartner);
        Mockito.when(imageUtil.resizeAndSaveFile(newData.imageFile())).thenReturn(updatedPartner.imageUrl());

        Partner result = partnerService.updatePartner(existingPartner.id(), newData);

        Mockito.verify(imageService, Mockito.times(1)).deleteImage(existingPartner.imageUrl());
        Mockito.verify(partnerRepository, Mockito.times(1)).save(captorPartner.getValue());

        Assertions.assertEquals(result, updatedPartner);
        Assertions.assertEquals(captorPartner.getValue().id(), updatedPartner.id());
        Assertions.assertEquals(captorPartner.getValue().title(), updatedPartner.title());
        Assertions.assertEquals(captorPartner.getValue().imageUrl(), updatedPartner.imageUrl());
        Assertions.assertEquals(captorPartner.getValue().link(), updatedPartner.link());
    }

    private static Stream<Arguments> parametersForUpdatePartnerTest() {

        String title = "test title";
        String link = "test link";
        String imageUrl = "test url";

        return Stream.of(
                Arguments.of(new Partner(-99L, title, imageUrl, link)),
                Arguments.of(new Partner(-99L, "", "", "")),
                Arguments.of(new Partner(-99L, "  ", " ", " ")),
                Arguments.of(new Partner(-99L, null, null, null)),
                Arguments.of(new Partner(0L, title, "", link)),
                Arguments.of(new Partner(99L, title, imageUrl, "")),
                Arguments.of(new Partner(Long.MAX_VALUE, title, null, link)),
                Arguments.of(new Partner(Long.MIN_VALUE, title, imageUrl, null))
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForUpdatePartnerExceptionTest")
    public void updatePartnerExceptionTest(Class<Exception> type, PartnerDto newData, long id) {

        if (type.isAssignableFrom(UnsupportedMediaTypeException.class)) {
            Mockito.when(partnerRepository.findById(id)).thenReturn(Optional.of(Partner.of(null, null, null)));
            Mockito.doThrow(new UnsupportedMediaTypeException()).when(imageUtil).resizeAndSaveFile(newData.imageFile());
        }

        Assertions.assertThrows(
                type,
                () -> partnerService.updatePartner(id, newData)
        );
    }

    private static Stream<Arguments> parametersForUpdatePartnerExceptionTest() throws IOException {

        return Stream.of(
                Arguments.of(IOBadRequestException.class, null, -99L),
                Arguments.of(IOBadRequestException.class, new PartnerDto(null, null, getImagePngWithContent()), Long.MIN_VALUE),
                Arguments.of(IOBadRequestException.class, new PartnerDto("", "", getImageEmptyPng()), Long.MAX_VALUE),
                Arguments.of(IOBadRequestException.class, new PartnerDto("   ", " ", NOT_IMAGE_FILE_WITH_CONTENT), 0L),
                Arguments.of(ContentNotFoundException.class, new PartnerDto("title", "link", null), 99L),
                Arguments.of(UnsupportedMediaTypeException.class, new PartnerDto("title", "link", getImagePngWithContent()), 0L)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForSaveTest")
    public void saveTest(PartnerDto data) {

        String title = data.title();
        String imageUrl = "test url";
        Partner partner = new Partner(0L, title, imageUrl, data.link());
        ArgumentCaptor<Partner> captorPartner = ArgumentCaptor.forClass(Partner.class);

        Mockito.when(partnerRepository.save(captorPartner.capture())).thenReturn(partner);
        Mockito.when(imageUtil.resizeAndSaveFile(data.imageFile())).thenReturn(imageUrl);

        Partner result = partnerService.savePartner(data);

        Assertions.assertEquals(result, partner);
        Assertions.assertEquals(captorPartner.getValue().title(), title);
        Assertions.assertEquals(captorPartner.getValue().imageUrl(), imageUrl);
        Assertions.assertEquals(captorPartner.getValue().link(), data.link());
    }

    private static Stream<Arguments> parametersForSaveTest() throws IOException {

        String title = "test title";
        String link = "test link";

        return Stream.of(
                Arguments.of(new PartnerDto(title, link, getImagePngWithContent())),
                Arguments.of(new PartnerDto(title, null, null)),
                Arguments.of(new PartnerDto(title, link, getImageEmptyPng()))
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForSaveBadRequestAndInternalServerExceptionTest")
    public void saveBadRequestAndInternalServerExceptionTest(
            Class<Exception> type,
            PartnerDto partnerDto,
            Throwable throwable
    ) {

        if (throwable != null) {
            Mockito.doThrow(throwable).when(imageUtil).resizeAndSaveFile(partnerDto.imageFile());
        }

        Assertions.assertThrows(
                type,
                () -> partnerService.savePartner(partnerDto)
        );
    }

    private static Stream<Arguments> parametersForSaveBadRequestAndInternalServerExceptionTest() throws IOException {

        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getBytes()).thenThrow(IOException.class);

        String link = "test link";

        return Stream.of(
                Arguments.of(IOBadRequestException.class, null, null),
                Arguments.of(IOBadRequestException.class, new PartnerDto(null, link, getImagePngWithContent()), null),
                Arguments.of(IOBadRequestException.class, new PartnerDto("", link, getImagePngWithContent()), null),
                Arguments.of(IOBadRequestException.class, new PartnerDto("   ", link, getImagePngWithContent()), null),
                Arguments.of(
                        UnsupportedMediaTypeException.class,
                        new PartnerDto("title", link, NOT_IMAGE_FILE_WITH_CONTENT),
                        new UnsupportedMediaTypeException()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetByIdTest")
    public void getByIdTest(Partner partner) throws ContentNotFoundException {
        long id = partner.id();
        Mockito.when(partnerRepository.findById(id)).thenReturn(Optional.of(partner));

        Partner result = partnerService.getPartner(id);

        Assertions.assertEquals(result, partner);
    }

    private static Stream<Arguments> parametersForGetByIdTest() {

        String title = "test title";
        String link = "test link";
        String imageUrl = "test url";

        return Stream.of(
                Arguments.of(new Partner(-99L, title, imageUrl, link)),
                Arguments.of(new Partner(-99L, "", "", "")),
                Arguments.of(new Partner(-99L, "  ", null, null)),
                Arguments.of(new Partner(-99L, null, null, "")),
                Arguments.of(new Partner(0L, title, "", null)),
                Arguments.of(new Partner(99L, title, " ", null)),
                Arguments.of(new Partner(Long.MAX_VALUE, title, null, " ")),
                Arguments.of(new Partner(Long.MIN_VALUE, title, imageUrl, link))
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetByIdThrowExceptionTest")
    public void getByIdThrowExceptionTest(Throwable throwable) {
        Partner partner = new Partner(1L, "title", "image url", "link");

        long id;
        Class<? extends Throwable> type;
        if (throwable == null) {
            id = 0;
            Mockito.when(partnerRepository.findById(id)).thenReturn(Optional.empty());
            type = ContentNotFoundException.class;
        } else {
            id = partner.id();
            type = throwable.getClass();
        }

        if (throwable != null) {
            Mockito.when(partnerRepository.findById(id)).thenThrow(throwable);
        }

        Assertions.assertThrows(
                type,
                () -> partnerService.getPartner(id)
        );
    }

    private static Stream<Arguments> parametersForGetByIdThrowExceptionTest() {

        return Stream.of(
                Arguments.of((Throwable) null),
                Arguments.of(new RuntimeException()),
                Arguments.of(new NullPointerException()),
                Arguments.of(new IllegalArgumentException())
        );
    }


    @ParameterizedTest
    @MethodSource("parametersForGetAllPartnersTest")
    public void getAllPartnersTest(List<Partner> partners) {

        Mockito.when(partnerRepository.findAll()).thenReturn(partners);

        List<Partner> result = (List<Partner>) partnerRepository.findAll();

        Assertions.assertEquals(result.size(), partners.size());
        for (int i = 0; i < partners.size(); i++) {
            Assertions.assertEquals(result.get(i), partners.get(i));
        }
    }

    private static Stream<Arguments> parametersForGetAllPartnersTest() {

        String title = "test title";
        String imageUrl = "test url";
        String link = "test link";

        return Stream.of(
                Arguments.of(List.of(
                        new Partner(-99L, title, imageUrl, link),
                        new Partner(-99L, "", imageUrl, ""),
                        new Partner(-99L, null, null, link),
                        new Partner(0L, title, "", null),
                        new Partner(99L, title, " ", link),
                        new Partner(Long.MAX_VALUE, title, imageUrl, ""),
                        new Partner(Long.MIN_VALUE, title, imageUrl, null)
                )),
                Arguments.of(new ArrayList<Partner>())
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetAllPartnersThrowExceptionTest")
    public void getAllPartnersThrowExceptionTest(Throwable throwable) {

        Mockito.doThrow(throwable).when(partnerRepository).findAll();

        Assertions.assertThrows(
                throwable.getClass(),
                () -> partnerService.getAllPartners(),
                throwable.getMessage()
        );
    }

    private static Stream<Arguments> parametersForGetAllPartnersThrowExceptionTest() {

        String error = "test error";

        return Stream.of(
                Arguments.of(new RuntimeException()),
                Arguments.of(new RuntimeException(error)),
                Arguments.of(new NullPointerException()),
                Arguments.of(new NullPointerException(error)),
                Arguments.of(new IllegalArgumentException()),
                Arguments.of(new IllegalArgumentException(error))
        );
    }
}