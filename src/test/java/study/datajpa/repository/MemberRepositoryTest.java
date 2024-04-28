package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findHelloBy();
        List<Member> helloTop3By = memberRepository.findTop3HelloBy();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");

        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);


        List<MemberDto> memberDtos = memberRepository.findMemberDto();
        for (MemberDto memberDto : memberDtos) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(List.of("AAA", "BBB"));
        for (Member m : result) {
            System.out.println("m = " + m);
            
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA"); //이거는 데이터가 없을 때 Empty를 반환함
        Member aaa1 = memberRepository.findMemberByUsername("AAA"); //이거는 데이터가 없으면 null 임
        Optional<Member> aaa2 = memberRepository.findOptionalByUsername("AAA"); //데이터가 있을지 없을지 모르면 걍 옵셔널을써!
    }

    @Test
    public void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null)); // 얘는 외부 반환해도 됨 (페이지 유지하면서 Dto로 변환 가능 )

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    public void paging_slice() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5); //slice에 없는 기능
        assertThat(page.getNumber()).isEqualTo(0);
//        assertThat(page.getTotalPages()).isEqualTo(2); //slice에 없는 기능
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    public void paging_list() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        List<Member> page = memberRepository.findListByAge(age, pageRequest);

        System.out.println(page);


    }

    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20);

//        // 벌크연산은 바로 쿼리를 날려버리기때문에 영속성 컨텍스트를 무시해버 린다.
//        //벌크 연산 이후에는 가급적 영속성 컨텍스트 날려야한다. 그렇게 되면 아래에서 다시 디비에서 조회해오기때문에 데이터 정합성이 맞다.
//
//        entityManager.flush();
//        entityManager.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {

        //given
        //member1 -> teamA 참조
        //member2 -> teamB 참조
        //멤버와 팀은 다대일 레이지로 되어있음. 멤버를 조회할 때 팀을 조회 하지 않음. 가짜 객체로 만들어놓고 실제로 팀을 쓸 때 조회함 (=지연로딩)

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();

        // N + 1 -> 쿼리를 한번 날렸는데 결과가 2개가 나옴 그럼 2번 쿼리를 더 돌리는 것
        //select Member 얘가 1
        List<Member> members = memberRepository.findAll();

        //보통 JPA에선 패치 조인으로 해결 .
        for(Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName()); //select Team
        }
    }

    @Test
    public void findMemberLazy_fetchJoin() {

        //given
        //member1 -> teamA 참조
        //member2 -> teamB 참조
        //멤버와 팀은 다대일 레이지로 되어있음. 멤버를 조회할 때 팀을 조회 하지 않음. 가짜 객체로 만들어놓고 실제로 팀을 쓸 때 조회함 (=지연로딩)

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();

        //    @Query("select m from Member m left join fetch m.team")//페치 조인이란걸 하면 멤버를 조회할 떄 연관된 팀을 한번에 같이 가져옵니다.
        //    List<Member> findMemberFetchJoin();
        List<Member> members = memberRepository.findMemberFetchJoin(); //연관관계 있는걸 데이터 베이스 조인을 이용하여 한방에 가져온다.

        //보통 JPA에선 패치 조인으로 해결 .
        for(Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass()); //패치조인하니 순수한 팀 엔티티 객체 끌고오네
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName()); //select Team
        }
    }

    @Test
    public void findMemberEntityGraph() {

        //given
        //member1 -> teamA 참조
        //member2 -> teamB 참조
        //멤버와 팀은 다대일 레이지로 되어있음. 멤버를 조회할 때 팀을 조회 하지 않음. 가짜 객체로 만들어놓고 실제로 팀을 쓸 때 조회함 (=지연로딩)

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();

        List<Member> members = memberRepository.findAll();

        for(Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass()); //패치조인하니 순수한 팀 엔티티 객체 끌고오네
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName()); //select Team
        }
    }

    @Test
    public void findMemberEntityGraphWithJPQL() {

        //given
        //member1 -> teamA 참조
        //member2 -> teamB 참조
        //멤버와 팀은 다대일 레이지로 되어있음. 멤버를 조회할 때 팀을 조회 하지 않음. 가짜 객체로 만들어놓고 실제로 팀을 쓸 때 조회함 (=지연로딩)

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        //영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();

        List<Member> members = memberRepository.findMemberEntityGraph();

        for(Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass()); //패치조인하니 순수한 팀 엔티티 객체 끌고오네
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName()); //select Team
        }
    }


    @Test
    public void queryHintNotUse() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        entityManager.flush();
        entityManager.clear();

        Member findMember = memberRepository.findById(member1.getId()).get();
        findMember.setUsername("member2");

        entityManager.flush(); //단점 변경감지를 하려면 데이터를 2개를 갖고있어야햄
    }

    @Test
    public void queryHintUse() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        entityManager.flush();
        entityManager.clear();

        Member findMember = memberRepository.findReadOnlyByUsername("member1");//변경감지 안하고 readonly만 함
        findMember.setUsername("member2");

        entityManager.flush();
    }


    @Test
    public void lock() {
        Member member1 = memberRepository.save(new Member("member1", 10));
        entityManager.flush();
        entityManager.clear();


        List<Member> result = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void projections_interface() {
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        entityManager.persist(m1);
        entityManager.persist(m2);

        entityManager.flush();
        entityManager.clear();

        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnly usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
        }

    }

    @Test
    public void nativeQuery() {
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        entityManager.persist(m1);
        entityManager.persist(m2);

        entityManager.flush();
        entityManager.clear();

//        Member result = memberRepository.findByNativeQuery("m1");
//        System.out.println("result = " + result);

        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        }


    }

}