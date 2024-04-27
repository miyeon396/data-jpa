package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) { //위와 똑같음 스프링이 중간에서 컨버팅 과정을 다 끝내고 바로 인젝션 해주는거 (조회용으로만쓰세요) 근데 쓰는걸 권장하지는 않음
        return member.getUsername();
    }

        @PostConstruct
    public void init() {
        memberRepository.save(new Member("userA"));
    }
}
