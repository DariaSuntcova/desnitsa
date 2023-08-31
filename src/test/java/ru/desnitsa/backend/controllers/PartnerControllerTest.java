package ru.desnitsa.backend.controllers;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindingResult;
import ru.desnitsa.backend.dto.PartnerDto;
import ru.desnitsa.backend.entities.Partner;
import ru.desnitsa.backend.exceptions.ContentNotFoundException;
import ru.desnitsa.backend.exceptions.IOBadRequestException;
import ru.desnitsa.backend.services.PartnerService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PartnerControllerTest {

    /*
     * Test mainImageFile content
     */
    private static final String PARTNER_DTO_CONTENT_NAME = "imageFile";
    private static final File IMAGE = new File("./src/test/resources/partner-image-test.png");
    private static final File EMPTY_IMAGE = new File("./src/test/resources/partner-empty-image-test.png");

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

    private PartnerService partnerService;
    private PartnerController partnerController;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running PartnerControllerTest");
        suiteStartTime = System.nanoTime();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("PartnerControllerTest complete: " + (System.nanoTime() - suiteStartTime));
    }

    @BeforeEach
    public void initTest() {
        partnerService = Mockito.mock(PartnerService.class);
        partnerController = new PartnerController(partnerService);
        System.out.println("Starting new test");
        testStartTime = System.nanoTime();
    }

    @AfterEach
    public void finalizeTest() {
        System.out.println("Test complete: " + (System.nanoTime() - testStartTime));
    }

    @ParameterizedTest
    @MethodSource("parametersForDeleteByIdTest")
    public void deleteByIdTest(long id, Throwable throwable) {

        if (throwable != null) {
            Mockito.doThrow(throwable).when(partnerService).deletePartner(id);
            Assertions.assertThrows(
                    throwable.getClass(),
                    () -> partnerController.deletePartner(id),
                    throwable.getMessage()
            );
        } else {
            partnerController.deletePartner(id);
        }

        Mockito.verify(partnerService, Mockito.times(1)).deletePartner(id);
    }

    private static Stream<Arguments> parametersForDeleteByIdTest() {

        return Stream.of(
                Arguments.of(0, null),
                Arguments.of(-99, null),
                Arguments.of(Long.MIN_VALUE, null),
                Arguments.of(Long.MAX_VALUE, null),
                Arguments.of(99, null),
                Arguments.of(99, new RuntimeException()),
                Arguments.of(99, new NullPointerException()),
                Arguments.of(99, new IllegalArgumentException())
        );

    }

    @ParameterizedTest
    @MethodSource("parametersForUpdateTest")
    public void updateTest(long id, PartnerDto newData, String imageUrl) {
        BindingResult bindingResult = Mockito.mock(BindingResult.class);

        Partner partner = null;
        if (newData != null) {
            partner = new Partner(id, newData.title(), imageUrl, newData.link());
        }

        Mockito.when(partnerService.updatePartner(id, newData)).thenReturn(partner);

        Partner result = partnerController.updatePartner(id, newData, bindingResult);

        Mockito.verify(partnerService, Mockito.times(1)).updatePartner(id, newData);
        Assertions.assertEquals(result, partner);
    }

    private static Stream<Arguments> parametersForUpdateTest() throws IOException {

        String title = "test title";
        String link = "test link";
        String imageUrl = "test url";
        return Stream.of(
                Arguments.of(-99L, new PartnerDto(title, link, getImagePngWithContent()), imageUrl),
                Arguments.of(-99L, new PartnerDto(null, null, null), imageUrl),
                Arguments.of(0L, new PartnerDto(title, link, getImagePngWithContent()), imageUrl),
                Arguments.of(99L, new PartnerDto(title, link, getImagePngWithContent()), imageUrl),
                Arguments.of(Long.MAX_VALUE, new PartnerDto(title, link, getImagePngWithContent()), imageUrl),
                Arguments.of(Long.MIN_VALUE, new PartnerDto(title, link, getImagePngWithContent()), imageUrl),
                Arguments.of(0L, null, imageUrl)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForUpdateThrowExceptionTest")
    public void updateThrowExceptionTest(Throwable throwable) throws IOException {
        String title = "test title";
        String link = "test link";
        long id = 0;
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        PartnerDto partner = new PartnerDto(title, link, getImagePngWithContent());

        Mockito.doThrow(throwable).when(partnerService).updatePartner(id, partner);

        Assertions.assertThrows(
                throwable.getClass(),
                () -> partnerController.updatePartner(id, partner, bindingResult),
                throwable.getMessage()
        );

        Mockito.verify(partnerService, Mockito.times(1)).updatePartner(id, partner);
    }

    private static Stream<Arguments> parametersForUpdateThrowExceptionTest() {

        String error = "test error";

        return Stream.of(
                Arguments.of(new RuntimeException()),
                Arguments.of(new RuntimeException(error)),
                Arguments.of(new NullPointerException()),
                Arguments.of(new NullPointerException(error)),
                Arguments.of(new IllegalArgumentException()),
                Arguments.of(new IllegalArgumentException(error)),
                Arguments.of(new OptimisticLockingFailureException(error)),
                Arguments.of(new ContentNotFoundException()),
                Arguments.of(new IOBadRequestException())
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForSaveTest")
    public void saveTest(long id, PartnerDto newData) {
        String imageUrl = "test url";
        BindingResult bindingResult = Mockito.mock(BindingResult.class);

        Partner partner = null;
        if (newData != null) {
            partner = new Partner(id, newData.title(), imageUrl, newData.link());
        }
        Mockito.when(partnerService.savePartner(newData)).thenReturn(partner);

        Partner result = partnerController.savePartner(newData, bindingResult);

        Mockito.verify(partnerService, Mockito.times(1)).savePartner(newData);
        Assertions.assertEquals(result, partner);
    }

    private static Stream<Arguments> parametersForSaveTest() throws IOException {

        String title = "test title";
        String link = "test link";

        return Stream.of(
                Arguments.of(-99L, new PartnerDto(title, link, getImagePngWithContent())),
                Arguments.of(-99L, new PartnerDto(null, null, null)),
                Arguments.of(0L, new PartnerDto(title, link, getImagePngWithContent())),
                Arguments.of(99L, new PartnerDto(title, link, getImagePngWithContent())),
                Arguments.of(Long.MAX_VALUE, new PartnerDto(title, link, getImagePngWithContent())),
                Arguments.of(Long.MIN_VALUE, new PartnerDto(title, null, getImageEmptyPng())),
                Arguments.of(0L, null)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForSaveThrowExceptionTest")
    public void saveThrowExceptionTest(Throwable throwable) throws IOException {
        String title = "test title";
        String link = "test link";
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        PartnerDto partner = new PartnerDto(title, link, getImagePngWithContent());

        Mockito.doThrow(throwable).when(partnerService).savePartner(partner);

        Assertions.assertThrows(
                throwable.getClass(),
                () -> partnerController.savePartner(partner, bindingResult),
                throwable.getMessage()
        );

        Mockito.verify(partnerService, Mockito.times(1)).savePartner(partner);
    }

    private static Stream<Arguments> parametersForSaveThrowExceptionTest() {

        String error = "test error";

        return Stream.of(
                Arguments.of(new RuntimeException()),
                Arguments.of(new RuntimeException(error)),
                Arguments.of(new NullPointerException()),
                Arguments.of(new NullPointerException(error)),
                Arguments.of(new IllegalArgumentException()),
                Arguments.of(new IllegalArgumentException(error)),
                Arguments.of(new OptimisticLockingFailureException(error)),
                Arguments.of(new IOBadRequestException())
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetByIdTest")
    public void getByIdTest(Partner partner) {

        long id;
        if (partner == null) {
            id = 0;
        } else {
            id = partner.id();
        }

        Mockito.when(partnerService.getPartner(id)).thenReturn(partner);

        Partner result = partnerController.getPartner(id);

        if (partner == null) {
            Assertions.assertNull(result);
        } else {
            Assertions.assertEquals(result.title(), partner.title());
            Assertions.assertEquals(result.imageUrl(), partner.imageUrl());
            Assertions.assertEquals(result.id(), partner.id());
            Assertions.assertEquals(result.link(), partner.link());
        }

        Mockito.verify(partnerService, Mockito.times(1)).getPartner(id);
    }

    private static Stream<Arguments> parametersForGetByIdTest() {

        String title = "test title";
        String link = "test link";
        String imageUrl = "test url";

        return Stream.of(
                Arguments.of(new Partner(-99L, title, imageUrl, link)),
                Arguments.of(new Partner(-99L, "", "", "")),
                Arguments.of(new Partner(-99L, null, null, null)),
                Arguments.of(new Partner(0L, title, "", null)),
                Arguments.of(new Partner(99L, title, null, "")),
                Arguments.of(new Partner(Long.MAX_VALUE, title, imageUrl, link)),
                Arguments.of(new Partner(Long.MIN_VALUE, title, null, link)),
                Arguments.of((Object) null)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetByIdThrowExceptionTest")
    public void getByIdThrowExceptionTest(Throwable throwable) throws ContentNotFoundException {
        long id = 0;
        Mockito.doThrow(throwable).when(partnerService).getPartner(id);

        Assertions.assertThrows(
                throwable.getClass(),
                () -> partnerController.getPartner(id),
                throwable.getMessage()
        );

        Mockito.verify(partnerService, Mockito.times(1)).getPartner(id);
    }

    private static Stream<Arguments> parametersForGetByIdThrowExceptionTest() {

        String error = "test error";

        return Stream.of(
                Arguments.of(new RuntimeException()),
                Arguments.of(new RuntimeException(error)),
                Arguments.of(new NullPointerException()),
                Arguments.of(new NullPointerException(error)),
                Arguments.of(new IllegalArgumentException()),
                Arguments.of(new IllegalArgumentException(error)),
                Arguments.of(new OptimisticLockingFailureException(error)),
                Arguments.of(new ContentNotFoundException())
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetAllPartnersTest")
    public void getAllPartnersTest(List<Partner> partners) {
        Mockito.when(partnerService.getAllPartners()).thenReturn(partners);
        List<Partner> result = (List<Partner>) partnerService.getAllPartners();

        Mockito.verify(partnerService, Mockito.times(1)).getAllPartners();

        if (partners == null) {
            Assertions.assertNull(result);
        } else {
            Assertions.assertEquals(result.size(), partners.size());
            for (int i = 0; i < partners.size(); i++) {
                Assertions.assertEquals(result.get(i), partners.get(i));
            }
        }
    }

    private static Stream<Arguments> parametersForGetAllPartnersTest() {

        String title = "test title";
        String link = "test link";
        String imageUrl = "test url";

        return Stream.of(
                Arguments.of(List.of(
                        new Partner(-99L, title, imageUrl, link),
                        new Partner(-99L, "", "", ""),
                        new Partner(-99L, null, null, null),
                        new Partner(0L, title, imageUrl, null),
                        new Partner(99L, title, null, link),
                        new Partner(Long.MAX_VALUE, title, "", null),
                        new Partner(Long.MIN_VALUE, title, null, "")
                )),
                Arguments.of(new ArrayList<Partner>()),
                null
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetAllPartnersThrowExceptionTest")
    public void getAllPartnersThrowExceptionTest(Throwable throwable) {

        Mockito.doThrow(throwable).when(partnerService).getAllPartners();

        Assertions.assertThrows(
                throwable.getClass(),
                () -> partnerController.getAllPartners(),
                throwable.getMessage()
        );

        Mockito.verify(partnerService, Mockito.times(1)).getAllPartners();
    }

    private static Stream<Arguments> parametersForGetAllPartnersThrowExceptionTest() {

        String error = "test error";

        return Stream.of(
                Arguments.of(new RuntimeException()),
                Arguments.of(new RuntimeException(error)),
                Arguments.of(new NullPointerException()),
                Arguments.of(new NullPointerException(error)),
                Arguments.of(new IllegalArgumentException()),
                Arguments.of(new IllegalArgumentException(error)),
                Arguments.of(new OptimisticLockingFailureException(error))
        );
    }
}