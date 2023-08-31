package ru.desnitsa.backend.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import ru.desnitsa.backend.dto.MemberDto;
import ru.desnitsa.backend.entities.Member;
import ru.desnitsa.backend.services.MemberService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static reactor.core.publisher.Mono.when;

class MemberControllerTest {
    private static final File IMAGE = new File("./src/test/resources/test_member.jpg");
    private static final String CONTENT_NAME = "imageFile";

    private static MockMultipartFile getImage() throws IOException {
        return new MockMultipartFile(
                CONTENT_NAME,
                "test_member.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new FileInputStream(IMAGE)
        );
    }

    private MemberController memberController;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = Mockito.mock(MemberService.class);
        memberController = new MemberController(memberService);
    }

    @Test
    void getAllMembers() {
    }

    @Test
    void getMemberById() {
    }

    @Test
    void saveMember() throws IOException {
        String firstName = "Ivan";
        String lastName = "Ivanov";
        String speciality = "Employee";
        String profession = "Doctor";
        String description = "Human";
        MultipartFile imageFile = getImage();
        MemberDto memberDto = new MemberDto(firstName, lastName, speciality, profession, description, imageFile);
        Member member = null;
        Mockito.when(memberService.saveMember(memberDto)).thenReturn(member);

        Member actual = memberController.saveMember(memberDto);

        Mockito.verify(memberService, Mockito.times(1)).saveMember(memberDto);
    }

    @Test
    void updateMember() {
    }

    @Test
    void deleteMember() {
    }
}