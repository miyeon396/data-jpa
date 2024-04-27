package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
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

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        //페이지를 반환헀지만 엔티티를 그대로 외부에 노출하며 안된다. 스펙 외부 노출 + 엔티티 변하는 순간 스펙이 변경되버림 -> dto로 바꿔서 내보내기 아래와 같이 ***
//        Page<MemberDto> map = page.map(member -> new MemberDto(member));
        Page<MemberDto> map = page.map(MemberDto::new);
        return map;
    }

    @PostConstruct //스프링 올라 올 때 한번 실행이 되는 것
    public void init() {
        for (int i=0; i< 100; i++) {
            memberRepository.save(new Member("user"+i, i));
        }

    }
}
