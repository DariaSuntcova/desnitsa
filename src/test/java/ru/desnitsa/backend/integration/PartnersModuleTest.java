package ru.desnitsa.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.desnitsa.backend.entities.Partner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class PartnersModuleTest {

    /*
     * Endpoint and constants
     */
    private static final File IMAGE = new File("./src/test/resources/partner-image-test.png");
    private static final File EMPTY_IMAGE = new File("./src/test/resources/partner-empty-image-test.png");
    private static final String PREFIX_FILE_URL = "http://localhost:9090/image/";
    private static final String PARTNER_ENDPOINT = "/partners";
    private static final String PARTNER_DTO_TITLE_NAME = "title";
    private static final String PARTNER_DTO_TITLE_VALUE = "Test Partner";
    private static final String PARTNER_DTO_LINK_NAME = "link";
    private static final String PARTNER_DTO_LINK_VALUE = "Test link";
    private static final String PARTNER_DTO_CONTENT_NAME = "imageFile";
    private static final String PARTNER_DTO_TITLE_VALUE_EMPTY = "";
    private static final String PARTNER_DTO_TITLE_BLANK = "    ";
    private static final String PARTNER_DTO_TITLE_NULL = null;

    /*
     * Response
     */
    private static final int RESPONSE_OK = 200;
    private static final int RESPONSE_CREATED = 201;
    private static final int RESPONSE_DELETED = 204;
    private static final int RESPONSE_NOT_FOUND = 404;
    private static final String BAD_REQUEST_MESSAGE = "Ошибка чтения/записи.";
    private static final String BAD_REQUEST_VARIABLE_TYPE_MESSAGE = "";
    private static final String FORBIDDEN_MESSAGE = "Доступ к контенту запрещен";
    private static final String NOT_FOUND_MESSAGE = "Контент не найден.";
    private static final String UNSUPPORTED_MEDIA_TYPE_MESSAGE = "Один или несколько файлов не являются изображением gif, jpg, png, bmp";

    /*
     * Test mainImageFile content
     */
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

    private static final MockMultipartFile NOT_IMAGE_FILE_WITH_CONTENT = new MockMultipartFile(
            PARTNER_DTO_CONTENT_NAME,
            "testFile.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Test file content".getBytes()
    );

    /*
     * Init database and security configuration
     */
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    private static void testProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url=", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username=", POSTGRES::getUsername);
        registry.add("spring.datasource.password=", POSTGRES::getPassword);
    }

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper mapper;
    private MockMvc mockMvc;
    private static long suiteStartTime;
    private long testStartTime;

    @BeforeAll
    public static void initSuite() {
        System.out.println("Running PartnersModuleIntegrationTests");
        suiteStartTime = System.nanoTime();
        POSTGRES
                .withReuse(true)
                .withDatabaseName("postgres")
                .withUsername("test")
                .withPassword("test")
                .start();
    }

    @AfterAll
    public static void completeSuite() {
        System.out.println("PartnersModuleIntegrationTests complete: " + (System.nanoTime() - suiteStartTime));
    }

    @BeforeEach
    public void initTest() {
        System.out.println("Starting new test");
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        testStartTime = System.nanoTime();
    }

    @AfterEach
    public void finalizeTest() {
        System.out.println("Test complete: " + (System.nanoTime() - testStartTime));
        SecurityContextHolder.clearContext();
    }

    @Test
    public void addPartnerWithInvalidFileTest() throws Exception {
        MockHttpServletRequestBuilder request = multipart(PARTNER_ENDPOINT)
                .file(NOT_IMAGE_FILE_WITH_CONTENT)
                .param(PARTNER_DTO_TITLE_NAME, PARTNER_DTO_TITLE_VALUE)
                .param(PARTNER_DTO_LINK_NAME, PARTNER_DTO_LINK_VALUE);
        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(status().isUnsupportedMediaType(), content().string(UNSUPPORTED_MEDIA_TYPE_MESSAGE));
    }

    @ParameterizedTest
    @MethodSource("parametersForAddPartnerSuccessTest")
    public void addPartnerWithFileSuccessTest(
            String title,
            String link,
            MockMultipartFile file
    ) throws Exception {
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart(PARTNER_ENDPOINT);
        if (file != null) {
            requestBuilder.file(file);
        }
        MockHttpServletRequestBuilder request =
                requestBuilder
                        .param(PARTNER_DTO_TITLE_NAME, title)
                        .param(PARTNER_DTO_LINK_NAME, link);
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

        String json = response.getContentAsString();
        Partner partner = mapper.readValue(json, Partner.class);

        Assertions.assertEquals(response.getStatus(), RESPONSE_CREATED);
        Assertions.assertEquals(partner.title(), title);
        Assertions.assertEquals(partner.link(), link);
        Assertions.assertTrue(partner.id() > 0);

        if (file == null) {
            Assertions.assertNull(partner.imageUrl());
        } else {
            Assertions.assertNotNull(partner.imageUrl());
            Assertions.assertTrue(partner.imageUrl().startsWith(PREFIX_FILE_URL));
        }
    }

    private static Stream<Arguments> parametersForAddPartnerSuccessTest() throws IOException {

        return Stream.of(
                Arguments.of(
                        PARTNER_DTO_TITLE_VALUE,
                        PARTNER_DTO_LINK_VALUE,
                        getImagePngWithContent()
                ), Arguments.of(
                        PARTNER_DTO_TITLE_VALUE,
                        null,
                        getImagePngWithContent()
                ), Arguments.of(
                        PARTNER_DTO_TITLE_VALUE,
                        PARTNER_DTO_LINK_VALUE,
                        null
                ), Arguments.of(
                        PARTNER_DTO_TITLE_VALUE,
                        PARTNER_DTO_LINK_VALUE,
                        getImageEmptyPng()
                ),
                Arguments.of(
                        PARTNER_DTO_TITLE_VALUE,
                        null,
                        null
                )
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForAddPartnerBadRequestTest")
    public void addPartnerBadRequestTest(String title) throws Exception {
        MockHttpServletRequestBuilder request;
        if (title != null && title.equals("null partner")) {
            request = multipart(PARTNER_ENDPOINT)
                    .param("Partner", (String) null);
        } else {
            request =
                    multipart(PARTNER_ENDPOINT)
                            .file(getImagePngWithContent())
                            .param(PARTNER_DTO_TITLE_NAME, title)
                            .param(PARTNER_DTO_LINK_NAME, PARTNER_DTO_LINK_VALUE);
        }

        mockMvc.perform(request).andExpect(status().isBadRequest()).andExpect(content().string(BAD_REQUEST_MESSAGE));
    }

    private static Stream<Arguments> parametersForAddPartnerBadRequestTest() {

        return Stream.of(
                Arguments.of(PARTNER_DTO_TITLE_VALUE_EMPTY),
                Arguments.of(PARTNER_DTO_TITLE_BLANK),
                Arguments.of(PARTNER_DTO_TITLE_NULL),
                Arguments.of("null partner")
        );
    }

    @Test
    public void addPartnerUnsupportedMediaTypeTest() throws Exception {
        MockHttpServletRequestBuilder request = multipart(PARTNER_ENDPOINT)
                .file(NOT_IMAGE_FILE_WITH_CONTENT)
                .param(PARTNER_DTO_TITLE_NAME, PARTNER_DTO_TITLE_VALUE)
                .param(PARTNER_DTO_LINK_NAME, PARTNER_DTO_LINK_VALUE);
        mockMvc.perform(request).andExpectAll(
                status().isUnsupportedMediaType(),
                content().string(UNSUPPORTED_MEDIA_TYPE_MESSAGE)
        );
    }

    @Test
    public void getAllPartnersTest() throws Exception {
        mockMvc.perform(getRequest(getImagePngWithContent()));
        mockMvc.perform(getRequest(null));

        MockMvc mockMvcWithCsrf =
                MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        MockHttpServletResponse response = mockMvcWithCsrf.perform(get(PARTNER_ENDPOINT)).andReturn().getResponse();
        String json = response.getContentAsString();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Partner.class);
        List<Partner> partners = mapper.readValue(json, collectionType);

        Assertions.assertEquals(response.getStatus(), RESPONSE_OK);
        Assertions.assertNotNull(partners);
        Assertions.assertFalse(partners.isEmpty());
        Assertions.assertEquals(partners.size(), 2);
        Assertions.assertEquals(partners.get(0).id(), 1);
        Assertions.assertEquals(partners.get(0).title(), PARTNER_DTO_TITLE_VALUE);
        Assertions.assertEquals(partners.get(0).link(), PARTNER_DTO_LINK_VALUE);
        Assertions.assertNotNull(partners.get(0).imageUrl());
        Assertions.assertTrue(partners.get(0).imageUrl().startsWith(PREFIX_FILE_URL));
        Assertions.assertEquals(partners.get(1).id(), 2);
        Assertions.assertEquals(partners.get(1).title(), PARTNER_DTO_TITLE_VALUE);
        Assertions.assertEquals(partners.get(1).link(), PARTNER_DTO_LINK_VALUE);
        Assertions.assertNull(partners.get(1).imageUrl());
    }

    @ParameterizedTest
    @MethodSource("parametersForDeletePartnerTest")
    public void deletePartnerTest(long id) throws Exception {
        String path = String.format("%s/%d", PARTNER_ENDPOINT, id);
        MockHttpServletRequestBuilder request = delete(path);
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();

        String body = response.getContentAsString();
        Assertions.assertEquals(response.getStatus(), RESPONSE_DELETED);
        Assertions.assertTrue(body.isEmpty());
    }

    private static Stream<Arguments> parametersForDeletePartnerTest() {

        return Stream.of(
                Arguments.of(0),
                Arguments.of(-99),
                Arguments.of(99),
                Arguments.of(Long.MIN_VALUE),
                Arguments.of(Long.MAX_VALUE)
        );
    }

    @Test
    public void deletePartnerBadRequestTest() throws Exception {
        String path = String.format("%s/%s", PARTNER_ENDPOINT, "id");
        MockHttpServletRequestBuilder request = delete(path);
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    public void updatePartnerWithInvalidFileSuccessTest() throws Exception {
        Partner partner = createPartner();
        String path = String.format("%s/%d", PARTNER_ENDPOINT, partner.id());
        MockHttpServletRequestBuilder requestBuilder = multipart(HttpMethod.PUT, path)
                .file(NOT_IMAGE_FILE_WITH_CONTENT)
                .param(PARTNER_DTO_TITLE_NAME, PARTNER_DTO_TITLE_VALUE)
                .param(PARTNER_DTO_LINK_NAME, PARTNER_DTO_LINK_VALUE);
        MockHttpServletRequestBuilder request = requestBuilder.param(PARTNER_DTO_TITLE_NAME, PARTNER_DTO_TITLE_VALUE);

        mockMvc.perform(request)
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string(UNSUPPORTED_MEDIA_TYPE_MESSAGE));
    }

    @ParameterizedTest
    @MethodSource("parametersForUpdatePartnerSuccessTest")
    public void updatePartnerWithFileSuccessTest(
            String title,
            String link,
            MockMultipartFile file
    ) throws Exception {
        Partner partner = createPartner();
        String path = String.format("%s/%d", PARTNER_ENDPOINT, partner.id());
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart(HttpMethod.PUT, path);
        if (file != null) {
            requestBuilder.file(file);
        }
        MockHttpServletRequestBuilder request = requestBuilder
                .param(PARTNER_DTO_TITLE_NAME, title)
                .param(PARTNER_DTO_LINK_NAME, link);
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().getResponse();
        String json = response.getContentAsString();
        Partner partnerNew = mapper.readValue(json, Partner.class);

        Assertions.assertEquals(response.getStatus(), RESPONSE_OK);
        Assertions.assertEquals(partnerNew.id(), partner.id());
        Assertions.assertEquals(partnerNew.title(), title);
        Assertions.assertEquals(partnerNew.link(), link);

        if (file == null) {
            Assertions.assertNull(partnerNew.imageUrl());
        } else {
            Assertions.assertNotNull(partnerNew.imageUrl());
            Assertions.assertTrue(partnerNew.imageUrl().startsWith(PREFIX_FILE_URL));
        }
    }

    private static Stream<Arguments> parametersForUpdatePartnerSuccessTest() throws IOException {

        return Stream.of(
                Arguments.of(
                        PARTNER_DTO_TITLE_VALUE,
                        PARTNER_DTO_LINK_VALUE,
                        getImagePngWithContent()
                ),
                Arguments.of(
                        PARTNER_DTO_TITLE_VALUE,
                        PARTNER_DTO_LINK_VALUE,
                        null
                ),
                Arguments.of(
                        "....",
                        null,
                        getImagePngWithContent()
                ),
                Arguments.of(
                        "=====",
                        "",
                        getImagePngWithContent()
                ),
                Arguments.of(
                        PARTNER_DTO_TITLE_VALUE,
                        PARTNER_DTO_LINK_VALUE,
                        getImageEmptyPng()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForUpdatePartnerBadRequestTest")
    public void updatePartnerBadRequestTest(String title, String expected) throws Exception {

        Partner partner = createPartner();

        String path;
        if (expected.equals(BAD_REQUEST_VARIABLE_TYPE_MESSAGE)) {
            path = String.format("%s/%s", PARTNER_ENDPOINT, "id");
        } else {
            path = String.format("%s/%d", PARTNER_ENDPOINT, partner.id());
        }

        MockHttpServletRequestBuilder request;
        if (title != null && title.equals("null partner")) {
            request = multipart(HttpMethod.PUT, path)
                    .param("Partner", (String) null);
        } else {
            request =
                    multipart(HttpMethod.PUT, path)
                            .file(getImagePngWithContent())
                            .param(PARTNER_DTO_TITLE_NAME, title);
        }

        mockMvc.perform(request).andExpect(status().isBadRequest()).andExpect(content().string(expected));
    }

    private static Stream<Arguments> parametersForUpdatePartnerBadRequestTest() {

        return Stream.of(
                Arguments.of(PARTNER_DTO_TITLE_VALUE_EMPTY, BAD_REQUEST_MESSAGE),
                Arguments.of(PARTNER_DTO_TITLE_BLANK, BAD_REQUEST_MESSAGE),
                Arguments.of(PARTNER_DTO_TITLE_NULL, BAD_REQUEST_MESSAGE),
                Arguments.of("null partner", BAD_REQUEST_MESSAGE),
                Arguments.of(PARTNER_DTO_TITLE_VALUE, BAD_REQUEST_VARIABLE_TYPE_MESSAGE)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForUpdatePartnerNotFoundTest")
    public void updatePartnerNotFoundTest(long id) throws Exception {
        String path = String.format("%s/%d", PARTNER_ENDPOINT, id);
        MockHttpServletRequestBuilder requestBuilder =
                multipart(HttpMethod.PUT, path)
                        .file(getImagePngWithContent())
                        .param(PARTNER_DTO_TITLE_NAME, PARTNER_DTO_TITLE_VALUE);
        MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();

        String body = response.getContentAsString();
        Assertions.assertEquals(response.getStatus(), RESPONSE_NOT_FOUND);
        Assertions.assertEquals(body, NOT_FOUND_MESSAGE);
    }

    private static Stream<Arguments> parametersForUpdatePartnerNotFoundTest() {

        return Stream.of(
                Arguments.of(0),
                Arguments.of(-99),
                Arguments.of(Long.MIN_VALUE),
                Arguments.of(Long.MAX_VALUE),
                Arguments.of(99)
        );
    }

    @Test
    public void getPartnerByIdSuccessTest() throws Exception {
        Partner partner = createPartner();
        String path = String.format("%s/%d", PARTNER_ENDPOINT, partner.id());

        mockMvc.perform(get(path)).andExpectAll(
                status().isOk(),
                jsonPath("$.id").value(partner.id()),
                jsonPath("$.title").value(partner.title()),
                jsonPath("$.link").value(partner.link()),
                jsonPath("$.imageUrl").value(partner.imageUrl())
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGetPartnerByIdNotFoundTest")
    public void getPartnerByIdNotFoundTest(long id) throws Exception {
        String path = String.format("%s/%d", PARTNER_ENDPOINT, id);

        mockMvc.perform(get(path)).andExpectAll(
                status().isNotFound(),
                content().string(NOT_FOUND_MESSAGE)
        );
    }

    private static Stream<Arguments> parametersForGetPartnerByIdNotFoundTest() {

        return Stream.of(
                Arguments.of(0),
                Arguments.of(-99),
                Arguments.of(Long.MIN_VALUE),
                Arguments.of(Long.MAX_VALUE),
                Arguments.of(99)
        );
    }

    @Test
    public void getPartnerByIdBadRequestTest() throws Exception {
        String pathString = String.format("%s/%s", PARTNER_ENDPOINT, "id");
        mockMvc.perform(get(pathString))
                .andExpectAll(status().isBadRequest(), content().string(BAD_REQUEST_VARIABLE_TYPE_MESSAGE));

        String pathFloat = String.format("%s/%s", PARTNER_ENDPOINT, 2.5);
        mockMvc.perform(get(pathFloat))
                .andExpectAll(status().isBadRequest(), content().string(BAD_REQUEST_VARIABLE_TYPE_MESSAGE));
    }

    @Test
    public void forbiddenRequestsTest() throws Exception {
        MockMvc mockMvcWithCsrf =
                MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        MockHttpServletRequestBuilder addPartnerRequest = multipart(PARTNER_ENDPOINT)
                .file(getImagePngWithContent())
                .param(PARTNER_DTO_TITLE_NAME, PARTNER_DTO_TITLE_VALUE);
        mockMvcWithCsrf.perform(addPartnerRequest)
                .andExpect(status().isForbidden())
                .andExpect(content().string(FORBIDDEN_MESSAGE));

        MockHttpServletRequestBuilder updatePartnerRequest = multipart(HttpMethod.PUT, PARTNER_ENDPOINT + "/1")
                .file(getImagePngWithContent())
                .param(PARTNER_DTO_TITLE_NAME, PARTNER_DTO_TITLE_VALUE);
        mockMvcWithCsrf.perform(updatePartnerRequest)
                .andExpect(status().isForbidden())
                .andExpect(content().string(FORBIDDEN_MESSAGE));

        MockHttpServletRequestBuilder deletePartnerRequest = multipart(HttpMethod.DELETE, PARTNER_ENDPOINT + "/1");
        mockMvcWithCsrf.perform(deletePartnerRequest)
                .andExpect(status().isForbidden())
                .andExpect(content().string(FORBIDDEN_MESSAGE));
    }

    private Partner createPartner() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = multipart(PARTNER_ENDPOINT)
                .param(PARTNER_DTO_TITLE_NAME, PARTNER_DTO_TITLE_VALUE)
                .param(PARTNER_DTO_LINK_NAME, PARTNER_DTO_LINK_VALUE);
        MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();
        String json = response.getContentAsString();
        return mapper.readValue(json, Partner.class);
    }

    private MockHttpServletRequestBuilder getRequest(MockMultipartFile file) {
        MockMultipartHttpServletRequestBuilder request = multipart(PARTNER_ENDPOINT);
        if (file != null) {
            request.file(file);
        }
        return request
                .param(PARTNER_DTO_TITLE_NAME, PARTNER_DTO_TITLE_VALUE)
                .param(PARTNER_DTO_LINK_NAME, PARTNER_DTO_LINK_VALUE);
    }
}
