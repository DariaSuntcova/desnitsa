package ru.desnitsa.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.desnitsa.backend.dto.MemberDto;
import ru.desnitsa.backend.entities.Member;
import ru.desnitsa.backend.services.MemberService;

@RestController
@RequestMapping("/team")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public Iterable<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("{id}")
    public Member getMemberById(@PathVariable Long id) {
        return memberService.getMemberById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Member saveMember(@ModelAttribute MemberDto memberDto) {
        return memberService.saveMember(memberDto);
    }

    @PutMapping("{id}")
    public Member updateMember(@PathVariable Long id,
                               @ModelAttribute MemberDto memberDto) {
        return memberService.updateMember(id, memberDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
    }
}
