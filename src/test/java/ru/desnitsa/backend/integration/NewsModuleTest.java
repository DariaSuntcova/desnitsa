package ru.desnitsa.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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
import ru.desnitsa.backend.entities.News;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class NewsModuleTest {
    private static final File IMAGE = new File("./src/test/resources/test_news.jpg");
    private static final String IMAGE_NAME = "test_news.jpg";
    private static final File IMAGE_2 = new File("./src/test/resources/test_news2.jpg");
    private static final String IMAGE_2_NAME = "test_news2.jpg";

    private static final String PREFIX_FILE_URL = "http://localhost:9090/image/";
    private static final String NEWS_ENDPOINT = "/news";
    private static final String NEWS_DTO_TITLE_NAME = "title";
    private static final String NEWS_DTO_TITLE_VALUE = "Test News";
    private static final String NEWS_DTO_DESCRIPTION_NAME = "description";
    private static final String NEWS_DTO_DESCRIPTION_VALUE = "Test News description";
    private static final String NEWS_DTO_DATA = "newsDate";
    private static final String NEWS_DTO_DATA_VALUE = "25.06.2020";
    private static final String NEWS_DTO_MAIN_IMAGE_CONTENT_NAME = "mainImageFile";
    private static final String NEWS_DTO_CONTENT_NAME = "imageList";

    private static final String NEWS_DTO_VIDEO_LINK = "videoLink";
    private static final String NEWS_DTO_VIDEO_LINK_VALUE = "videoLink test";
    private static final String VALUE_EMPTY = "";
    private static final String VALUE_BLANK = "    ";
    private static final String VALUE_NULL = null;
    private static final String BAD_REQUEST_NO_TITLE_MESSAGE = "Заголовок не может быть пустым";
    private static final String BAD_REQUEST_NO_DESCRIPTION_MESSAGE = "Описание не может быть пустым";
    private static final String BAD_REQUEST_NO_DATA_MESSAGE = "Дата не может быть пустой";
    private static final String BAD_REQUEST_VARIABLE_TYPE_MESSAGE = "";
    private static final String FORBIDDEN_MESSAGE = "Доступ к контенту запрещен";
    private static final String NOT_FOUND_MESSAGE = "Контент не найден.";
    private static final String UNSUPPORTED_MEDIA_TYPE_MESSAGE = "Один или несколько файлов не являются изображением gif, jpg, png, bmp";

    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    private static MockMultipartFile getImage(String filename, String originalFilename, File image) throws IOException {
        return new MockMultipartFile(
                filename,
                originalFilename,
                MediaType.IMAGE_JPEG_VALUE,
                new FileInputStream(image)
        );
    }

    private static final MockMultipartFile NOT_IMAGE_FILE_WITH_CONTENT = new MockMultipartFile(
            NEWS_DTO_MAIN_IMAGE_CONTENT_NAME,
            "testFile.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Test file content".getBytes()
    );

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

    @BeforeAll
    public static void initSuite() {
        POSTGRES
                .withReuse(true)
                .withDatabaseName("postgres")
                .withUsername("test")
                .withPassword("test")
                .start();
    }

    @BeforeEach
    public void initTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    public void finalizeTest() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void addNewsWithInvalidFileTest() throws Exception {
        MockHttpServletRequestBuilder request = multipart(NEWS_ENDPOINT)
                .file(NOT_IMAGE_FILE_WITH_CONTENT)
                .param(NEWS_DTO_TITLE_NAME, NEWS_DTO_TITLE_VALUE)
                .param(NEWS_DTO_DESCRIPTION_NAME, NEWS_DTO_DESCRIPTION_VALUE)
                .param(NEWS_DTO_DATA, NEWS_DTO_DATA_VALUE);
        mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(status().isUnsupportedMediaType(), content().string(UNSUPPORTED_MEDIA_TYPE_MESSAGE));
    }

    @ParameterizedTest
    @MethodSource("argumentsForAddNewsSuccessTest")
    void addNewsSuccessTest(
            MockMultipartFile mainFile,
            MockMultipartFile file2,
            String title,
            String description,
            String videoLink
    ) throws Exception {
        MockMultipartHttpServletRequestBuilder requestBuilder = multipart(NEWS_ENDPOINT)
                .file(mainFile);
        if (file2 != null) {
            requestBuilder.file(file2);
        }
        MockHttpServletRequestBuilder request = requestBuilder
                .param(NEWS_DTO_TITLE_NAME, title)
                .param(NEWS_DTO_DESCRIPTION_NAME, description)
                .param(NEWS_DTO_DATA, NEWS_DTO_DATA_VALUE)
                .param(NEWS_DTO_VIDEO_LINK, videoLink);

        String response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        News news = mapper.readValue(response, News.class);

        assertEquals(news.title(), title);
        assertEquals(news.description(), description);
        assertNotNull(news.newsDate());
        assertTrue(news.mainImageUrl().startsWith(PREFIX_FILE_URL));

        if (file2 == null) {
            assertTrue(news.imageUrlList().isEmpty());
        } else {
            assertNotNull(news.imageUrlList());
            assertTrue(news.imageUrlList().spliterator().tryAdvance(imageUrls ->
                    imageUrls.imageUrl().startsWith(PREFIX_FILE_URL)));
        }

    }

    private static Stream<Arguments> argumentsForAddNewsSuccessTest() throws IOException {
        return Stream.of(
                Arguments.of(
                        getImage(NEWS_DTO_MAIN_IMAGE_CONTENT_NAME, IMAGE_NAME, IMAGE),
                        getImage(NEWS_DTO_CONTENT_NAME, IMAGE_2_NAME, IMAGE_2),
                        NEWS_DTO_TITLE_VALUE,
                        NEWS_DTO_DESCRIPTION_VALUE,
                        NEWS_DTO_VIDEO_LINK_VALUE),
                Arguments.of(
                        getImage(NEWS_DTO_MAIN_IMAGE_CONTENT_NAME, IMAGE_NAME, IMAGE),
                        null,
                        NEWS_DTO_TITLE_VALUE,
                        NEWS_DTO_DESCRIPTION_VALUE,
                        NEWS_DTO_VIDEO_LINK_VALUE),
                Arguments.of(
                        getImage(NEWS_DTO_MAIN_IMAGE_CONTENT_NAME, IMAGE_NAME, IMAGE),
                        null,
                        NEWS_DTO_TITLE_VALUE,
                        NEWS_DTO_DESCRIPTION_VALUE,
                        NEWS_DTO_VIDEO_LINK_VALUE)
        );
    }

    @Test
    void addNewsNoVideoLinkSuccessTest() throws Exception {

        MockHttpServletRequestBuilder request = multipart(NEWS_ENDPOINT)
                .file(getImage(NEWS_DTO_MAIN_IMAGE_CONTENT_NAME, IMAGE_NAME, IMAGE))
                .param(NEWS_DTO_TITLE_NAME, NEWS_DTO_TITLE_VALUE)
                .param(NEWS_DTO_DESCRIPTION_NAME, NEWS_DTO_DESCRIPTION_VALUE)
                .param(NEWS_DTO_DATA, NEWS_DTO_DATA_VALUE);

        String response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        News news = mapper.readValue(response, News.class);

        assertEquals(news.title(), NEWS_DTO_TITLE_VALUE);
        assertEquals(news.description(), NEWS_DTO_DESCRIPTION_VALUE);
        assertNotNull(news.newsDate());
        assertTrue(news.mainImageUrl().startsWith(PREFIX_FILE_URL));
    }

    @ParameterizedTest
    @MethodSource("parametersForAddNewsBadRequestTest")
    public void addNewsBadRequestTest(
            String title,
            String description,
            String date,
            String expMsg) throws Exception {
        MockHttpServletRequestBuilder request = multipart(NEWS_ENDPOINT)
                .file(getImage(NEWS_DTO_MAIN_IMAGE_CONTENT_NAME, IMAGE_NAME, IMAGE))
                .param(NEWS_DTO_TITLE_NAME, title)
                .param(NEWS_DTO_DESCRIPTION_NAME, description)
                .param(NEWS_DTO_DATA, date);


        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expMsg));
    }

    private static Stream<Arguments> parametersForAddNewsBadRequestTest() {
        return Stream.of(
                Arguments.of(
                        VALUE_EMPTY,
                        NEWS_DTO_DESCRIPTION_VALUE,
                        NEWS_DTO_DATA_VALUE,
                        BAD_REQUEST_NO_TITLE_MESSAGE),
                Arguments.of(
                        VALUE_BLANK,
                        NEWS_DTO_DESCRIPTION_VALUE,
                        NEWS_DTO_DATA_VALUE,
                        BAD_REQUEST_NO_TITLE_MESSAGE),
                Arguments.of(
                        VALUE_NULL,
                        NEWS_DTO_DESCRIPTION_VALUE,
                        NEWS_DTO_DATA_VALUE,
                        BAD_REQUEST_NO_TITLE_MESSAGE),
                Arguments.of(
                        NEWS_DTO_TITLE_VALUE,
                        VALUE_EMPTY,
                        NEWS_DTO_DATA_VALUE,
                        BAD_REQUEST_NO_DESCRIPTION_MESSAGE
                ),
                Arguments.of(
                        NEWS_DTO_TITLE_VALUE,
                        VALUE_BLANK,
                        NEWS_DTO_DATA_VALUE,
                        BAD_REQUEST_NO_DESCRIPTION_MESSAGE
                ),
                Arguments.of(
                        NEWS_DTO_TITLE_VALUE,
                        VALUE_NULL,
                        NEWS_DTO_DATA_VALUE,
                        BAD_REQUEST_NO_DESCRIPTION_MESSAGE
                ),
                Arguments.of(
                        NEWS_DTO_TITLE_VALUE,
                        NEWS_DTO_DESCRIPTION_VALUE,
                        VALUE_NULL,
                        BAD_REQUEST_NO_DATA_MESSAGE
                )

        );
    }

    @Test
    void getAllNewsSuccessTest() throws Exception {
        News newsOne = createNews();
        News newsTwo = createNews();

        MockMvc mockMvcWithCsrf =
                MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        String response = mockMvcWithCsrf.perform(get(NEWS_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, News.class);
        List<News> newsList = mapper.readValue(response, collectionType);

        assertNotNull(newsList);
        assertFalse(newsList.isEmpty());
        assertEquals(newsList.size(), 2);

        assertEquals(newsList.get(0).id(), newsOne.id());
        assertEquals(newsList.get(0).title(), NEWS_DTO_TITLE_VALUE);
        assertEquals(newsList.get(0).description(), NEWS_DTO_DESCRIPTION_VALUE);
        assertNotNull(newsList.get(0).mainImageUrl());
        assertTrue(newsList.get(0).mainImageUrl().startsWith(PREFIX_FILE_URL));
        assertTrue(newsList.get(0).imageUrlList().isEmpty());

        assertEquals(newsList.get(1).id(), newsTwo.id());
        assertEquals(newsList.get(1).title(), NEWS_DTO_TITLE_VALUE);
        assertEquals(newsList.get(1).description(), NEWS_DTO_DESCRIPTION_VALUE);
        assertNotNull(newsList.get(1).mainImageUrl());
        assertTrue(newsList.get(1).mainImageUrl().startsWith(PREFIX_FILE_URL));
        assertTrue(newsList.get(0).imageUrlList().isEmpty());
    }

    @Test
    void getNewsByIdBadRequestTest() throws Exception {
        String pathString = String.format("%s/%s", NEWS_ENDPOINT, "id");
        mockMvc.perform(get(pathString))
                .andExpectAll(status().isBadRequest(), content().string(BAD_REQUEST_VARIABLE_TYPE_MESSAGE));

        String pathFloat = String.format("%s/%s", NEWS_ENDPOINT, 2.5);
        mockMvc.perform(get(pathFloat))
                .andExpectAll(status().isBadRequest(), content().string(BAD_REQUEST_VARIABLE_TYPE_MESSAGE));
    }

    @ParameterizedTest
    @MethodSource("argumentsForgetNewsByIdNotFoundTest")
    void getNewsByIdNotFoundTest(long id) throws Exception {
        String path = String.format("%s/%s", NEWS_ENDPOINT, id);
        mockMvc.perform(get(path))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_FOUND_MESSAGE));

    }

    private static Stream<Arguments> argumentsForgetNewsByIdNotFoundTest() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(Long.MIN_VALUE),
                Arguments.of(Long.MAX_VALUE)
        );
    }

    @Test
    void getNewsByIdSuccessTest() throws Exception {
        News news = createNews();

        MockMvc mockMvcWithCsrf =
                MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        String path = String.format("%s/%s", NEWS_ENDPOINT, news.id());
        mockMvcWithCsrf.perform(get(path))
                .andExpectAll(status().isOk(),
                        jsonPath("$.id").value(news.id()),
                        jsonPath("$.title").value(news.title()),
                        jsonPath("$.description").value(news.description()),
                        jsonPath("$.mainImageUrl").value(news.mainImageUrl()));

    }

    @Test
    void updateNewsNotFoundTest() throws Exception {
        String path = String.format("%s/%s", NEWS_ENDPOINT, 1000);
        mockMvc.perform(put(path))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_FOUND_MESSAGE));
    }

    @Test
    void updateNewsSuccessTest() throws Exception {
        News news = createNews();
        String path = String.format("%s/%s", NEWS_ENDPOINT, news.id());
        String title = "Новый заголовок";
        String description = "Новое описание";
        String data = sdf.format(new Date());

        MockHttpServletRequestBuilder requestBuilder =
                multipart(HttpMethod.PUT, path)
                        .file(getImage(NEWS_DTO_MAIN_IMAGE_CONTENT_NAME, IMAGE_2_NAME, IMAGE))
                        .file(getImage("imagesToAdd", IMAGE_NAME, IMAGE_2))
                        .param(NEWS_DTO_TITLE_NAME, title)
                        .param(NEWS_DTO_DESCRIPTION_NAME, description)
                        .param(NEWS_DTO_DATA, data);

        String response = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        News updatedNews = mapper.readValue(response, News.class);

        assertEquals(updatedNews.title(), title);
        assertEquals(updatedNews.description(), description);
        assertEquals(sdf.format(updatedNews.newsDate()), data);
        assertNotNull(updatedNews.mainImageUrl());
        assertTrue(updatedNews.mainImageUrl().startsWith(PREFIX_FILE_URL));
        assertNotNull(updatedNews.imageUrlList());
        assertEquals(1, updatedNews.imageUrlList().size());
    }

    @Test
    void updateNewsTestDeletingImage() throws Exception {
        MockHttpServletRequestBuilder request = multipart(NEWS_ENDPOINT)
                .file(getImage(NEWS_DTO_MAIN_IMAGE_CONTENT_NAME, IMAGE_NAME, IMAGE))
                .file(getImage(NEWS_DTO_CONTENT_NAME, IMAGE_2_NAME, IMAGE_2))
                .file(getImage(NEWS_DTO_CONTENT_NAME, IMAGE_NAME, IMAGE))
                .param(NEWS_DTO_TITLE_NAME, NEWS_DTO_TITLE_VALUE)
                .param(NEWS_DTO_DESCRIPTION_NAME, NEWS_DTO_DESCRIPTION_VALUE)
                .param(NEWS_DTO_DATA, NEWS_DTO_DATA_VALUE)
                .param(NEWS_DTO_VIDEO_LINK, NEWS_DTO_VIDEO_LINK_VALUE);

        String response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        News news = mapper.readValue(response, News.class);
        List<String> imageUrls = new ArrayList<>();
        news.imageUrlList().forEach(image -> imageUrls.add(image.imageUrl()));

        String path = String.format("%s/%s", NEWS_ENDPOINT, news.id());

        MockHttpServletRequestBuilder updateRequest = put(path)
                .param("imagesToDelete", imageUrls.get(0))
                .param("imagesToDelete", imageUrls.get(1))
                .param(NEWS_DTO_TITLE_NAME, NEWS_DTO_TITLE_VALUE)
                .param(NEWS_DTO_DESCRIPTION_NAME, NEWS_DTO_DESCRIPTION_VALUE)
                .param(NEWS_DTO_DATA, NEWS_DTO_DATA_VALUE);

        String updateResponse = mockMvc.perform(updateRequest)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        News updatedNews = mapper.readValue(updateResponse, News.class);

        assertTrue(updatedNews.imageUrlList().isEmpty());
    }

    @Test
    void deleteNewsSuccessTest() throws Exception {
        News news = createNews();
        String path = String.format("%s/%s", NEWS_ENDPOINT, news.id());

        MockHttpServletRequestBuilder request = delete(path);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void checkForbiddenTest() throws Exception {
        MockMvc mockMvcWithCsrf =
                MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();

        MockHttpServletRequestBuilder addNewsRequest = multipart(NEWS_ENDPOINT)
                .file(getImage(NEWS_DTO_MAIN_IMAGE_CONTENT_NAME, IMAGE_NAME, IMAGE))
                .param(NEWS_DTO_TITLE_NAME, NEWS_DTO_TITLE_VALUE)
                .param(NEWS_DTO_DESCRIPTION_NAME, NEWS_DTO_DESCRIPTION_VALUE)
                .param(NEWS_DTO_DATA, NEWS_DTO_DATA_VALUE);
        mockMvcWithCsrf.perform(addNewsRequest)
                .andExpect(status().isForbidden())
                .andExpect(content().string(FORBIDDEN_MESSAGE));

        MockHttpServletRequestBuilder updateNewsRequest = multipart(HttpMethod.PUT, NEWS_ENDPOINT + "/1")
                .file(getImage(NEWS_DTO_MAIN_IMAGE_CONTENT_NAME, IMAGE_NAME, IMAGE))
                .param(NEWS_DTO_TITLE_NAME, NEWS_DTO_TITLE_VALUE)
                .param(NEWS_DTO_DESCRIPTION_NAME, NEWS_DTO_DESCRIPTION_VALUE)
                .param(NEWS_DTO_DATA, NEWS_DTO_DATA_VALUE);
        mockMvcWithCsrf.perform(updateNewsRequest)
                .andExpect(status().isForbidden())
                .andExpect(content().string(FORBIDDEN_MESSAGE));

        MockHttpServletRequestBuilder deleteNewsRequest = multipart(HttpMethod.DELETE, NEWS_ENDPOINT + "/1");
        mockMvcWithCsrf.perform(deleteNewsRequest)
                .andExpect(status().isForbidden())
                .andExpect(content().string(FORBIDDEN_MESSAGE));
    }


    private News createNews() throws Exception {
        MockHttpServletRequestBuilder request = multipart(NEWS_ENDPOINT)
                .file(getImage(NEWS_DTO_MAIN_IMAGE_CONTENT_NAME, IMAGE_NAME, IMAGE))
                .param(NEWS_DTO_TITLE_NAME, NEWS_DTO_TITLE_VALUE)
                .param(NEWS_DTO_DESCRIPTION_NAME, NEWS_DTO_DESCRIPTION_VALUE)
                .param(NEWS_DTO_DATA, NEWS_DTO_DATA_VALUE);
        String response = mockMvc.perform(request)
                .andReturn().getResponse().getContentAsString();
        return mapper.readValue(response, News.class);
    }


}