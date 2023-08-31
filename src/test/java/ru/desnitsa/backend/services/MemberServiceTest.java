package ru.desnitsa.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.desnitsa.backend.dto.MemberDto;
import ru.desnitsa.backend.entities.Member;
import ru.desnitsa.backend.exceptions.IOBadRequestException;
import ru.desnitsa.backend.exceptions.MemberNotFoundException;
import ru.desnitsa.backend.repositories.MemberRepository;
import ru.desnitsa.backend.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ImageService imageService;
    @Mock
    private ImageUtil imageUtil;
    @InjectMocks
    private MemberService memberService;


    private MemberDto memberDto;
    private Member member1;
    private Member member2;

    @BeforeEach
    void setUp() {
        member1 = new Member(1L, "Anna", "Ivanova", "Volunteer", "helper", "none", null);
        member2 = new Member(1L, "Ivan", "Frolov", "Volunteer", "helper", "none", null);
        memberDto = new MemberDto("Anna", "Ivanova", "Volunteer", "helper", "none", null);
    }

    @Test
    void saveMember_validData_memberSaved() {
        when(memberRepository.save(any(Member.class))).thenReturn(member1);
        Member savedMember = memberService.saveMember(memberDto);

        assertEquals(member1, savedMember);
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void saveMember_invalidData_exceptionThrown() {
        memberDto = new MemberDto(null, "Ivanova", "Volunteer", "helper", "none", null);

        assertThrows(IOBadRequestException.class, () -> memberService.saveMember(memberDto));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void getAllMembers_membersExist_returnMembers() {
        List<Member> members = new ArrayList<>();
        members.add(member1);
        members.add(member2);

        when(memberRepository.findAll()).thenReturn(members);

        Iterable<Member> allMembers = memberService.getAllMembers();

        assertNotNull(allMembers);
        assertEquals(members, allMembers);
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    void getMemberById_memberExists_returnMember() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member1));

        Member foundMember = memberService.getMemberById(1L);

        assertNotNull(foundMember);
        assertEquals(member1, foundMember);
        verify(memberRepository, times(1)).findById(anyLong());
    }

    @Test
    void getMemberById_memberDoesNotExist_exceptionThrown() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(1L));
        verify(memberRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateMember_validData_withoutPhoto() {
        MemberDto updatedMemberDto = new MemberDto("Anna", "Ivanova", "Volunteer", "helper", "none", null);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member1));
        when(memberRepository.save(any(Member.class))).thenReturn(member1);

        Member updatedMember = memberService.updateMember(1L, updatedMemberDto);

        assertNotNull(updatedMember);
        assertEquals(member1.id(), updatedMember.id());
        assertEquals(updatedMemberDto.firstName(), updatedMember.firstName());
        assertEquals(updatedMemberDto.lastName(), updatedMember.lastName());
        assertEquals(updatedMemberDto.speciality(), updatedMember.speciality());
        assertEquals(updatedMemberDto.profession(), updatedMember.profession());
        assertEquals(updatedMemberDto.description(), updatedMember.description());
        assertNull(updatedMember.imageUrl());
        verify(memberRepository, times(1)).findById(anyLong());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMember_validData_noNewPhoto() {
        member1 = new Member(1L, "Anna", "Ivanova", "Volunteer", "helper", "none", "image.jpg");
        MemberDto updatedMemberDto = new MemberDto("Anna", "Ivanova", "Volunteer", "helper", "none", null);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member1));
        when(memberRepository.save(any(Member.class))).thenReturn(member1);

        Member updatedMember = memberService.updateMember(1L, updatedMemberDto);

        assertNotNull(updatedMember);
        assertEquals(member1.id(), updatedMember.id());
        assertEquals(updatedMemberDto.firstName(), updatedMember.firstName());
        assertEquals(updatedMemberDto.lastName(), updatedMember.lastName());
        assertEquals(updatedMemberDto.speciality(), updatedMember.speciality());
        assertEquals(updatedMemberDto.profession(), updatedMember.profession());
        assertEquals(updatedMemberDto.description(), updatedMember.description());
        assertEquals(member1.imageUrl(), updatedMember.imageUrl());
        verify(memberRepository, times(1)).findById(anyLong());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMember_invalidData_exceptionThrown() {
        MemberDto updatedMemberDto = new MemberDto(null, "Ivanova", "Volunteer", "helper", "none", null);

        assertThrows(IOBadRequestException.class, () -> memberService.updateMember(1L, updatedMemberDto));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void deleteMember_memberExists_memberDeleted() {
        member1 = new Member(1L, "Anna", "Ivanova", "Volunteer", "helper", "none", "image.jpg");
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member1));

        memberService.deleteMember(1L);

        verify(imageService, times(1)).deleteImage(member1.imageUrl());
        verify(memberRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteMember_memberDoesNotExist_nothingDeleted() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        memberService.deleteMember(1L);

        verify(imageService, never()).deleteImage(anyString());
        verify(memberRepository, never()).deleteById(anyLong());
    }
}