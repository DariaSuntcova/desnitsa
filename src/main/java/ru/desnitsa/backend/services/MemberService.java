package ru.desnitsa.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.desnitsa.backend.dto.MemberDto;
import ru.desnitsa.backend.entities.Member;
import ru.desnitsa.backend.exceptions.IOBadRequestException;
import ru.desnitsa.backend.exceptions.MemberNotFoundException;
import ru.desnitsa.backend.repositories.MemberRepository;
import ru.desnitsa.backend.utils.ImageUtil;

import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    private final ImageUtil imageUtil;

    @Autowired
    public MemberService(MemberRepository memberRepository, ImageService imageService, ImageUtil imageUtil) {
        this.memberRepository = memberRepository;
        this.imageService = imageService;
        this.imageUtil = imageUtil;
    }

    private void checkMemberDto(MemberDto memberDto) {
        if (memberDto.firstName() == null || memberDto.lastName() == null || memberDto.speciality() == null) {
            throw new IOBadRequestException();
        }
    }

    public Member saveMember(MemberDto memberDto) {
        checkMemberDto(memberDto);
        String imageUrl = imageUtil.resizeAndSaveFile(memberDto.imageFile());
        return memberRepository.save(Member.of(memberDto.firstName(),
                memberDto.lastName(),
                memberDto.speciality(),
                memberDto.profession(),
                memberDto.description(),
                imageUrl));
    }

    public Iterable<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
    }

    public Member updateMember(Long id, MemberDto memberDto) {
        checkMemberDto(memberDto);
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);
        String imageUrl = imageUtil.resizeAndSaveFile(memberDto.imageFile());
        if (member.imageUrl() != null && imageUrl != null) {
            imageService.deleteImage(member.imageUrl().substring(imageUrl.lastIndexOf("/") + 1));
        } else if (member.imageUrl() != null && imageUrl == null) {
            imageUrl = member.imageUrl();
        }
        return memberRepository.save(new Member(member.id(),
                memberDto.lastName(),
                memberDto.firstName(),
                memberDto.speciality(),
                memberDto.profession(),
                memberDto.description(),
                imageUrl));
    }

    public void deleteMember(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            String imageUrl = member.get().imageUrl();
            if (imageUrl != null) {
                imageService.deleteImage(imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
            }
            memberRepository.deleteById(id);
        }
    }
}
