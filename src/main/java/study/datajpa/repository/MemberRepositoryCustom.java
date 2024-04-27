package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom { //스프링 데이터 JPA가 아니라 직접 구현한 기능을 쓰고 싶은 것
    List<Member> findMemberCustom();
}
